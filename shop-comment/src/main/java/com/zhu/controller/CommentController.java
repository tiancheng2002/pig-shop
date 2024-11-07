package com.zhu.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhu.constant.MQConstant;
import com.zhu.constant.OrderConstant;
import com.zhu.pojo.Comment;
import com.zhu.pojo.User;
import com.zhu.service.ICommentService;
import com.zhu.vo.OrderVo;
import com.zhu.vo.RespBean;
import com.zhu.vo.RespBeanEnum;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author xiaozhu
 * @since 2022-06-14
 */
@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private ICommentService commentService;

    @Autowired
    @Qualifier("myRedisTemplate")
    private RedisTemplate redisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostMapping("/save")
    public RespBean saveComment(@CookieValue("userTicket") String ticket, @RequestBody Comment comment) {
        if (StringUtils.isEmpty(ticket)) {
            return RespBean.error(RespBeanEnum.Login_NO);
        }
        System.out.println(comment);
        User user = (User) redisTemplate.opsForValue().get("user:" + ticket);
        comment.setMname(user.getNickname());
        comment.setMhead(user.getHead());
        comment.setCommentDate(new Date());
        boolean save = commentService.save(comment);
        System.out.println(save);
        //将订单状态更改为已完成
        if (save) {
            OrderVo orderVo = new OrderVo(comment.getOid(), OrderConstant.Status_Finish);
            rabbitTemplate.convertAndSend(MQConstant.ShopTopic, "shop.orderStatus", orderVo);
        }
        return save ? RespBean.success(null) : RespBean.error(RespBeanEnum.Comment_ERROR);
    }

    //查看指定商品对应的评论
    @RequestMapping("/detail")
    public RespBean getCommentByGoods(@RequestParam("goodsId") Integer goodsId) {
        List<Comment> comments = commentService.list(new QueryWrapper<Comment>().eq("gid", goodsId));
        return RespBean.success(comments);
    }

}
