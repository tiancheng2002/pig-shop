package com.zhu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhu.pojo.Order;
import com.zhu.vo.OrderVo;
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
 * @since 2022-04-12
 */
@Mapper
@Repository
public interface OrderMapper extends BaseMapper<Order> {

    List<OrderVo> getOrder(@Param("uid") Long uid);

    OrderVo getOrderById(@Param("uid") Long uid,@Param("oid") String oid);

    List<Order> userComment(long uid);
}
