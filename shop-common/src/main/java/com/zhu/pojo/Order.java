package com.zhu.pojo;

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
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    private String oid;

    private Integer gid;

    private String gname;

    private Integer count;

    private Date createTime;

    private Date endTime;

    private Integer status;

    private Date payTime;

    private BigDecimal money;

    private Long uid;

    private Integer aid;

}
