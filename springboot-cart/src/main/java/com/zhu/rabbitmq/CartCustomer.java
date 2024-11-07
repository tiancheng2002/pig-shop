package com.zhu.rabbitmq;

import com.zhu.constant.MQConstant;
import com.zhu.pojo.Cart;
import com.zhu.pojo.Order;
import com.zhu.service.ICartService;
import com.zhu.vo.CountVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class CartCustomer {

    @Autowired
    private ICartService cartService;

    @RabbitListener(queues = MQConstant.CartQueue)
    public void cart(CountVo countVo){
        cartService.action(countVo);
    }

    @RabbitListener(queues = MQConstant.CartDelQueue)
    public void cartDel(List<Cart> carts){
        System.out.println(carts);
        cartService.removeBatchByIds(carts);
    }

}
