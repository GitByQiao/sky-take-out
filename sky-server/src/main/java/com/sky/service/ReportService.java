package com.sky.service;

import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;

public interface ReportService {

    /**
     * 营业额统计
     *
     * @return
     */
    TurnoverReportVO reportTurnoverStatistics(LocalDate begin, LocalDate end) throws Exception;

    /**
     * 用户统计
     *
     * @param begin
     * @param end
     */
    UserReportVO getReportUserStatistics(LocalDate begin, LocalDate end);

    /**
     * 订单统计
     *
     * @param begin
     * @param end
     * @return
     */
    OrderReportVO getOrdersStatistics(LocalDate begin, LocalDate end);

    /**
     * 查询销量排名top10
     *
     * @param begin
     * @param end
     * @return
     */
    SalesTop10ReportVO getSaleTop10(LocalDate begin, LocalDate end);

    /**
     * 导出运营数据报表
     * @param response
     */
    void exportBusinessData(HttpServletResponse response);
}
