package com.zhu.utils;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.impl.cookie.DateUtils;

import java.util.Date;

public class OidUtils {

    //生成唯一的订单号
    public static String oid(){
        String time = DateUtils.formatDate(new Date(), "yyyyMMddHHmmss");
        String randomString = RandomStringUtils.randomNumeric(8);
        return time+randomString;
    }

}
