package com.atheima.reggie.controller;
import com.atheima.reggie.common.R;
import com.atheima.reggie.dto.SetmealDto;
import com.atheima.reggie.entity.Category;
import com.atheima.reggie.entity.Setmeal;
import com.atheima.reggie.service.CategoryService;
import com.atheima.reggie.service.SetmealDishService;
import com.atheima.reggie.service.SetmealService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private CategoryService categoryService;
    @GetMapping("/page")
    public R<Page> getPage(Long page,Long pageSize,String name){
        Page<Setmeal> pageinfo = new Page(page,pageSize);
        Page<SetmealDto> pageDto = new Page<>();
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(StringUtils.isNotEmpty(name),Setmeal::getName,name);
        setmealService.page(pageinfo,lambdaQueryWrapper);
        BeanUtils.copyProperties(pageinfo,pageDto);
        BeanUtils.copyProperties(pageinfo,pageDto,"records");
        List<Setmeal> records = pageinfo.getRecords();
        List<SetmealDto> setmealDtoList=new ArrayList<>();
        for (Setmeal dto : records){
            SetmealDto setmealDto = new SetmealDto();
            Long categoryId = dto.getCategoryId();
            BeanUtils.copyProperties(dto,setmealDto);
            //根据分类的id查询分类的对象
            Category category = categoryService.getById(categoryId);
            if (category != null){
                String name1 = category.getName();
                setmealDto.setCategoryName(name1);
            }
            setmealDtoList.add(setmealDto);
        }
        pageDto.setRecords(setmealDtoList);
        return R.success(pageDto);
    }
    @PostMapping
    @CacheEvict(value = "setmealCache",allEntries = true)
    public R<String> save(@RequestBody SetmealDto setmealDto){
        log.info("Saving"+setmealDto);
        setmealService.saveWithDish(setmealDto);
        return R.success("Saved套餐成功");
    }
    @DeleteMapping
    @CacheEvict(value = "setmealCache",allEntries = true)
    public R<String> delete(@RequestParam("ids") List<Long> ids){
        log.info("Deleting"+ids);
        setmealService.removeWithDish(ids);
        return R.success("Deletingsuccess");
    }
    @GetMapping("/list")
    @Cacheable(value = "setmealCache",key = "#setmeal.categoryId + '_' + #setmeal.status")
    public R<List<Setmeal>> getlist(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId()!=null, Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus()!=null, Setmeal::getStatus,setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> list = setmealService.list(queryWrapper);
        return R.success(list);
    }
}
