package com.sky.mapper;

import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderDetailMapper {

    /**
     * 插入N条订单明细表数据
     *
     * @param orderDetails
     */
    void postOrderDetails(List<OrderDetail> orderDetails);

    /**
     * 通过订单id查询订单详细表
     *
     * @param ordersId
     * @return
     */
    @Select("select *from order_detail where order_id=#{ordersId}")
    List<OrderDetail> getOrderDetailsById(Long ordersId);
}
