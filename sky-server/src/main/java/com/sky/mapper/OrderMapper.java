package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.vo.OrderStatisticsVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {
    /**
     * 新增一条订单表数据
     *
     * @param order
     */
    void postOrder(Orders order);

    /**
     * 根据订单号查询订单
     *
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumberOrder(String orderNumber);

    /**
     * 修改订单信息
     *
     * @param orders
     */
    void update(Orders orders);


    /**
     * 分页查找订单表数据
     *
     * @param ordersPageQueryDTO
     * @return
     */
    Page<Orders> getHistoryOrders(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 通过订单id查询订单
     *
     * @param id
     * @return
     */
    @Select("select *from orders where id=#{id}")
    Orders getByIdOrder(Long id);


    /**
     * 各个状态的订单数量统计
     *
     * @return
     */
    @Select("select count(id) from orders where status=#{status}")
    Integer getStatusCountOrder(Integer status);

    /**
     * 查询是待付款的并且超过十五分钟的
     *
     * @param pendingPayment
     * @param time
     * @return
     */
    @Select("select *from orders where status=#{pendingPayment} and order_time<#{time}")
    List<Orders> OrderPaymentOutTime(Integer pendingPayment, LocalDateTime time);

    /**
     * 根据动态条件统计营业额数据
     * @param map
     * @return
     */
    Double sumByMap(Map map);

    /**
     * 根据动态条件统计订单数量
     * @param map
     * @return
     */
    Integer countByMap(Map map);
}
