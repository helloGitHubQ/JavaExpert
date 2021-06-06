package com.geekbang.bean.annotation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Q
 * @date 2021/5/31
 */
@Configuration
public class BeanConfig {

    @Bean
    public Car car() {
        return new Car();
    }

    @Bean
    public AutowiredBean autowiredBean() {
        return new AutowiredBean();
    }

    @Bean
    public ResourceBean resourceBean() {
        return new ResourceBean();
    }
}
