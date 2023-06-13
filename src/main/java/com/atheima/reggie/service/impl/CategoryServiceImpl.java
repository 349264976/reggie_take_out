package com.atheima.reggie.service.impl;

import com.atheima.reggie.common.CustomException;
import com.atheima.reggie.entity.Category;
import com.atheima.reggie.entity.Dish;
import com.atheima.reggie.entity.Setmeal;
import com.atheima.reggie.mapper.CategoryMapper;
import com.atheima.reggie.service.CategoryService;
import com.atheima.reggie.service.DishService;
import com.atheima.reggie.service.SetmealService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService service;
    /**
     * 根据id删除菜品
     * @param id
     */
    @Override
    public void remove(Long id) {

        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件根据分类id查询
        lambdaQueryWrapper.eq(Dish::getCategoryId, id);
        //查询当前分类是否关联了菜品，如果关联了抛出一个业务异常
        int count = dishService.count(lambdaQueryWrapper);

        //查询当前分类是否关联了菜品
        if (count > 1) {
            //已关联抛出异常
            throw new CustomException("当前分类关联了菜品不能删除");
        }
        //查询是否关联了套餐如果关联抛出一个业务异常
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper1 = new LambdaQueryWrapper<>();
        //添加查询条件根据分类id查询
        lambdaQueryWrapper1.eq(Setmeal::getCategoryId, id);
        //查询当前分类是否关联了菜品，如果关联了抛出一个业务异常
        int count1 = service.count(lambdaQueryWrapper1);
        //查询当前分类是否关联了菜品
        if (count1 >1) {
            //已关联抛出异常
            throw new CustomException("当前分类关联了套餐不能删除");
        }
        //正常删除
        super.removeById(id);
    }

}
