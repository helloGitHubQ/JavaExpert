package com.geekbang.homework;

/**
 * @author Q
 * @date 2021/5/30
 */
public class ThreadA extends Thread {

    @Override
    public void run() {
        int sum = HomeWork03.sum();
        System.out.println("ThreadA:" + sum);
    }
}
