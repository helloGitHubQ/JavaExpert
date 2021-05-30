package com.geekbang.homework;

import java.util.concurrent.Callable;

/**
 * @author Q
 * @date 2021/5/30
 */
public class CallableC implements Callable<Integer> {
    @Override
    public Integer call() {
        int sum = HomeWork03.sum();
        return sum;
    }
}
