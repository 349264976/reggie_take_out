package com.atheima.reggie.dto;


import com.atheima.reggie.entity.Dish;
import com.atheima.reggie.entity.DishFlavor;
import java.util.List;
import lombok.Data;
import java.util.ArrayList;

@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
