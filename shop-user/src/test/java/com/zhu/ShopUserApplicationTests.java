package com.zhu;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhu.mapper.AddressMapper;
import com.zhu.mapper.UserMapper;
import com.zhu.pojo.Address;
import com.zhu.pojo.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest(classes = ShopUserApplication.class)
class ShopUserApplicationTests {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AddressMapper addressMapper;

    @Test
    void contextLoads() {
//        int i = addressMapper.updateById(new Address(1, "隔壁", "abc", 12345678970L, 18352963835L));
//        System.out.println(i);
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        List<Address> addresses = addressMapper.selectBatchIds(list);
        for (Address address : addresses) System.out.println(address);
//        User user = userMapper.selectOne(new QueryWrapper<User>().eq("uid", "18352963835"));
//        System.out.println(user);
    }

}
