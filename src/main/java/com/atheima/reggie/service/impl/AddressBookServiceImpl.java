package com.atheima.reggie.service.impl;

import com.atheima.reggie.entity.AddressBook;
import com.atheima.reggie.mapper.AddressBookMapper;
import com.atheima.reggie.service.AddressBookService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {

}
