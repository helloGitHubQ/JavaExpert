package com.geekbang.demo.complex.outbound;

import com.alibaba.fastjson.JSONObject;
import com.geekbang.demo.complex.filter.HttpResponseFilter;
import com.geekbang.demo.complex.router.HttpEndpointRouter;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpUtil;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.util.List;
import java.util.concurrent.*;

import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaderValues.KEEP_ALIVE;
import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * @author Q
 */
public class HttpOutboundHandler {

    /**
     * cpu核心数，可做线程组核心数
     */
    private final int cores = Runtime.getRuntime().availableProcessors();

    /**
     * 后端实例url
     */
    private final List<String> backends;

    /**
     * httpClient
     */
    private CloseableHttpAsyncClient httpclient;

    /**
     * 线程组
     */
    private ExecutorService proxyService;
    private final HttpResponseFilter responseFilter = new HttpResponseFilter();


    public HttpOutboundHandler(List<String> backends) {
        this.backends = backends;
//        initProxyService();
//        initHttpClient();
        int cores = Runtime.getRuntime().availableProcessors();
        long keepAliveTime = 1000;
        int queueSize = 2048;
        RejectedExecutionHandler handler = new ThreadPoolExecutor.CallerRunsPolicy();
        proxyService = new ThreadPoolExecutor(cores, cores,
                keepAliveTime, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(queueSize),
                new HttpThreadFactory("proxyService", false), handler);

        IOReactorConfig ioConfig = IOReactorConfig.custom()
                .setConnectTimeout(1000)
                .setSoTimeout(1000)
                .setIoThreadCount(cores)
                .setRcvBufSize(32 * 1024)
                .build();

        httpclient = HttpAsyncClients.custom().setMaxConnTotal(40)
                .setMaxConnPerRoute(8)
                .setDefaultIOReactorConfig(ioConfig)
                .setKeepAliveStrategy((response,context) -> 6000)
                .build();
        httpclient.start();



//        httpclient.start();
    }

    public void handler(FullHttpRequest request, ChannelHandlerContext ctx) {
        //路由服务实例 {url,instanceFrom}
        JSONObject endpoint = HttpEndpointRouter.getRandomEndpoint(backends);
        //组装完整url，因为多路由其实是同一地址，这里是数组下标区分来自于不同实例
        String url = endpoint.getString("url") + request.uri() + "?instanceFrom=" + endpoint.getInteger("instanceFrom");
        //不知道哪里的问题，用线程池跑response回不来
        //proxyService.submit(() -> processRequest(request, ctx, url));
        processRequest(request, ctx, url);

    }


    private void processRequest(FullHttpRequest request, ChannelHandlerContext ctx, String url) {
        final HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_KEEP_ALIVE);
        httpGet.setHeader("MyRequestFilter", "RequestFilter add this header");
        httpclient.execute(httpGet, new FutureCallback<HttpResponse>() {
            @Override
            public void completed(final HttpResponse endpointResponse) {
                try {
                    handleResponse(request, ctx, endpointResponse);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failed(final Exception ex) {
                httpGet.abort();
                ex.printStackTrace();
            }

            @Override
            public void cancelled() {
                httpGet.abort();
            }
        });
    }


    private void handleResponse(FullHttpRequest request, ChannelHandlerContext ctx, HttpResponse endpointResponse) throws Exception {
        FullHttpResponse response = null;
        try {
            byte[] body = EntityUtils.toByteArray(endpointResponse.getEntity());
            response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(body));
            response.headers().set("Content-Type", "application/json");
            response.headers().setInt("Content-Length", Integer.parseInt(endpointResponse.getFirstHeader("Content-Length").getValue()));
            responseFilter.responseFilter(response);
        } catch (Exception e) {
            e.printStackTrace();
            response = new DefaultFullHttpResponse(HTTP_1_1, NO_CONTENT);
            ctx.close();
        } finally {
            if (request != null) {
                if (!HttpUtil.isKeepAlive(request)) {
                    ctx.write(response).addListener(ChannelFutureListener.CLOSE);
                } else {
                    response.headers().set(CONNECTION, KEEP_ALIVE);
                    ctx.write(response);
                }
            }
            if (request != null) {
                if (!HttpUtil.isKeepAlive(request)) {
                    ctx.write(response).addListener(ChannelFutureListener.CLOSE);
                } else {
                    ctx.write(response);
                }
            }
            ctx.flush();
        }
    }


//    //初始化线程组
//    private void initProxyService() {
//        long keepAliveTime = 1000;
//        int queueSize = 2048;
//        RejectedExecutionHandler handler = new ThreadPoolExecutor.CallerRunsPolicy();//.DiscardPolicy();
//        proxyService = new ThreadPoolExecutor(cores, cores,
//                keepAliveTime, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(queueSize),
//                new HttpThreadFactory("proxyService", false), handler);
//    }
//
//    //初始化httpClient
//    private void initHttpClient() {
//        //reactor配置
//        IOReactorConfig ioConfig = IOReactorConfig.custom()
//                .setConnectTimeout(1000)
//                .setSoTimeout(1000)
//                .setIoThreadCount(cores)
//                .setRcvBufSize(32 * 1024)
//                .build();
//
//        httpclient = HttpAsyncClients.custom().setMaxConnTotal(40)
//                .setMaxConnPerRoute(8)
//                .setDefaultIOReactorConfig(ioConfig)
//                .setKeepAliveStrategy((response, context) -> 6000)
//                .build();
//    }
}
