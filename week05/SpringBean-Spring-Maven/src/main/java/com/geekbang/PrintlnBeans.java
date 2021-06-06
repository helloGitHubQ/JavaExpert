package com.geekbang;


import com.geekbang.annotation.BeanConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 打印Spring中的所有Bean
 *
 * @author Q
 * @date 2021/5/28
 */
public class PrintlnBeans {
    public static void main(String[] args) throws Exception {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(BeanConfig.class);
        String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
        for (String name : beanDefinitionNames) {
            System.out.println("beanName:" + name);
        }


        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        System.out.println("context.getBeanDefinitionNames() ===>> " + String.join(",", context.getBeanDefinitionNames()));
    }
}
