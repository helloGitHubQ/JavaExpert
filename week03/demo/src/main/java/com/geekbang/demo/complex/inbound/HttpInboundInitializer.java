package com.geekbang.demo.complex.inbound;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

import java.util.List;

/**
 * @author Q
 */
public class HttpInboundInitializer extends ChannelInitializer {

    private final List<String> backends;

    public HttpInboundInitializer(List<String> backends){
        this.backends = backends;
    }
    @Override
    protected void initChannel(Channel ch) {
        ChannelPipeline cp = ch.pipeline();
        cp.addLast(new HttpServerCodec())
                .addLast(new HttpObjectAggregator(1024 * 1024 * 1024))
                .addLast(new HttpInboundHandler(this.backends));
    }
}
