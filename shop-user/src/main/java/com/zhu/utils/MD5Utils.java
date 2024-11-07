package com.zhu.utils;

import org.apache.commons.codec.digest.DigestUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MD5Utils {

    public static String salt = "1a2b3c4d";

    public static String md5(String src){
        return DigestUtils.md5Hex(src);
    }

    //第一次加密
    public static String inputPass(String inputPass){
        String s = ""+salt.charAt(0)+salt.charAt(2)+inputPass+salt.charAt(5)+salt.charAt(4);
        return md5(s);
    }

    //第二次加密
    public static String fromPass(String fromPass,String salt){
        String s = ""+salt.charAt(0)+salt.charAt(2)+fromPass+salt.charAt(5)+salt.charAt(4);
        return md5(s);
    }

    public static String inputPassToDB(String pass,String salt){
        String inputPass = inputPass(pass);
        String fromPass = fromPass(inputPass,salt);
        return fromPass;
    }

    public static void main(String[] args) {
        System.out.println(inputPass("123456"));
//        System.out.println(fromPass("d3b1294a61a07da9b49b6e22b2cbd7f9", "1a2b3c4d"));
//        System.out.println(inputPassToDB("d3b1294a61a07da9b49b6e22b2cbd7f9", "1a2b3c4d"));
//        System.out.println(Math.addExact(1, 2));
    }

}
