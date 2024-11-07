package com.zhu.vo;

import com.zhu.pojo.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderVo extends Order {

    private String province;
    private String city;
    private String down;
    private String gname;
    private String gimg;
    private int price;
    private String name;
    private Long phone;
    private String addressName;

}
