package com.zhu.vo;

import com.zhu.pojo.SeckillGoods;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SecKillGoodsVo extends SeckillGoods {

    private String gname;
    private BigDecimal price;
    private String gimg;
    private String description;

    @Override
    public String toString() {
        return "SecKillGoodsVo{" +
                "gname='" + gname + '\'' +
                ", price=" + price +
                ", gimg='" + gimg + '\'' +
                "} " + super.toString();
    }
}
