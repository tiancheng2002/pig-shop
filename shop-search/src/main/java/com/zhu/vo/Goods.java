package com.zhu.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author xiaozhu
 * @since 2022-03-29
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Goods implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer gid;

    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word", store = true)
    private String gname;

    private int tid;

    private int tpid;

    private String gimg;

    private BigDecimal price;

    private String description;

    private Integer count;

    private Date createTime;

    private Integer buy;

    private Integer stars;

    private boolean isStar;

    private boolean isSale;

    private BigDecimal sprice;

    private Date startTime;

    private Date endTime;

    private String dimg;

}
