package com.zhu.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author xiaozhu
 * @since 2022-04-22
 */
@TableName("t_seckill_order")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SeckillOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Long uid;

    private String oid;

    private Integer gid;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

}
