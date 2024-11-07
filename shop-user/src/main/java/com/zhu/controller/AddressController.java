package com.zhu.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhu.pojo.Address;
import com.zhu.pojo.User;
import com.zhu.service.IAddressService;
import com.zhu.vo.RespBean;
import com.zhu.vo.RespBeanEnum;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author xiaozhu
 * @since 2022-04-03
 */
@RestController
@RequestMapping("/address")
public class AddressController {

    @Autowired
    @Qualifier("myRedisTemplate")
    private RedisTemplate redisTemplate;

    @Autowired
    private IAddressService addressService;

    @RequestMapping("/all")
    public RespBean getAddress(@CookieValue("userTicket") String ticket){
        if(StringUtils.isEmpty(ticket)){
            return RespBean.error(RespBeanEnum.Login_NO);
        }
        User user = (User) redisTemplate.opsForValue().get("user:" + ticket);
        List<Address> address = addressService.list(new QueryWrapper<Address>().eq("uid", user.getUid()));
        return RespBean.success(address);
    }

    @RequestMapping("/save")
    public RespBean save(@CookieValue("userTicket") String ticket, Address address){
        System.out.println(address);
        if(StringUtils.isEmpty(ticket)){
            return RespBean.error(RespBeanEnum.Login_NO);
        }
        User user = (User) redisTemplate.opsForValue().get("user:" + ticket);
        address.setUid(user.getUid());
        boolean save;
        if(address.getAid()!=0){
            save = addressService.updateById(address);
        }else {
            save = addressService.save(address);
        }
        if(save){
            return RespBean.success("添加地址成功");
        }else{
            return RespBean.error(RespBeanEnum.Address_SAVE_ERROR);
        }
    }

    @RequestMapping("/del")
    public RespBean delAddress(@RequestParam("aid") Integer aid,@CookieValue("userTicket") String ticket){
        if(StringUtils.isEmpty(ticket)){
            return RespBean.error(RespBeanEnum.Login_NO);
        }
        User user = (User) redisTemplate.opsForValue().get("user:" + ticket);
        boolean b = addressService.removeById(aid);
        if(b){
            return RespBean.success(null);
        }else{
            return RespBean.error(RespBeanEnum.Address_DEL_ERROR);
        }
    }

    @RequestMapping("/get")
    public List<Address> showAll(){
        return addressService.list();
    }

}
