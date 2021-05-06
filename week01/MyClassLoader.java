package com.geekbang.week01;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 自定义ClassLoader
 * 加载Hello.xlass文件并进行反码（x=255-x）操作
 * 然后通过反射调用其内部方法
 *
 * @author Q
 * @date 2021/5/5
 */
public class MyClassLoader extends ClassLoader {
    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        MyClassLoader myClassLoader = new MyClassLoader();
        Class<?> hello = myClassLoader.findClass("Hello");
        Object object = hello.newInstance();
        Method method = hello.getMethod("hello");
        method.invoke(object);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        File directory = new File(".");
        String xlassPath = null;
        byte[] bytes = null;
        Path path = null;
        Class<?> clazz;

        try {
            xlassPath = directory.getCanonicalPath() + "\\src\\com\\geekbang\\week01\\Hello.xlass";
            path = Paths.get(xlassPath);
            bytes = Files.readAllBytes(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //获取.class 文件的二进制字节
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) (255 - bytes[i]);
        }

        //将二进制字节转化为Class对象
        clazz = defineClass(name, bytes, 0, bytes.length);

        return clazz;
    }
}
