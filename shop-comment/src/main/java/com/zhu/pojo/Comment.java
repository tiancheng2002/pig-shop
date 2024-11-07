package com.zhu.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author xiaozhu
 * @since 2022-06-14
 */
@TableName("t_comment")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "mid", type = IdType.AUTO)
    private Integer mid;

    private String mname;

    private String mhead;

    private String context;

    private BigDecimal rate;

    private Integer grade;

    private Date commentDate;

    private Integer gid;

    private String oid;

}
