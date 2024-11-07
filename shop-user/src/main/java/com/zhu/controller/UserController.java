package com.zhu.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhu.param.UserParam;
import com.zhu.pojo.User;
import com.zhu.service.IUserService;
import com.zhu.utils.BeanCopeUtils;
import com.zhu.utils.CookieUtil;
import com.zhu.utils.MD5Utils;
import com.zhu.vo.LoginVo;
import com.zhu.vo.RespBean;
import com.zhu.vo.RespBeanEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author xiaozhu
 * @since 2022-04-02
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private IUserService userService;

    @RequestMapping("/login")
    public RespBean Login(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response){
        log.info("{}",loginVo);
        RespBean respBean = userService.doLogin(loginVo, request, response);
        return respBean;
    }

    @PostMapping("/register")
    public RespBean register(@RequestBody UserParam userParam){
        if(!userParam.getPassword().equals(userParam.getConfirmPassword())){
            return RespBean.error(RespBeanEnum.PASSWORD_NO_MATCH);
        }
        if(userService.getOne(new QueryWrapper<User>().eq("uid",userParam.getUid()))!=null){
            return RespBean.error(RespBeanEnum.USER_EXIST);
        }
        User user = BeanCopeUtils.copyBean(userParam, User.class);
        user.setPassword(MD5Utils.inputPass(user.getPassword()));
        user.setRegisterDate(new Date());
        userService.save(user);
        return RespBean.success("注册成功");
    }

    @RequestMapping("/detail")
    public String detail(@CookieValue("userTicket") String ticket,HttpServletRequest request){
        String userTicket = CookieUtil.getCookieValue(request, "userTicket");
        System.out.println(userTicket);
        return ticket;
    }

    @RequestMapping("/get")
    public RespBean getUser(@CookieValue("userTicket") String ticket){
        if(StringUtils.isEmpty(ticket)&&ticket==null){
            return RespBean.error(RespBeanEnum.Login_NO);
        }
        return userService.getUser(ticket);
    }

    @PostMapping("/save")
    public RespBean saveUser(@RequestBody User user) {
        return userService.saveInfo(user);
    }

    @RequestMapping("/all")
    public RespBean getUser(){
        List<User> users = userService.list();
        return RespBean.success(users);
    }

}
