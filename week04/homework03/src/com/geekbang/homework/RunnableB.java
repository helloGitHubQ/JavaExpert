package com.geekbang.homework;


/**
 * @author Q
 * @date 2021/5/30
 */
public class RunnableB implements Runnable {

    @Override
    public void run() {
        int sum = HomeWork03.sum();
        System.out.println("RunnableB:" + sum);
    }
}
