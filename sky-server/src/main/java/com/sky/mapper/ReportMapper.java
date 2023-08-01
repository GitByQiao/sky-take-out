package com.sky.mapper;

import com.sky.dto.GoodsSalesDTO;
import com.sky.vo.TurnoverReportVO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ReportMapper {

    /**
     * 查询每天营业额
     *
     * @param minDateTime
     * @param maxDateTime
     * @param status
     * @return
     */
    Double getAmountReport(LocalDateTime minDateTime, LocalDateTime maxDateTime, Integer status);

    /**
     * 查询到的当天的订单数
     *
     * @param begin
     * @param end
     * @return
     */
    Integer getCountOrder(LocalDateTime begin, LocalDateTime end, Integer status);

    /**
     * 查询商品名称和销量排名top10
     *
     * @param minDateTime
     * @param maxDateTime
     * @return
     */
    List<GoodsSalesDTO> getSaleTop10(LocalDateTime minDateTime, LocalDateTime maxDateTime, Integer status);
}
