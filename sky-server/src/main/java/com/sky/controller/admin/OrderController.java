package com.sky.controller.admin;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@ResponseBody
@Slf4j
@RequestMapping("/admin/order")
@Api(tags = "商家订单管理接口")
@RestController(value = "adminOrderController")
public class OrderController {
    @Autowired
    private OrderService orderService;


    /**
     * 订单搜索
     *
     * @param ordersPageQueryDTO
     * @return
     */
    @GetMapping("/conditionSearch")
    @ApiOperation("订单搜索")
    public Result<PageResult> getConditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        log.info("订单搜索：{}", ordersPageQueryDTO);
        PageResult pageResult = orderService.getConditionSearch(ordersPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 各个状态的订单数量统计
     *
     * @return
     */
    @ApiOperation("各个状态的订单数量统计")
    @GetMapping("/statistics")
    public Result<OrderStatisticsVO> getStatusCountOrder() {
        log.info("各个状态的订单数量统计");
        OrderStatisticsVO orderStatisticsVO = orderService.getStatusCountOrder();
        return Result.success(orderStatisticsVO);
    }

    /**
     * 查询订单详情
     *
     * @param id
     * @return
     */
    @ApiOperation("查询订单详情")
    @GetMapping("/details/{id}")
    public Result<OrderVO> getDetailsByIdOrder(@PathVariable Long id) {
        log.info("查询订单详情:{}", id);
        OrderVO orderDetails = orderService.getOrderDetails(id);
        return Result.success(orderDetails);
    }

    /**
     * 接单
     *
     * @return
     */
    @ApiOperation("接单")
    @PutMapping("/confirm")
    public Result putConfirmOrder(@RequestBody OrdersConfirmDTO ordersConfirmDTO) {
        log.info("接单：{}", ordersConfirmDTO);
        orderService.putConfirmOrder(ordersConfirmDTO);
        return Result.success();
    }

    /**
     * 拒单
     *
     * @param ordersRejectionDTO
     * @return
     */
    @ApiOperation("拒单")
    @PutMapping("/rejection")
    public Result putRejectionOrder(@RequestBody OrdersRejectionDTO ordersRejectionDTO) {
        log.info("拒单：{}", ordersRejectionDTO);
        orderService.putRejectionOrder(ordersRejectionDTO);
        return Result.success();
    }

    /**
     * 管理端取消订单
     *
     * @param ordersCancelDTO
     * @return
     */
    @ApiOperation("管理端取消订单")
    @PutMapping("/cancel")
    public Result putAdminCancelOrder(@RequestBody OrdersCancelDTO ordersCancelDTO) {
        log.info("管理端取消订单：{}", ordersCancelDTO);
        orderService.putAdminCancelOrder(ordersCancelDTO);
        return Result.success();
    }

    /**
     * 派送订单
     *
     * @param id
     * @return
     */
    @ApiOperation("派送订单")
    @PutMapping("/delivery/{id}")
    public Result putDeliveryByIdOrder(@PathVariable Long id) {
        log.info("派送订单：{}", id);
        orderService.putDeliveryByIdOrder(id);
        return Result.success();
    }

    /**
     * 完成订单
     * @param id
     * @return
     */
    @ApiOperation("完成订单")
    @PutMapping("/complete/{id}")
    public Result putCompleteById(@PathVariable Long id) {
        log.info("完成订单：{}", id);
        orderService.putCompleteById(id);
        return Result.success();
    }
}
