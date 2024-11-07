package com.zhu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhu.mapper.CartMapper;
import com.zhu.pojo.Cart;
import com.zhu.pojo.User;
import com.zhu.service.ICartService;
import com.zhu.vo.CountVo;
import com.zhu.vo.RespBean;
import com.zhu.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xiaozhu
 * @since 2022-04-07
 */
@Service
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements ICartService {

    @Autowired
    @Qualifier("myRedisTemplate")
    private RedisTemplate redisTemplate;

    @Autowired
    private CartMapper cartMapper;

    @Override
    public RespBean addCart(Cart cart, String ticket) {
        User user = (User) redisTemplate.opsForValue().get("user:" + ticket);
        if(user==null){
            return RespBean.error(RespBeanEnum.Login_NOUSER);
        }
        cart.setUid(user.getUid());
        Cart cartOne = cartMapper.selectOne(new QueryWrapper<Cart>()
                .eq("gid", cart.getGid())
                .eq("uid", cart.getUid()));
        int save;
        if(cartOne==null){
            System.out.println("为空");
            save = cartMapper.insert(cart);
        }else{
            save = inCount(cartOne.getCid());
        }
        if(save>0){
            return RespBean.success(null);
        }else{
            return RespBean.error(RespBeanEnum.Cart_ADD_Error);
        }
    }

    @Override
    public RespBean getCart(String ticket) {
        User user = (User) redisTemplate.opsForValue().get("user:" + ticket);
        if(user!=null){
            List<Cart> carts = cartMapper.selectList(new QueryWrapper<Cart>()
                    .eq("uid", user.getUid()));
            return RespBean.success(carts);
        }
        return RespBean.error(RespBeanEnum.ERROR);
    }

    @Override
    public RespBean action(CountVo countVo) {
        int update;
        if(countVo.getAction().equals("in")){
            update = inCount(countVo.getCid());
        }else{
            update = downCount(countVo.getCid());
        }
        if(update>0){
            return RespBean.success(null);
        }
        return RespBean.error(RespBeanEnum.Cart_ADD_Error);
    }

    @Override
    public RespBean del(List<Cart> cart, String ticket) {
        User user = (User) redisTemplate.opsForValue().get("user:" + ticket);
        if(user==null){
            return RespBean.error(RespBeanEnum.ERROR);
        }
        for(Cart c:cart)cartMapper.deleteById(c);
        return null;
    }

//    @Override
//    public RespBean buy(List<Cart> cart, String ticket) {
//        User user = (User) redisTemplate.opsForValue().get("user:" + ticket);
//        if(user==null){
//            return RespBean.error(RespBeanEnum.ERROR);
//        }
//        //删除购物车当中的记录
//        del(cart,ticket);
//        //通过mq异步生成对应的订单
//        List list = new ArrayList();
//        for (Cart c:cart){
//
//        }
//        return RespBean.success(list);
//    }

    public int inCount(Integer cid){
        int update = cartMapper.update(null,new UpdateWrapper<Cart>()
                .setSql("count = count + 1")
                .eq("cid", cid));
        return update;
    }

    public int downCount(Integer cid){
        int update = cartMapper.update(null,new UpdateWrapper<Cart>()
                .setSql("count = count - 1")
                .eq("cid", cid));
        return update;
    }

}
