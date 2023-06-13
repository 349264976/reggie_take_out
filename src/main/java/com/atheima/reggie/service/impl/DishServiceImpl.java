package com.atheima.reggie.service.impl;

import com.atheima.reggie.dto.DishDto;
import com.atheima.reggie.entity.Dish;
import com.atheima.reggie.entity.DishFlavor;
import com.atheima.reggie.mapper.DishMapper;
import com.atheima.reggie.service.DishFlavorService;
import com.atheima.reggie.service.DishService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private DishService dishService;

    /**
     * 新增菜品同时保存口味数据
     * @param dishDto
     */
    @Transactional
    @Override
    public void saveWithFlavor(DishDto dishDto) {
        /**
         * 保存基本信息到dish
         */
        this.save(dishDto);
        Long dishDtoId = dishDto.getId();
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.stream().map((item)->{
            item.setDishId(dishDtoId);
            return item;
        }).collect(Collectors.toList());

        //保存菜品口味数据dish——flavor
        dishFlavorService.saveBatch(flavors);
    }

    @Override
    @Transactional
    public void updatWithFlavors(DishDto dishDto) {
        //更新dish表基本信息

        dishService.updateById(dishDto);
//        dishFlavorService.updateBatchById();
        /**
         * 清理菜品对应的口味数据--dish——flavor表的delete操作
         */
        LambdaQueryWrapper<DishFlavor> query = new LambdaQueryWrapper();
        query.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(query);
        List<DishFlavor> flavors = dishDto.getFlavors();
        for (DishFlavor flavor : flavors){
            flavor.setDishId(dishDto.getId());
        }
        //清理完数据后插入新数据
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据菜品id查询口味信息以及菜品信息
     * @param id
     * @return
     */
//    @Override
//    public DishDto getByidWithFlavor(Long id) {
//
////        Dish DishbyId = this.getById(id);
////        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper=new LambdaQueryWrapper<>();
////        lambdaQueryWrapper.eq(DishFlavor::getDishId,id);
////        DishFlavor one = dishFlavorService.getOne(lambdaQueryWrapper);
////        DishDto dishDto=new DishDto();
////        BeanUtils.copyProperties(DishbyId,dishDto);
////        dishDto.setFlavors();
//
//
//        return null;
//    }
}
