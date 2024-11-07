package com.zhu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhu.mapper.SeckillGoodsMapper;
import com.zhu.pojo.SeckillGoods;
import com.zhu.service.ISeckillGoodsService;
import com.zhu.vo.SecKillGoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author xiaozhu
 * @since 2022-04-22
 */
@Service
public class SeckillGoodsServiceImpl extends ServiceImpl<SeckillGoodsMapper, SeckillGoods> implements ISeckillGoodsService {

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    @Qualifier("myRedisTemplate")
    private RedisTemplate redisTemplate;

    @Override
    public List<SecKillGoodsVo> secKillGoods() {
        return seckillGoodsMapper.secKillGoods();
    }

    @Override
    public SecKillGoodsVo detail(Integer id) {
        return seckillGoodsMapper.detail(id);
    }

    @Override
    public String getSecPath(Integer id, Long uid) {
        String key = "path:" + uid + ":" + id;
        String value = UUID.randomUUID().toString().replace("-", "");
        redisTemplate.opsForValue().set(key, value);
        return value;
    }
}
