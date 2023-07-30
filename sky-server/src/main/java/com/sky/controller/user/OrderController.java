package com.sky.controller.user;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("userOrderController")
@ResponseBody
@Api(tags = "C端订单接口")
@Slf4j
@RequestMapping("/user/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    /**
     * 用户下单
     *
     * @param ordersSubmitDTO
     * @return
     */
    @ApiOperation("用户下单")
    @PostMapping("/submit")
    public Result<OrderSubmitVO> postUserOrderSubmit(@RequestBody OrdersSubmitDTO ordersSubmitDTO) {
        log.info("用户订单：{}", ordersSubmitDTO);
        OrderSubmitVO orderSubmitVO = orderService.postOrderSubmit(ordersSubmitDTO);
        return Result.success(orderSubmitVO);
    }

    /**
     * 新的订单支付：伪装支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    @PutMapping("/payment")
    @ApiOperation("订单支付")
    public Result payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        log.info("订单支付：{}", ordersPaymentDTO);
        //OrderPaymentVO orderPaymentVO = orderService.payment(ordersPaymentDTO);
        //log.info("生成预支付交易单：{}", orderPaymentVO);
        String orderNumber = ordersPaymentDTO.getOrderNumber();
        orderService.paySuccess(orderNumber);
        return Result.success();
    }

    /**
     * 查询历史订单
     *
     * @param ordersPageQueryDTO
     * @return
     */
    @GetMapping("/historyOrders")
    @ApiOperation(value = "查询历史订单")
    public Result<PageResult> getHistoryOrders(OrdersPageQueryDTO ordersPageQueryDTO) {
        log.info("查询历史订单：{}", ordersPageQueryDTO);
        PageResult pageResult = orderService.getHistoryOrders(ordersPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 查询订单详情
     *
     * @param id
     * @return
     */
    @GetMapping("/orderDetail/{id}")
    @ApiOperation(value = "查询订单详情")
    public Result<OrderVO> getOrderDetails(@PathVariable Long id) {
        log.info("查询订单详情:{}", id);
        OrderVO orderVO = orderService.getOrderDetails(id);
        return Result.success(orderVO);
    }

    /**
     * 取消订单
     *
     * @param id
     * @return
     */
    @PutMapping(path = "/cancel/{id}")
    @ApiOperation("取消订单")
    public Result cancelByIdOrder(@PathVariable Long id) {
        log.info("取消订单：{}", id);
        OrdersRejectionDTO ordersRejectionDTO = new OrdersRejectionDTO();
        ordersRejectionDTO.setId(id);
        orderService.cancelByIdOrder(ordersRejectionDTO);
        return Result.success();
    }

    /**
     * 再来一单
     *
     * @param id
     * @return
     */
    @PostMapping("/repetition/{id}")
    @ApiOperation("再来一单")
    public Result repetitionOrder(@PathVariable Long id) {
        log.info("再来一单：{}", id);
        orderService.repetitionOrder(id);
        return Result.success();
    }

    /**
     * 用户催单
     * @param id
     * @return
     */
    @GetMapping("/reminder/{id}")
    @ApiOperation("用户催单")
    public Result reminderByIdOrder(@PathVariable Long id) {
        log.info("用户催单：{}", id);
        orderService.reminderByIdOrder(id);
        return Result.success();
    }
}
