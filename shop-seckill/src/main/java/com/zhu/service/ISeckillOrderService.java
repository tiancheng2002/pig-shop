package com.zhu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhu.pojo.SeckillOrder;
import com.zhu.vo.RespBean;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xiaozhu
 * @since 2022-04-22
 */
public interface ISeckillOrderService extends IService<SeckillOrder> {

    RespBean getOrder();

}
