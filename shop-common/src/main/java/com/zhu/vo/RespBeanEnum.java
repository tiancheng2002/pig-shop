package com.zhu.vo;

import lombok.AllArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor
public enum RespBeanEnum {

    SUCCESS(200, "SUCCESS"),
    ERROR(500, "服务器异常"),

    Login_NOUSER(500101, "用户未注册"),
    Login_NO(500104, "未获取到登录信息"),
    Login_NOEQ_PASS(500102, "密码不正确"),
    Login_SUECCESS(500103, "登录成功"),
    Captcha_ERROR(500104, "验证码错误"),
    Role_SAVE_ERROR(500105, "角色保存失败"),

    PASSWORD_NO_MATCH(500105,"两次密码不一致"),
    USER_EXIST(500106,"用户已被注册"),

    Address_SAVE_ERROR(500201, "添加失败"),
    Address_DEL_ERROR(500202, "删除失败"),

    Star_ERROR(500301, "收藏失败"),

    Goods_NO(500401, "商品不存在"),
    Goods_Count_NO(500402, "商品库存不足"),
    Goods_ADD_FAIL(500403, "商品添加失败"),

    Cart_ADD_Error(500501, "添加购物车失败"),
    Cart_DATA_Change(500502, "购物车数据发生改变"),

    Order_ADD_Error(500601, "添加订单失败"),
    Order_Del_Error(500602, "删除订单失败"),
    Order_No(500603, "订单不存在"),
    Order_Status_Change(500604, "订单状态变更"),

    SecKill_No_Goods(500701, "秒杀商品不存在"),
    SecKill_Have(500702, "您已秒杀过该商品，请不要重复秒杀"),
    SecKill_No_Count(500703, "该商品已被秒杀完，请查看其他活动"),
    SecKill_Fail(500704, "秒杀失败"),
    SecKill_Time_Over(500705, "该商品秒杀时间已过，获取地址失败！"),
    SecKill_Path_ERROR(500706, "非法请求，秒杀失败！"),

    Image_UPLOAD_FAIL(500801, "图片上传失败"),

    Comment_ERROR(500901, "评论失败");

    private final Integer code;
    private final String msg;

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
