package com.zhu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhu.pojo.SeckillGoods;
import com.zhu.vo.SecKillGoodsVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xiaozhu
 * @since 2022-04-22
 */
public interface ISeckillGoodsService extends IService<SeckillGoods> {

    List<SecKillGoodsVo> secKillGoods();

    SecKillGoodsVo detail(Integer id);

    String getSecPath(Integer id, Long uid);
}
