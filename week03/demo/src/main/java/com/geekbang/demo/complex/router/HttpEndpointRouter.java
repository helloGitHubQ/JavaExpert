package com.geekbang.demo.complex.router;

import com.alibaba.fastjson.JSONObject;

import java.util.List;
import java.util.Random;

/**
 * @author Q
 */
public class HttpEndpointRouter {


    public static JSONObject getRandomEndpoint(List<String> proxyServers) {
        Random random = new Random();
        JSONObject result = new JSONObject();
        int instanceFrOM = random.nextInt(proxyServers.size());
        result.put("url", proxyServers.get(instanceFrOM));
        result.put("instanceFrom", instanceFrOM);
        return result;
    }

}
