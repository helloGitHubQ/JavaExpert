package com.geekbang.bean;

import com.geekbang.bean.annotation.AutowiredBean;
import com.geekbang.bean.annotation.BeanConfig;
import com.geekbang.bean.annotation.ResourceBean;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author Q
 * @date 2021/5/31
 */
public class PrintlnBean {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(BeanConfig.class);
        context.refresh();

        AutowiredBean autowiredBean = context.getBean(AutowiredBean.class);
        System.out.println(autowiredBean);
        ResourceBean resourceBean = context.getBean(ResourceBean.class);
        System.out.println(resourceBean);

        context.close();

        String[] beanDefinitionNames = context.getBeanDefinitionNames();
        for (String name : beanDefinitionNames) {
            System.out.println("beanName:" + name);
        }
    }
}
