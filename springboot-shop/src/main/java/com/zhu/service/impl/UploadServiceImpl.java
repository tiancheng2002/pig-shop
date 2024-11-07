package com.zhu.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import com.zhu.service.UploadService;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class UploadServiceImpl implements UploadService {
    private static String QINIU_IMAGE_DOMAIN = "http://image.xiaozhu02.top/";
    @Override
    public String upload(InputStream is, String fileName) {
        String url = "";
        //构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Region.huadongZheJiang2());
        //...其他参数参考类注释
        UploadManager uploadManager = new UploadManager(cfg);
        //...生成上传凭证，然后准备上传
        String accessKey = "_niBnbOu8wA8JUrm4xnGjaf8GpEXBNzZ9IsWiTIE";
        String secretKey = "wHO_R50lgWFtqi2eu6Wjzqx9xQ-haZYmx1Ak2qXh";
        String bucket = "shopzhu";
        //如果是Windows情况下，格式是 D:\\qiniu\\test.png
        //默认不指定key的情况下，以文件内容的hash值作为文件名
        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucket);
        try {
            Response response = uploadManager.put(is, fileName, upToken,null,null);
            url = QINIU_IMAGE_DOMAIN + JSONObject.parseObject(response.bodyString()).get("key");
            //解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            System.out.println(putRet.key);
            System.out.println(putRet.hash);
            return url;
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println(ex.toString());
        }
        return null;
    }

}
