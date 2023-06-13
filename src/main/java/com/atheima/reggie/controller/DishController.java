package com.atheima.reggie.controller;
import com.atheima.reggie.common.R;
import com.atheima.reggie.dto.DishDto;
import com.atheima.reggie.entity.Category;
import com.atheima.reggie.entity.Dish;
import com.atheima.reggie.entity.DishFlavor;
import com.atheima.reggie.service.CategoryService;
import com.atheima.reggie.service.DishFlavorService;
import com.atheima.reggie.service.DishService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 菜品管理
 */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private DishService dishService;
    @PostMapping
    public R<String> saveDish(@RequestBody DishDto dishDto){
        log.info("Saving dish{}",dishDto);
//        dishService.save();
//        dishFlavorService.save();
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品添加成功");
    }
    @GetMapping("/page")
    public R<Page> getPage(Long page,Long pageSize,String name){
        Page<Dish> Dishpage=new Page(page,pageSize);
        Page<DishDto> DishDtopage=new Page(page,pageSize);
        LambdaQueryWrapper<Dish> lambdaQueryWrapper=new LambdaQueryWrapper();
        lambdaQueryWrapper.like(StringUtils.isNotEmpty(name),Dish::getName,name);
        lambdaQueryWrapper.orderByDesc(Dish::getUpdateTime);
        dishService.page(Dishpage,lambdaQueryWrapper);
        //对象拷贝
        BeanUtils.copyProperties(Dishpage,DishDtopage,"records");
        List<Dish> records = Dishpage.getRecords();
        List<DishDto> list= records.stream().map(item->{
            DishDto dishDto=new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);

            if (category!=null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());
        DishDtopage.setRecords(list);
        return R.success(DishDtopage);
    }

    @GetMapping("/{id}")
    public R<DishDto> getDishDtoById(@PathVariable Long id ){

        Dish DishbyId = dishService.getById(id);
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DishFlavor::getDishId,id);
        List<DishFlavor> dishFlavor = dishFlavorService.list(lambdaQueryWrapper);
        DishDto dishDto=new DishDto();
        BeanUtils.copyProperties(DishbyId,dishDto);
        dishDto.setFlavors(dishFlavor);
        return R.success(dishDto);
    }
    @PutMapping
    public R<String> updateDish(@RequestBody DishDto dishDto){
        log.info("Saving dish{}",dishDto);
//        dishService.save();
//        dishFlavorService.save();
        dishService.updatWithFlavors(dishDto);
        return R.success("修改菜品添加成功");
    }
    @PostMapping("/status/{status}")
    public R<List<Dish>> soptshop(@PathVariable Integer status,Long[] ids){
       LambdaQueryWrapper<Dish> lambdaQueryWrapper=new LambdaQueryWrapper<>();
       lambdaQueryWrapper.in(Dish::getId,ids);
        List<Dish> dishList = dishService.list(lambdaQueryWrapper);
        for(Dish d: dishList){
            d.setStatus(status);
        }
        dishService.updateBatchById(dishList);
        return R.success(dishList);
    }
    @DeleteMapping
    public R<String> deletedish(Long[] ids){
        LambdaQueryWrapper<Dish> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(Dish::getId,ids);
        dishService.remove(lambdaQueryWrapper);
        return R.success("删除成功");
    }
    /**
     * 根据条件查询对应菜品数据
     * @param dish
     * @return
     */
//    @GetMapping("/list")
////    http://localhost:8080/dish/list?categoryId=1397844303408574465
//    public R<List<Dish>> getlist(Dish dish){
//        LambdaQueryWrapper<Dish> lambdaQueryWrapper=new LambdaQueryWrapper<>();
//        lambdaQueryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
//        lambdaQueryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//        //添加起售状态的菜品
//        lambdaQueryWrapper.eq(Dish::getStatus,1);
//        List<Dish> list = dishService.list(lambdaQueryWrapper);
//        return R.success(list);
//    }

    @GetMapping("/list")
//    http://localhost:8080/dish/list?categoryId=1397844303408574465
    public R<List<DishDto>> getlist(Dish dish){
        LambdaQueryWrapper<Dish> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        lambdaQueryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        //添加起售状态的菜品
        lambdaQueryWrapper.eq(Dish::getStatus,1);
        List<Dish> list = dishService.list(lambdaQueryWrapper);
        List<DishDto> dishDtos=new ArrayList<>();

        for (Dish dish1:list){
            DishDto dishDto=new DishDto();
            BeanUtils.copyProperties(dish1,dishDto);
            //根据id查询分类的对象
            Category category = categoryService.getById(dish1.getCategoryId());
            if (category!=null){
                dishDto.setCategoryName(category.getName());
            }
            Long Dishid = dish1.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper1=new LambdaQueryWrapper<DishFlavor>();
            lambdaQueryWrapper1.eq(DishFlavor::getDishId,Dishid);
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper1);
            dishDto.setFlavors(dishFlavorList);
            dishDtos.add(dishDto);
        }
        return R.success(dishDtos);
    }
}
