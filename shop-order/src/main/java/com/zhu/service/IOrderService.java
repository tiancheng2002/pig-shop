package com.zhu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhu.pojo.Order;
import com.zhu.vo.RespBean;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xiaozhu
 * @since 2022-04-12
 */
public interface IOrderService extends IService<Order> {

    RespBean takeOrder(List<Order> orders);

    RespBean getOrder(Long uid);

    RespBean delOrder(String oid);

    RespBean getOrderById(Long uid,String oid);

    RespBean pay(String oid,String ticket);

    RespBean goPay(String ticket,Integer method);

    RespBean userComment(long uid);
}
