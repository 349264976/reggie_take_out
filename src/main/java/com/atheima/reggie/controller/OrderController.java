package com.atheima.reggie.controller;
import com.atheima.reggie.common.R;
import com.atheima.reggie.entity.Orders;
import com.atheima.reggie.service.OrderService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 订单
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    /**
     * 用户下单
     *
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders) {
        log.info("订单数据：{}", orders);
        orderService.submit(orders);
        return R.success("下单成功");
    }

    @GetMapping("/userPage")
    public R<Page<Orders>> getOrdersPage(Long page, Long pageSize){
        Page<Orders> pageinfo = new Page<Orders>(page,pageSize);
        LambdaQueryWrapper<Orders> query = new LambdaQueryWrapper<Orders>();
        query.orderByDesc(Orders::getOrderTime);
         orderService.page(pageinfo,query);
         return R.success(pageinfo);
    }
}