package com.zhu.constant;

public class MQConstant {

    //总的交换机
    public static final String ShopTopic = "shopTopic";
    //收藏消息队列
    public static final String StarQueue = "shop.star";
    //购物车消息队列
    public static final String CartQueue = "shop.cartAction";
    //购物车删除队列
    public static final String CartDelQueue = "shop.cartDel";
    //订单消息队列
    public static final String OrderQueue = "shop.order";
    //订单状态更改队列
    public static final String OrderStatus = "shop.orderStatus";
    //订单超时队列
    public static final String OrderTimeQueue = "shop.orderTime";
    //商品消息队列
    public static final String GoodsQueue  = "shop.goods";
    //死信交换机
    public static final String DLDirect = "dl.direct";
    //死信队列
    public static final String DLQueue = "dl.queue";
    //秒杀消息队列
    public static final String SecKillQueue = "shop.secKill";

}
