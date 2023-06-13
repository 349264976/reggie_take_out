package com.atheima.reggie.service.impl;
import com.atheima.reggie.common.CustomException;
import com.atheima.reggie.dto.SetmealDto;
import com.atheima.reggie.entity.Setmeal;
import com.atheima.reggie.entity.SetmealDish;
import com.atheima.reggie.mapper.SetmealMapper;
import com.atheima.reggie.service.SetmealDishService;
import com.atheima.reggie.service.SetmealService;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SetmealImpl extends ServiceImpl<SetmealMapper,Setmeal> implements SetmealService {
    /**
     * 新增套餐，同时要保存套餐和菜品的关联关系
     * @param setmealDto
     */
    @Autowired
    private SetmealDishService setmealDishService;
    @Transactional
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐的基本信息，操作setmeal执行insert操作
        this.save(setmealDto);
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        //保存套餐和菜品的关联信息 操作setmeal——dish执行insert操作
        for (SetmealDish setmealDish : setmealDishes){
            setmealDish.setSetmealId(setmealDto.getId());
        }
        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 删除套餐并且删除套餐菜品的关联数据
     * @param ids
     */
    @Transactional
    @Override
    public void removeWithDish(List<Long> ids) {
        //查询套餐状态 确定是否可以删除
        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);
        int count = this.count(queryWrapper);
        //如果不能删除抛出一个业务异常
        if (count>0){
            throw  new CustomException("套餐正在售卖中，不能删除");
        }
        //如果可删除，先删除套餐表中数据--setmeal
        this.removeByIds(ids);
        //删除关系表中数据
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper=new LambdaQueryWrapper<SetmealDish>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(lambdaQueryWrapper);
    }
}
