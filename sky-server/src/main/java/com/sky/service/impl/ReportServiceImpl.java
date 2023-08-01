package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.ReportMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {
    @Autowired
    private ReportMapper reportMapper;
    @Autowired
    private UserMapper userMapper;

    /**
     * 营业额统计
     *
     * @return
     */
    @Override
    public TurnoverReportVO reportTurnoverStatistics(LocalDate begin, LocalDate end) {
        //日期集合
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        String dateJoin = StringUtils.join(dateList, ",");
        //营业额集合
        List<Double> turnoverList = new ArrayList<>();
        for (LocalDate date : dateList) {
            //遍历当天数据获得当天营业额
            LocalDateTime minDateTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime maxDateTime = LocalDateTime.of(date, LocalTime.MAX);
            Double dayAmount = reportMapper.getAmountReport(minDateTime, maxDateTime, Orders.COMPLETED);
            dayAmount = dayAmount == null ? 0.0 : dayAmount;
            turnoverList.add(dayAmount);
        }
        String turnoverListJoin = StringUtils.join(turnoverList, ",");
        return new TurnoverReportVO(dateJoin, turnoverListJoin);
    }

    /**
     * 用户统计
     *
     * @param begin
     * @param end
     */
    @Override
    @Transactional
    public UserReportVO getReportUserStatistics(LocalDate begin, LocalDate end) {
        //统计日期列表
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        String dateJoin = StringUtils.join(dateList, ",");
        //统计每天的新增用户数量
        List<Long> newUserSum = new ArrayList<>();
        //统计每天的总用户数量
        List<Long> SumUserSum = new ArrayList<>();
        for (LocalDate date : dateList) {
            LocalDateTime minTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime maxTime = LocalDateTime.of(date, LocalTime.MAX);
            //每天新增用户数量
            Long newUserCount = userMapper.getReportUserStatistics(minTime, maxTime);
            newUserCount = newUserCount == null ? 0L : newUserCount;
            newUserSum.add(newUserCount);
            //每天的总用户数量
            Long SumUserCount = userMapper.getReportCountStatistics(maxTime);
            SumUserCount = SumUserCount == null ? 0L : SumUserCount;
            SumUserSum.add(SumUserCount);
        }
        //每天新用户总量列表
        String newUserJoin = StringUtils.join(newUserSum, ",");
        //每天用户总量列表
        String SumUserJoin = StringUtils.join(SumUserSum, ",");
        return new UserReportVO(dateJoin, SumUserJoin, newUserJoin);
    }

    /**
     * 订单统计
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public OrderReportVO getOrdersStatistics(LocalDate begin, LocalDate end) {
        //日期列表集合
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        //日期列表
        String dateStringJon = StringUtils.join(dateList, ",");

        //查询到订单数列表
        List<Integer> orderCountList = new ArrayList<>();
        //查询到的有效订单数列表
        List<Integer> validOrderCountList = new ArrayList<>();
        //订单完成率
        double orderCompletionRate = 0.00;
        //订单总数
        int totalOrderCount = 0;
        //有效订单数
        int validOrderCount = 0;
        for (LocalDate date : dateList) {
            LocalDateTime minDate = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime maxDate = LocalDateTime.of(date, LocalTime.MAX);
            //查询到订单数
            Integer reportCountList = reportMapper.getCountOrder(minDate, maxDate, null);
            reportCountList = reportCountList == null ? 0 : reportCountList;
            orderCountList.add(reportCountList);
            //订单总数
            totalOrderCount += reportCountList;
            //查询到的有效订单数
            Integer reportEffectiveCountList =
                    reportMapper.getCountOrder(minDate, maxDate, Orders.COMPLETED);
            reportEffectiveCountList = reportEffectiveCountList == null ? 0 : reportEffectiveCountList;
            validOrderCountList.add(reportEffectiveCountList);
            //有效订单数
            validOrderCount += reportEffectiveCountList;
        }
        //订单完成率
        orderCompletionRate = validOrderCount * 1.00 / totalOrderCount;
        //订单数列表
        String CountStringJoin = StringUtils.join(orderCountList, ",");
        //有效订单数列表
        String CountEffectiveStringJoin = StringUtils.join(validOrderCountList, ",");

        return new OrderReportVO(
                dateStringJon,//日期列表
                CountStringJoin,//订单列表
                CountEffectiveStringJoin,//订单有效列表
                totalOrderCount,//订单总数
                validOrderCount,//有效订单数
                orderCompletionRate);//订单完成率
    }

    /**
     * 查询销量排名top10
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    @Transactional
    public SalesTop10ReportVO getSaleTop10(LocalDate begin, LocalDate end) {
        //商品出售集合
        LocalDateTime minDateTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime maxDateTime = LocalDateTime.of(end, LocalTime.MAX);
        //获得商品名称和销量
        List<GoodsSalesDTO> goodsSalesDTOList
                = reportMapper.getSaleTop10(minDateTime, maxDateTime, Orders.COMPLETED);
        //商品名称集合
        List<String> nameList = new ArrayList<>();
        //商品销量集合
        List<Integer> numberList = new ArrayList<>();
        for (GoodsSalesDTO goodsSalesDTO : goodsSalesDTOList) {
            String name = goodsSalesDTO.getName();
            nameList.add(name);
            Integer number = goodsSalesDTO.getNumber();
            numberList.add(number);
        }
        String nameListJoin = StringUtils.join(nameList, ",");
        String numberListJoin = StringUtils.join(numberList, ",");
        return new SalesTop10ReportVO(nameListJoin, numberListJoin);
    }
}
