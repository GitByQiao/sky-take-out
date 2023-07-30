package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.AddressBookService;
import com.sky.service.OrderService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.*;
import com.sky.webSocket.WebSocketServer;
import org.apache.tomcat.websocket.AsyncChannelWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private ShoppingMapper shoppingMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WebSocketServer webSocketServer;
    @Autowired
    private WeChatPayUtil weChatPayUtil;

    /**
     * 用户订单下单
     *
     * @param ordersSubmitDTO
     * @return
     */
    @Override
    @Transactional
    public OrderSubmitVO postOrderSubmit(OrdersSubmitDTO ordersSubmitDTO) {
        Long addressBookId = ordersSubmitDTO.getAddressBookId();
        //判断是否地址是否为空
        //查询地址䈬
        AddressBook addressBook = addressBookMapper.getById(addressBookId);
        //址䈬为空
        if (addressBook == null) {
            //抛出异常,将错误信息抛给前端进行提示终止此程序运行
            throw new AddressBookBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }
        //址䈬不为空
        Long userId = BaseContext.getCurrentId();
        //获得当前用户的购物车
        List<ShoppingCart> shoppingCarts = shoppingMapper.getByUserIdShoppingCart(userId);
        //判断购物车是否为空
        //购物车为空
        if (shoppingCarts == null || shoppingCarts.size() == 0) {
            //抛出异常,将错误信息抛给前端进行提示终止此程序运行
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }
        //购物车不为空
        //插入一条订单表数据
        Orders order = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, order);
        //待付款
        order.setStatus(Orders.PENDING_PAYMENT);
        order.setNumber(String.valueOf(System.currentTimeMillis()));
        order.setUserId(userId);
        order.setOrderTime(LocalDateTime.now());
        order.setPayStatus(Orders.UN_PAID);
        //地址
        order.setAddress(addressBook.getProvinceName()
                + addressBook.getCityName()
                + addressBook.getDistrictName()
                + addressBook.getDetail());
        order.setPhone(addressBook.getPhone());
        //订单上用户名字
        order.setConsignee(addressBook.getConsignee());
        //System.out.println("我的order是什么：" + order);
        //返回插入的id主键值
        orderMapper.postOrder(order);
        //插入N条订单明细表数据
        List<OrderDetail> orderDetails = new ArrayList<>();
        //根据购物车数量插入订单明细数据
        for (ShoppingCart shoppingCart : shoppingCarts) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(shoppingCart, orderDetail);
            orderDetail.setOrderId(order.getId());
            orderDetails.add(orderDetail);
        }
        orderDetailMapper.postOrderDetails(orderDetails);
        //清空购物车
        shoppingMapper.deleteAllByUserIdShoppingCart(userId);
        //封装返回数据
        return OrderSubmitVO.builder()
                .id(order.getId())
                .orderAmount(order.getAmount())
                .orderNumber(order.getNumber())
                .orderTime(order.getOrderTime())
                .build();
    }

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getByOpenId(String.valueOf(userId));

        //调用微信支付接口，生成预支付交易单
        JSONObject jsonObject = weChatPayUtil.pay(
                ordersPaymentDTO.getOrderNumber(), //商户订单号
                new BigDecimal(0.01), //支付金额，单位 元
                "苍穹外卖订单", //商品描述
                user.getOpenid() //微信用户的openid
        );

        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
            throw new OrderBusinessException("该订单已支付");
        }

        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));
        return vo;
    }


    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    @Transactional
    public void paySuccess(String outTradeNo) {

        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumberOrder(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();
        orderMapper.update(orders);
        //支付成功向浏览器推送播报来单了
        Map<String, Object> map = new HashMap<>();
        map.put("type", 1);
        map.put("orderId", ordersDB.getId());
        map.put("content", "订单号：" + outTradeNo);
        String jsonString = JSON.toJSONString(map);
        webSocketServer.sendToAllClient(jsonString);
    }

    /**
     * 查询历史订单
     *
     * @param ordersPageQueryDTO
     * @return
     */
    @Transactional
    @Override
    public PageResult getHistoryOrders(OrdersPageQueryDTO ordersPageQueryDTO) {
        int page = ordersPageQueryDTO.getPage();
        int pageSize = ordersPageQueryDTO.getPageSize();
        //用户id
        Long userId = BaseContext.getCurrentId();
        ordersPageQueryDTO.setUserId(userId);
        //设置订单状态查询
        ordersPageQueryDTO.setStatus(ordersPageQueryDTO.getStatus());
        //分页查找
        PageHelper.startPage(page, pageSize);
        //获得订单表历史查找
        Page<Orders> ordersPage = orderMapper.getHistoryOrders(ordersPageQueryDTO);
        //订单数
        long total = ordersPage.getTotal();
        //订单详情
        List<Orders> ordersPageResult = ordersPage.getResult();
        //详细返回
        List<OrderVO> orderVOList = new ArrayList<>();
        if (ordersPageResult != null && ordersPageResult.size() > 0) {
            for (Orders orders : ordersPageResult) {
                //订单id
                Long ordersId = orders.getId();
                //通过订单id查询订单详细表
                List<OrderDetail> orderDetailList = orderDetailMapper.getOrderDetailsById(ordersId);
                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(orders, orderVO);
                orderVO.setOrderDetailList(orderDetailList);
                orderVOList.add(orderVO);
            }
        }
        return new PageResult(total, orderVOList);
    }

    /**
     * 查询订单详情
     *
     * @param id
     * @return
     */
    @Override
    @Transactional
    public OrderVO getOrderDetails(Long id) {
        //Long userId = BaseContext.getCurrentId();
        //查询订单id下的订单详细表
        List<OrderDetail> orderDetailList = orderDetailMapper.getOrderDetailsById(id);
        //通过订单id查询订单
        Orders orders = orderMapper.getByIdOrder(id);
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders, orderVO);
        orderVO.setOrderDetailList(orderDetailList);
        return orderVO;
    }

    /**
     * 用户取消订单
     *
     * @param ordersRejectionDTO
     */
    @Transactional
    @Override
    public void cancelByIdOrder(OrdersRejectionDTO ordersRejectionDTO) {
        Orders order = orderMapper.getByIdOrder(ordersRejectionDTO.getId());
        if (order == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        //获得订单状态
        Integer orderStatus = order.getStatus();
        if (orderStatus == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        if (orderStatus == 1) {
            //待付款将状态修改为已取消
            order.setStatus(Orders.CANCELLED);
        } else if (orderStatus == 2) {
            //待接单将状态修改为已取消
            order.setStatus(Orders.CANCELLED);
            //并退款
            order.setPayStatus(Orders.REFUND);
        } else if (orderStatus == 3 || orderStatus == 4) {
            //用户沟通商家
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        } else {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        String reason = "用户取消";
        if (ordersRejectionDTO.getRejectionReason() != null) {
            reason = ordersRejectionDTO.getRejectionReason();
        }
        //更改订单状态
        order.setCancelReason(reason);
        order.setCancelTime(LocalDateTime.now());
        orderMapper.update(order);
    }

    /**
     * 再来一单
     *
     * @param id
     */
    @Override
    @Transactional
    public void repetitionOrder(Long id) {
        //获得当前订单明细信息
        List<OrderDetail> orderDetailList = orderDetailMapper.getOrderDetailsById(id);
        if (orderDetailList == null || orderDetailList.size() == 0) {
            throw new OrderBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }
        for (OrderDetail orderDetail : orderDetailList) {
            //设置购物车信息
            ShoppingCart shoppingCart = new ShoppingCart();
            BeanUtils.copyProperties(orderDetail, shoppingCart);
            shoppingCart.setUserId(BaseContext.getCurrentId());
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingMapper.postShoppingCart(shoppingCart);
        }
    }

    /**
     * 订单搜索
     *
     * @param ordersPageQueryDTO
     * @return
     */
    @Transactional
    @Override
    public PageResult getConditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        //分页查找订单信息
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        Page<Orders> historyOrders = orderMapper.getHistoryOrders(ordersPageQueryDTO);
        //全部信息条数
        long total = historyOrders.getTotal();
        //订单信息
        List<Orders> result = historyOrders.getResult();
        //订单表和菜品和数量数据集合
        List<OrderSearchVO> searchVOList = new ArrayList<>();
        if (result != null && result.size() > 0) {
            //遍历每一个订单
            for (Orders order : result) {
                OrderSearchVO orderSearchVO = new OrderSearchVO();
                BeanUtils.copyProperties(order, orderSearchVO);
                Long orderId = order.getId();
                //查询订单明细表
                List<OrderDetail> orderDetailList = orderDetailMapper.getOrderDetailsById(orderId);
                //拼接一个订单的名字和数量
                StringBuilder stringBuilder = new StringBuilder();
                for (OrderDetail orderDetail : orderDetailList) {
                    String name = orderDetail.getName();
                    BigDecimal amount = orderDetail.getAmount();
                    //拼接一个订单明细表的名字和数量
                    String s = name + "*" + amount + "; ";
                    stringBuilder.append(s);
                }
                //拼接一个订单的名字和数量
                String string = stringBuilder.toString();
                orderSearchVO.setOrderDishes(string);
                searchVOList.add(orderSearchVO);
            }
        }
        return new PageResult(total, searchVOList);
    }

    /**
     * 各个状态的订单数量统计
     *
     * @return
     */
    @Transactional
    @Override
    public OrderStatisticsVO getStatusCountOrder() {
        //待接单
        Integer toBeConfirmed = orderMapper.getStatusCountOrder(Orders.TO_BE_CONFIRMED);
        //待派送
        Integer confirmed = orderMapper.getStatusCountOrder(Orders.CONFIRMED);
        //派送中
        Integer deliveryInProgress = orderMapper.getStatusCountOrder(Orders.DELIVERY_IN_PROGRESS);
        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();
        orderStatisticsVO.setToBeConfirmed(toBeConfirmed);
        orderStatisticsVO.setDeliveryInProgress(deliveryInProgress);
        orderStatisticsVO.setConfirmed(confirmed);
        return orderStatisticsVO;
    }

    /**
     * 接单
     *
     * @param ordersConfirmDTO
     */
    @Override
    public void putConfirmOrder(OrdersConfirmDTO ordersConfirmDTO) {
        Orders order = new Orders();
        order.setId(ordersConfirmDTO.getId());
        order.setUserId(BaseContext.getCurrentId());
        order.setStatus(Orders.CONFIRMED);
        orderMapper.update(order);
    }

    /**
     * 拒单
     *
     * @param ordersRejectionDTO
     */
    @Override
    @Transactional
    public void putRejectionOrder(OrdersRejectionDTO ordersRejectionDTO) {
        Long id = ordersRejectionDTO.getId();
        String rejectionReason = ordersRejectionDTO.getRejectionReason();
        //获得订单信息
        Orders order = orderMapper.getByIdOrder(id);
        if (order == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        Integer status = order.getStatus();
        //只要待接单时候才能拒单
        if (!Orders.TO_BE_CONFIRMED.equals(status)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        Orders updateOrders = new Orders();
        if (Orders.PAID.equals(order.getPayStatus())) {
            updateOrders.setPayStatus(Orders.REFUND);
        }
        updateOrders.setId(id);
        updateOrders.setStatus(Orders.CANCELLED);
        updateOrders.setCancelReason(rejectionReason);
        updateOrders.setCancelTime(LocalDateTime.now());
        orderMapper.update(updateOrders);
    }

    /**
     * 管理端取消订单
     *
     * @param ordersCancelDTO
     */
    @Override
    @Transactional
    public void putAdminCancelOrder(OrdersCancelDTO ordersCancelDTO) {
        String cancelReason = ordersCancelDTO.getCancelReason();
        Long id = ordersCancelDTO.getId();
        Orders order = orderMapper.getByIdOrder(id);
        if (order == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        Orders updateOrder = new Orders();
        Integer payStatus = order.getPayStatus();
        //已经支付
        if (Orders.PAID.equals(payStatus)) {
            //退款
            updateOrder.setPayStatus(Orders.REFUND);
        }
        //管理端更新状态，时间，取消原因
        updateOrder.setId(id);
        updateOrder.setCancelTime(LocalDateTime.now());
        updateOrder.setCancelReason(cancelReason);
        updateOrder.setStatus(Orders.CANCELLED);
        orderMapper.update(updateOrder);
    }

    /**
     * 派送订单
     *
     * @param id
     */
    @Transactional
    @Override
    public void putDeliveryByIdOrder(Long id) {
        //查询当前订单
        Orders order = orderMapper.getByIdOrder(id);
        if (order == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        Integer status = order.getStatus();
        //如果不是已接单待派送
        if (!Orders.CONFIRMED.equals(status)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        Orders deliveryOrder = new Orders();
        deliveryOrder.setId(id);
        deliveryOrder.setStatus(Orders.DELIVERY_IN_PROGRESS);
        orderMapper.update(deliveryOrder);
    }

    /**
     * 完成订单
     *
     * @param id
     */
    @Override
    @Transactional
    public void putCompleteById(Long id) {
        //查询订单
        Orders order = orderMapper.getByIdOrder(id);
        if (order == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        Integer status = order.getStatus();
        //订单是否派送中
        if (!Orders.DELIVERY_IN_PROGRESS.equals(status)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        Orders CompleteOrder = new Orders();
        CompleteOrder.setId(id);
        CompleteOrder.setStatus(Orders.COMPLETED);
        CompleteOrder.setDeliveryTime(LocalDateTime.now());
        orderMapper.update(CompleteOrder);
    }

    /**
     * 用户催单
     *
     * @param id
     */
    @Override
    public void reminderByIdOrder(Long id) {
        Orders order = orderMapper.getByIdOrder(id);
        if (order == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        //向浏览器推送催单了
        Map<String, Object> map = new HashMap<>();
        map.put("type", 2);
        map.put("orderId", order.getId());
        map.put("content", "订单号：" + order.getNumber());
        String jsonString = JSON.toJSONString(map);
        webSocketServer.sendToAllClient(jsonString);
    }
}
