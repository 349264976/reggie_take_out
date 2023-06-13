package com.atheima.reggie.service.impl;

import com.atheima.reggie.entity.ShoppingCart;
import com.atheima.reggie.mapper.ShoppingCartMapper;
import com.atheima.reggie.service.ShoppingCartService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper,ShoppingCart> implements ShoppingCartService {
}
