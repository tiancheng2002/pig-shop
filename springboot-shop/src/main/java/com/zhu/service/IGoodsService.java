package com.zhu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhu.pojo.Goods;
import com.zhu.vo.RespBean;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xiaozhu
 * @since 2022-03-29
 */
public interface IGoodsService extends IService<Goods> {

    RespBean star(Integer goodsId, String status, String ticket);

    RespBean getStar(String ticket);

    boolean goodsStar(Integer goodsId,String ticket);
}
