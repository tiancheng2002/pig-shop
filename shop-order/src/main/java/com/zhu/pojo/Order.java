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
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author xiaozhu
 * @since 2022-04-12
 */
@TableName("t_order")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "oid")
    private String oid;

    private Integer gid;

    private String gname;

    private Integer count;

    private Date createTime;

    private Date endTime;
    //表示订单状态
    //0待付款 1待收货 2待出库 3待收货 4订单完成 -1取消订单
    private Integer status;

    private Integer method;

    private Date payTime;

    private BigDecimal money;

    private Long uid;

    private Integer aid;

}
