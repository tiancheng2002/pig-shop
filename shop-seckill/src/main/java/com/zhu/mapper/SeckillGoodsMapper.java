package com.zhu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhu.pojo.SeckillGoods;
import com.zhu.vo.SecKillGoodsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author xiaozhu
 * @since 2022-04-22
 */
@Mapper
@Repository
public interface SeckillGoodsMapper extends BaseMapper<SeckillGoods> {

    List<SecKillGoodsVo> secKillGoods();

    SecKillGoodsVo detail(@Param("id") Integer id);
}
