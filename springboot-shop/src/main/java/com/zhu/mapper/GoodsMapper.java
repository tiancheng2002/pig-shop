package com.zhu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhu.pojo.Goods;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author xiaozhu
 * @since 2022-03-29
 */
@Mapper
@Repository
public interface GoodsMapper extends BaseMapper<Goods> {

}
