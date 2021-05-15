package com.geekbang.httpclient.post;

import org.apache.commons.io.FileUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;

/**
 * 常规POST请求(无参)
 * 设置Header来伪装浏览器请求
 *
 * @author Q
 * @date 2021/5/15
 */
public class DoPost {
    public static void main(String[] args) {
        CloseableHttpClient httpClient= HttpClients.createDefault();

        HttpPost httpPost=new HttpPost("https://cn.bing.com/");

        httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
        CloseableHttpResponse response=null;

        try {
            response=httpClient.execute(httpPost);
            if(response.getStatusLine().getStatusCode()==200){
                String content= EntityUtils.toString(response.getEntity(),"UTF-8");
                FileUtils.writeStringToFile(new File("E:\\devtest\\bing.html"),content,"UTF-8");
                System.out.println("内容长度:"+content.length());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
