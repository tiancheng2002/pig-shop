package com.zhu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhu.pojo.User;
import com.zhu.vo.LoginVo;
import com.zhu.vo.RespBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author xiaozhu
 * @since 2022-04-02
 */
public interface IUserService extends IService<User> {

    RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response);

    RespBean getUser(String ticket);

    RespBean saveInfo(User user);
}
