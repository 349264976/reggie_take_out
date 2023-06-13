package com.atheima.reggie.service;

import com.atheima.reggie.dto.DishDto;
import com.atheima.reggie.entity.Dish;
import com.baomidou.mybatisplus.extension.service.IService;

public interface DishService extends IService<Dish> {


    /**
     * 新增菜品同时插入数据到dish 和dishfalver
     */

    public void saveWithFlavor(DishDto dishDto);

    public void updatWithFlavors(DishDto dishDto);
}
