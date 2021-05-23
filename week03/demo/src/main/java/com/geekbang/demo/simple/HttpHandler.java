package com.geekbang.demo.simple;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.util.ReferenceCountUtil;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaderValues.KEEP_ALIVE;
import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * @author Q
 */
public class HttpHandler extends ChannelInboundHandlerAdapter {

    /**
     * 请求的基本url
     */
    String targetBaseUrl = "http://127.0.0.1:8801/";

    /**
     * 请求的最终url
     */
    String targetUrL = "";

    /**
     * 定义的httpClint
     */
    CloseableHttpClient httpClient = HttpClientBuilder.create().build();

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            FullHttpRequest request = (FullHttpRequest) msg;
            //不知道为什么每次测试请求总会多一个ico请求，先过滤掉
            if (!"/favicon.ico".equals( request.uri())) {
                //确定访问目标url
                packageTargetUrl(request);
                //获取访问结果
                String result = getResponseResult();
                //返回response
                sendResponse(ctx, request, result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }


    private void packageTargetUrl(FullHttpRequest request) {
        String uri = request.uri();
        String preUri;
        uri = uri.substring(1);

        //uri有多part的话，只取第一part，确定访问模块
        if (uri.contains("/")) {
            preUri = uri.substring(0, uri.indexOf("/"));
        } else {
            preUri = uri;
        }
        switch (preUri) {
            case "user":
            case "data":
            case "log": {
                targetUrL = targetBaseUrl + preUri;
                break;
            }
            default: {
                targetUrL = targetBaseUrl + "error";
            }
        }
    }


    private String getResponseResult() {
        HttpGet httpGet = new HttpGet(targetUrL);
        try {
            httpClient.execute(httpGet);
            CloseableHttpResponse response = null;
            // 执行请求
            response = httpClient.execute(httpGet);
            // 从响应模型中获取响应实体
            HttpEntity responseEntity = response.getEntity();
            return EntityUtils.toString(responseEntity);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    private void sendResponse(ChannelHandlerContext ctx, FullHttpRequest request, String result) {
        FullHttpResponse response = null;
        try {
            response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(result.getBytes("UTF-8")));
            response.headers().set("Content-Type", "application/json");
            response.headers().setInt("Content-Length", response.content().readableBytes());
        } catch (Exception e) {
            e.printStackTrace();
            response = new DefaultFullHttpResponse(HTTP_1_1, NO_CONTENT);
        } finally {
            if (request != null) {
                if (!HttpUtil.isKeepAlive(request)) {
                    ctx.write(response).addListener(ChannelFutureListener.CLOSE);
                } else {
                    response.headers().set(CONNECTION, KEEP_ALIVE);
                    ctx.write(response);
                }
            }
        }
    }
}
