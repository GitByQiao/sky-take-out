package com.sky.service;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderService {
    /**
     * 用户订单下单
     *
     * @param ordersSubmitDTO
     * @return
     */
    OrderSubmitVO postOrderSubmit(OrdersSubmitDTO ordersSubmitDTO);

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);

    /**
     * 查询历史订单
     *
     * @param ordersPageQueryDTO
     * @return
     */
    PageResult getHistoryOrders(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 查询订单详情
     *
     * @param id
     * @return
     */
    OrderVO getOrderDetails(Long id);

    /**
     * 取消订单
     *
     * @param ordersRejectionDTO
     */
    void cancelByIdOrder(OrdersRejectionDTO ordersRejectionDTO);

    /**
     * 再来一单
     *
     * @param id
     */
    void repetitionOrder(Long id);

    /**
     * 订单搜索
     *
     * @param ordersPageQueryDTO
     * @return
     */
    PageResult getConditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 各个状态的订单数量统计
     *
     * @return
     */
    OrderStatisticsVO getStatusCountOrder();

    /**
     * 接单
     *
     * @param ordersConfirmDTO
     */
    void putConfirmOrder(OrdersConfirmDTO ordersConfirmDTO);

    /**
     * 拒单
     *
     * @param ordersRejectionDTO
     */
    void putRejectionOrder(OrdersRejectionDTO ordersRejectionDTO);

    /**
     * 管理端取消订单
     *
     * @param ordersCancelDTO
     */
    void putAdminCancelOrder(OrdersCancelDTO ordersCancelDTO);

    /**
     * 派送订单
     *
     * @param id
     */
    void putDeliveryByIdOrder(Long id);

    /**
     * 完成订单
     *
     * @param id
     */
    void putCompleteById(Long id);

    /**
     * 用户催单
     *
     * @param id
     */
    void reminderByIdOrder(Long id);
}
