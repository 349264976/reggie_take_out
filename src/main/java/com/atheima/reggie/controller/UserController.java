package com.atheima.reggie.controller;

import com.atheima.reggie.Utils.SendMessageUtils;
import com.atheima.reggie.Utils.ValidateCodeUtils;
import com.atheima.reggie.common.R;
import com.atheima.reggie.entity.User;
import com.atheima.reggie.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.TimeoutUtils;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping("/sendMsg")
    public R<String> sendMessage(@RequestBody User user, HttpSession session){

        String phone=user.getPhone();
        //获取手机号
        if (StringUtils.isNotEmpty(phone)){
            //生成随机的四位验证码
            Integer integer = ValidateCodeUtils.generateValidateCode(4);
            String code = integer.toString();
//            SendMessageUtils.message(phone);
            //调用api短信
            //需要生成的验证码保存到session
//            session.setAttribute(phone,code);
            //将生成的验证码缓存到redis中并且设置有效期5分钟
            redisTemplate.opsForValue().set(phone,code,5,TimeUnit.MINUTES);
            log.info("Sent message"+code);
            return R.success("验证码登录成功");
        }
        return R.error("Invalid code失败");
    }
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map,HttpSession session){
        log.info(map.toString());
        String phone = map.get("phone").toString();
        String code = map.get("code").toString();
        //从cookie中
//        String phonecodeinsessoin =(String) session.getAttribute(phone);
        //从Redis中获取缓存验证码
        String phonecodeinsessoin=(String) redisTemplate.opsForValue().get(phone);
        if (phonecodeinsessoin!=null&&phonecodeinsessoin.equals(code)){
            LambdaQueryWrapper<User> lambdaQueryWrapper=new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(User::getPhone,phone);
            User user = userService.getOne(lambdaQueryWrapper);
            if (user==null){
                user=new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user",user.getId());
            //如果用户登陆成功则删除缓存验证码
            redisTemplate.delete(phone);
            return R.success(user);
        }
        return R.error("登陆失败");
    }

}
