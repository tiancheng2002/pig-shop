package com.zhu.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wf.captcha.ArithmeticCaptcha;
import com.zhu.pojo.Role;
import com.zhu.service.IRoleService;
import com.zhu.utils.MD5Utils;
import com.zhu.vo.RespBean;
import com.zhu.vo.RespBeanEnum;
import com.zhu.vo.RoleVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author xiaozhu
 * @since 2022-06-20
 */
@RestController
@RequestMapping("/role")
@Slf4j
public class RoleController {

    @Autowired
    private IRoleService roleService;

    @Autowired
    @Qualifier("myRedisTemplate")
    private RedisTemplate redisTemplate;

    //后台登录
    @PostMapping("/login")
    public RespBean RoleLogin(@RequestBody RoleVo roleVo,HttpServletRequest request){
        String captcha = (String) redisTemplate.opsForValue().get("captcha:"+getIpAddress(request));
        if(!captcha.equals(roleVo.getCaptcha())){
            return RespBean.error(RespBeanEnum.Captcha_ERROR);
        }
        Role role = roleService.getOne(new QueryWrapper<Role>()
                .eq("username", roleVo.getUsername()));
        if(role==null){
            return RespBean.error(RespBeanEnum.Login_NOUSER);
        }
        if(!MD5Utils.fromPass(roleVo.getPassword(),role.getSalt()).equals(role.getPassword())){
            return RespBean.error(RespBeanEnum.Login_NOEQ_PASS);
        }
        return RespBean.success(null);
    }

    @RequestMapping("/all")
    public RespBean getRole(){
        List<Role> roles = roleService.list();
        return RespBean.success(roles);
    }

    @PostMapping("/save")
    public RespBean saveRole(@RequestBody Role role){
        boolean save;
        if(role.getRid()!=null){
            save = roleService.updateById(role);
        }else{
            role.setPassword(MD5Utils.md5("123456"));
            role.setSalt("1a2b3c4d");
            save = roleService.save(role);
        }
        return save?RespBean.success(null):RespBean.error(RespBeanEnum.Role_SAVE_ERROR);
    }

    @RequestMapping("/del")
    public RespBean delRole(@RequestParam("rid") Integer rid){
        Role role = roleService.getById(rid);
        if(role==null){
            return RespBean.error(RespBeanEnum.Login_NOUSER);
        }
        boolean del = roleService.removeById(rid);
        return del?RespBean.success(null):RespBean.error(RespBeanEnum.ERROR);
    }

    //获取验证码
    @RequestMapping("/captcha")
    public void getCaptcha(HttpServletRequest request, HttpServletResponse response){
        //获取验证码
        response.setContentType("image/jpg");
        response.setHeader("Pargam","No-cache");
        response.setHeader("cache-Control","no-cache");
        response.setDateHeader("Expires",0);
        //生成验证码，将验证码放入redis当中
        ArithmeticCaptcha captch = new ArithmeticCaptcha(110, 38, 3);
        //获得用户的ip地址
        String ip = getIpAddress(request);
        redisTemplate.opsForValue().set("captcha:"+ip,captch.text(),300, TimeUnit.SECONDS);
        try {
            captch.out(response.getOutputStream());
        } catch (IOException e) {
            log.error("验证码生成失败",e.getMessage());
        }
    }

    //获取客户端IP地址
    public String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if(ip == null || ip.length() == 0 || "unknow".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length () == 0 || "unknown".equalsIgnoreCase (ip)) {
            ip = request.getHeader ("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length () == 0 || "unknown".equalsIgnoreCase (ip)) {
            ip = request.getRemoteAddr ();
            if (ip.equals ("127.0.0.1")) {
                //根据网卡取本机配置的IP
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost ();
                } catch (Exception e) {
                    e.printStackTrace ();
                }
                ip = inet.getHostAddress ();
            }
        }
        // 多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (ip != null && ip.length () > 15) {
            if (ip.indexOf (",") > 0) {
                ip = ip.substring (0, ip.indexOf (","));
            }
        }
        return ip;

    }

}
