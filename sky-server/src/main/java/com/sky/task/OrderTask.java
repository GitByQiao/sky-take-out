package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
//订单自动触发任务类
public class OrderTask {
    @Autowired
    private OrderMapper orderMapper;

    /**
     * 15分钟未付款自动取消订单
     */
    @Scheduled(cron = "0 1 * * * ?")
    @Transactional
    public void OrderPaymentOutTime() {
        LocalDateTime localDateTime = LocalDateTime.now();
        LocalDateTime time = localDateTime.plusMinutes(-1);
        //查询是待付款的并且超过十五分钟的
        List<Orders> ordersList = orderMapper.OrderPaymentOutTime(Orders.PENDING_PAYMENT, time);
        if (ordersList != null && ordersList.size() > 0) {
            for (Orders order : ordersList) {
                //将未付款订单取消
                Orders cancelOrder = new Orders();
                cancelOrder.setCancelTime(LocalDateTime.now());
                cancelOrder.setStatus(Orders.CANCELLED);
                cancelOrder.setCancelReason(Orders.PAY_OUT_TIME);
                cancelOrder.setId(order.getId());
                orderMapper.update(cancelOrder);
            }
        }
    }

    /**
     * 晚上1点自动将派送中改完完成订单
     */
    @Scheduled(cron = "0 0 1 * * ?")
    @Transactional
    public void autoCompleteOrder() {
        //获得1点之前派送中的订单
        LocalDateTime time = LocalDateTime.now().plusMinutes(-0);
        List<Orders> ordersList = orderMapper.OrderPaymentOutTime(Orders.DELIVERY_IN_PROGRESS, time);
        if (ordersList != null && ordersList.size() > 0) {
            for (Orders order : ordersList) {
                //将派送中改为已完成
                order.setStatus(Orders.COMPLETED);
                orderMapper.update(order);
            }
        }
    }
}
