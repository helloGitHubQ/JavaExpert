package com.geekbang.bean.annotation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Q
 * @date 2021/5/31
 */
@Component
public class AutowiredBean {

    @Autowired
    private Car car;

    @Override
    public String toString() {
        return "AutowiredBean{" +
                "car=" + car +
                '}';
    }
}
