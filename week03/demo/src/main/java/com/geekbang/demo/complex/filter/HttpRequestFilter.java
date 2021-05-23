package com.geekbang.demo.complex.filter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

/**
 * @author Q
 */
public class HttpRequestFilter {

    public boolean requestFilter(FullHttpRequest request, ChannelHandlerContext ctx) {
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
                return true;
            }
            default: {
                return false;
            }
        }
    }
}
