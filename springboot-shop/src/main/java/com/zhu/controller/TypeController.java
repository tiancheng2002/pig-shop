package com.zhu.controller;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhu.pojo.Type;
import com.zhu.service.ITypeService;
import com.zhu.vo.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author xiaozhu
 * @since 2022-04-25
 */
@RestController
@RequestMapping("/type")
public class TypeController {

    @Autowired
    private ITypeService typeService;

    @RequestMapping("/all")
    public RespBean type() {
        List<Type> types = typeService.list(new QueryWrapper<Type>().eq("tgrade", 1));
        return RespBean.success(types);
    }

    @RequestMapping("/second")
    public RespBean second(Integer tid) {
        List<Type> types = typeService.list(new QueryWrapper<Type>().eq("tpid", tid));
        return RespBean.success(types);
    }

    @PostMapping("/add")
    public RespBean addType(@RequestBody String type) throws UnsupportedEncodingException {
        List<Type> types = Decode(type);
        System.out.println(types);
        return RespBean.success(null);
    }

    public List<Type> Decode(String carts) throws UnsupportedEncodingException {
        //将获取到的字符串转码
        String decode = URLDecoder.decode(carts, "utf-8");
        //去掉字符串当中的\
        decode = decode.replace("\\", "");
        //截取字符串当中的[]之间的内容
        String cartString = decode.substring(decode.indexOf("["), decode.indexOf("]") + 1);
        //将字符串转换成json数组
        JSONArray cartArray = JSONArray.parseArray(cartString);
        //将jason数组转换成对应的对象集合
        List<Type> types = cartArray.toJavaList(Type.class);
        return types;
    }

}
