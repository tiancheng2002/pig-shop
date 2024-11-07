package com.zhu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhu.mapper.UserMapper;
import com.zhu.pojo.User;
import com.zhu.service.IUserService;
import com.zhu.utils.CookieUtil;
import com.zhu.utils.MD5Utils;
import com.zhu.utils.UUIDUtils;
import com.zhu.vo.LoginVo;
import com.zhu.vo.RespBean;
import com.zhu.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author xiaozhu
 * @since 2022-04-02
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    @Qualifier("myRedisTemplate")
    private RedisTemplate redisTemplate;

    @Override
    public RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response) {
        User user = userMapper.selectById(loginVo.getMobile());
        if (user == null) {
            return RespBean.error(RespBeanEnum.Login_NOUSER);
        } else {
            if (!user.getPassword().equals(MD5Utils.fromPass(loginVo.getPassword(), user.getSalt()))) {
                return RespBean.error(RespBeanEnum.Login_NOEQ_PASS);
            }
        }
        //获取uuid，将uuid添加到session中
        String ticket = UUIDUtils.getUUID();
        //为了实现session的共享，我们访问网页可用来自nginx代理不同的服务器上面，所以如果存到浏览器中，session不能共享，用户需要再次登录页面，体验性非常不好
        //所以我们可以将用户的信息存入到session中
        redisTemplate.opsForValue().set("user:" + ticket, user);
        CookieUtil.setCookie(request, response, "userTicket", ticket);
        return RespBean.success(ticket);

//        User user = userMapper.selectOne(new QueryWrapper<User>().eq("uid",loginVo.getMobile()));
        //如果用户为空，并且用密码的形式进行登录的话，那么就会返回错误
        //如果用户为空，但是通过手机验证码的形式进行登录的话，那么直接添加对应的用户即可
        //如果用户不为空，那么就会根据对应的登录形式进行相应的判断，登录成功之后就将用户信息添加到缓存当中
//        if(user==null){
//            if(loginVo.getLoginIndex()==1){
//                return RespBean.error(RespBeanEnum.Login_NOUSER);
//            }else{
//                //注册一个普通的用户
//                user = defaultUser(user, loginVo.getMobile());
//                userMapper.insert(user);
//            }
//        }else{
//            if(loginVo.getLoginIndex()==1){
//                if(!user.getPassword().equals(MD5Utils.fromPass(loginVo.getPassword(),user.getSalt()))){
//                    return RespBean.error(RespBeanEnum.Login_NOEQ_PASS);
//                }
//            }else{
//                String captcha = (String) redisTemplate.opsForValue().get("phone:" + loginVo.getMobile());
//                if(captcha!=loginVo.getCaptcha()){
//                    return RespBean.error(RespBeanEnum.Captcha_ERROR);
//                }
//            }
//        }
//        //随机生成uuid，当成cookie存入浏览器当中
//        String ticket = UUIDUtils.getUUID();
//        //将我们随机生成的id放入redis当中，当中session共享
//        redisTemplate.opsForValue().set("user:"+ticket,user);
//        //将cookie存储到浏览器当中
//        CookieUtil.setCookie(request,response,"userTicket",ticket);
//        return RespBean.success(ticket);
    }

    public User defaultUser(User user, String phone) {
        user = new User();
        user.setHead("https://p9.toutiaoimg.com/img/tos-cn-i-siecs4i2o7/9ed5ec7163c449239b96c3e88c4fce45~noop.image");
        user.setPassword(MD5Utils.inputPassToDB("123456", "1a2b3c4d"));
        user.setSalt("1a2b3c4d");
        user.setNickname("匿名用户");
        user.setRegisterDate(new Date());
        user.setSex('1');
        user.setUid(Long.parseLong(phone));
        return user;
    }

    @Override
    public RespBean getUser(String ticket) {
        User user = (User) redisTemplate.opsForValue().get("user:" + ticket);
        return RespBean.success(user);
    }

    @Override
    public RespBean saveInfo(User user) {
        int i = userMapper.updateById(user);
        System.out.println(i);
        return RespBean.success(i > 0);
    }

}
