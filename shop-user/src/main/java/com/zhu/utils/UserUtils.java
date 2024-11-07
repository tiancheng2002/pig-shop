package com.zhu.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhu.pojo.User;
import com.zhu.vo.RespBean;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserUtils {

    public static void createUser(int count) throws Exception {
        //用for循环来生成count个用户
        List<User> users = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            User user = new User();
            user.setUid(1300000000L + i);
            user.setNickname("user" + i);
            user.setSalt("1a2b3c4d");
            user.setPassword(MD5Utils.fromPass("123456", user.getSalt()));
            users.add(user);
            user.setRegisterDate(new Date());
        }
        System.out.println("create user");
        //生成完所有的用户之后我们需要将它添加至数据库当中
        Connection conn = getConnection();
        String sql = "insert into t_user (uid,nickname,password,salt) values (?,?,?,?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        for (int i = 0; i < users.size(); i++) {
            ps.setLong(1, users.get(i).getUid());
            ps.setString(2, users.get(i).getNickname());
            ps.setString(3, users.get(i).getPassword());
            ps.setString(4, users.get(i).getSalt());
            ps.addBatch();
        }
        ps.executeBatch();
        ps.clearParameters();
        conn.close();
        System.out.println("insert into db");
        //将生成的用户进行登录处理
        String urlString = "http://localhost:8083/user/login";
        File file = new File("D:\\新建文件夹\\Project\\shop\\context.txt");
        if (file.exists()) {
            file.delete();
        }
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        raf.seek(0);
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            URL url = new URL(urlString);
            HttpURLConnection co = (HttpURLConnection) url.openConnection();
            co.setRequestMethod("POST");
            co.setDoOutput(true);
            OutputStream out = co.getOutputStream();
            String params = "mobile=" + user.getUid() + "&password=123456";
            out.write(params.getBytes());
            out.flush();
            InputStream inputStream = co.getInputStream();
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            byte[] buff = new byte[1024];
            int len = 0;
            while ((len = inputStream.read(buff)) >= 0) {
                bout.write(buff, 0, len);
            }
            inputStream.close();
            bout.close();
            String response = new String(bout.toByteArray());
            ObjectMapper mapper = new ObjectMapper();
            RespBean respBean = mapper.readValue(response, RespBean.class);
            String userTicket = (String) respBean.getObj();
            System.out.println(userTicket);
            System.out.println("create userTicket :" + user.getUid());
            String row = user.getUid() + "," + userTicket;
            raf.seek(raf.length());
            raf.write(row.getBytes());
            raf.write("\r\n".getBytes());
            System.out.println("write to file :" + user.getUid());
        }
        raf.close();
        System.out.println("over");
    }

    public static Connection getConnection() throws Exception {
        String url = "jdbc:mysql://localhost:3306/shop?useUnicode=utf-8&useSSL=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai";
        String username = "root";
        String password = "12345678";
        String driver = "com.mysql.cj.jdbc.Driver";
        Class.forName(driver);
        return DriverManager.getConnection(url, username, password);
    }

    public static void main(String[] args) throws Exception {
        createUser(5000);
    }

}
