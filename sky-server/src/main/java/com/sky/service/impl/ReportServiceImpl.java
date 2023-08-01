package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.ReportMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
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
    @Autowired
    private WorkspaceService workspaceService;

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
            Integer reportEffectiveCountList = reportMapper.getCountOrder(minDate, maxDate, Orders.COMPLETED);
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

        return new OrderReportVO(dateStringJon,//日期列表
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
        List<GoodsSalesDTO> goodsSalesDTOList = reportMapper.getSaleTop10(minDateTime, maxDateTime, Orders.COMPLETED);
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

    /**
     * 导出运营数据报表
     *
     * @param response
     */
    @Override
    @Transactional
    public void exportBusinessData(HttpServletResponse response) {
        //1. 查询数据库，获取营业数据---查询最近30天的运营数据
        LocalDate dateBegin = LocalDate.now().minusDays(30);
        LocalDate dateEnd = LocalDate.now().minusDays(1);

        //查询概览数据
        BusinessDataVO businessDataVO = workspaceService.getBusinessData(
                LocalDateTime.of(dateBegin, LocalTime.MIN),
                LocalDateTime.of(dateEnd, LocalTime.MAX));

        //2. 通过POI将数据写入到Excel文件中
        InputStream in = this
                .getClass()
                .getClassLoader()
                .getResourceAsStream("template/运营数据报表模板.xlsx");

        try {
            //基于模板文件创建一个新的Excel文件
            assert in != null;
            XSSFWorkbook excel = new XSSFWorkbook(in);

            //获取表格文件的Sheet页
            XSSFSheet sheet = excel.getSheet("Sheet1");

            //填充数据--时间
            sheet.getRow(1).getCell(1).setCellValue("时间：" + dateBegin + "至" + dateEnd);

            //获得第4行
            XSSFRow row = sheet.getRow(3);
            row.getCell(2).setCellValue(businessDataVO.getTurnover());
            row.getCell(4).setCellValue(businessDataVO.getOrderCompletionRate());
            row.getCell(6).setCellValue(businessDataVO.getNewUsers());

            //获得第5行
            row = sheet.getRow(4);
            row.getCell(2).setCellValue(businessDataVO.getValidOrderCount());
            row.getCell(4).setCellValue(businessDataVO.getUnitPrice());

            //填充明细数据
            for (int i = 0; i < 30; i++) {
                LocalDate date = dateBegin.plusDays(i);
                //查询某一天的营业数据
                BusinessDataVO businessData = workspaceService.getBusinessData(LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX));

                //获得某一行
                row = sheet.getRow(7 + i);
                row.getCell(1).setCellValue(date.toString());
                row.getCell(2).setCellValue(businessData.getTurnover());
                row.getCell(3).setCellValue(businessData.getValidOrderCount());
                row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
                row.getCell(5).setCellValue(businessData.getUnitPrice());
                row.getCell(6).setCellValue(businessData.getNewUsers());
            }

            //3. 通过输出流将Excel文件下载到客户端浏览器
            ServletOutputStream out = response.getOutputStream();
            excel.write(out);

            //关闭资源
            out.close();
            excel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
