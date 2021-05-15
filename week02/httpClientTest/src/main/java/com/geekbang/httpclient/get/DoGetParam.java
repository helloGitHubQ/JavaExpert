package com.geekbang.httpclient.get;

import org.apache.commons.io.FileUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * 带参数的GET请求
 * 两种方式:
 * 1.直接将参数拼接到url后面 如：?wd=java
 * 2.使用URI的方法设置参数 setParameter("wd", "java")
 *
 * @author Q
 * @date 2021/5/15
 */
public class DoGetParam {
    public static void main(String[] args) throws URISyntaxException {
        CloseableHttpClient httpClient = HttpClients.createDefault();

        URI uri = new URIBuilder("http://www.baidu.com/s").setParameter("wd", "java").build();

        HttpGet httpGet = new HttpGet(uri);

        CloseableHttpResponse httpResponse = null;

        try {
            httpResponse = httpClient.execute(httpGet);

            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                String content = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
                FileUtils.writeStringToFile(new File("E:\\devtest\\baidu-param.html"), content, "UTF-8");
                System.out.println("内容长度:" + content.length());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                httpResponse.close();
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
