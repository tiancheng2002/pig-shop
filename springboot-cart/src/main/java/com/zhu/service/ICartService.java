package com.zhu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhu.pojo.Cart;
import com.zhu.vo.CountVo;
import com.zhu.vo.RespBean;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xiaozhu
 * @since 2022-04-07
 */
public interface ICartService extends IService<Cart> {

    RespBean addCart(Cart cart, String ticket);

    RespBean getCart(String ticket);

    RespBean action(CountVo countVo);

    RespBean del(List<Cart> cart, String ticket);

//    RespBean buy(List<Cart> cart, String ticket);
}
