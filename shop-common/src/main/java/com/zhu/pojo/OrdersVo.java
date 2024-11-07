package com.zhu.pojo;

import com.zhu.pojo.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrdersVo {

    private String name;
    private Long phone;
    private String addressName;
    private List<Order> orders;

}
