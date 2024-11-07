package com.zhu.vo;

import com.zhu.pojo.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MessageVo {

    private SecKillGoodsVo goodsVo;

    private long uid;

}
