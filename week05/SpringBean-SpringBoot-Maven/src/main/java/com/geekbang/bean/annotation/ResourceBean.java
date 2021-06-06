package com.geekbang.bean.annotation;

import javax.annotation.Resource;

/**
 * @author Q
 * @date 2021/5/31
 */
public class ResourceBean {

    @Resource
    private Car car;

    @Override
    public String toString() {
        return "ResourceBean{" +
                "car=" + car +
                '}';
    }
}
