package com.geekbang.demo.complex.filter;

import io.netty.handler.codec.http.FullHttpResponse;

/**
 * @author Q
 */
public class HttpResponseFilter {

    public void responseFilter(FullHttpResponse response) {
        response.headers().set("MyResponseFilter", "ResponseFilter add this header");

    }
}
