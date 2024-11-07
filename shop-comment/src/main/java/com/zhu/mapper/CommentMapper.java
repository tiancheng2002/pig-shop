package com.zhu.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhu.pojo.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author xiaozhu
 * @since 2022-06-14
 */
@Mapper
@Repository
public interface CommentMapper extends BaseMapper<Comment> {

}
