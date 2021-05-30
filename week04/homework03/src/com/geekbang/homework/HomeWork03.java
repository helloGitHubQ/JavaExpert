package com.geekbang.homework;

import java.util.concurrent.*;

/**
 * 本周作业：（必做）思考有多少种方式，在main函数启动一个新线程或线程池，
 * 异步运行一个方法，拿到这个方法的返回值后，退出主线程？
 * 写出你的方法，越多越好，提交到github。
 * <p>
 * 一个简单的代码参考：
 *
 * @author Q
 * @date 2021/5/30
 */
public class HomeWork03 {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        long start = System.currentTimeMillis();

        // 在这里创建一个线程或线程池，
        // 异步执行 下面方法

        //方式A
//        ThreadA threadA=new ThreadA();
//        threadA.start();
//        Thread.sleep(100);

        //方式B
//        RunnableB runnableB=new RunnableB();
//        Thread thread=new Thread(runnableB);
//        thread.start();
//        Thread.sleep(100);

        //方式C
//        CallableC callableC=new CallableC();
//        FutureTask<Integer> futureTask=new FutureTask<>(callableC);
//        new Thread(futureTask).start();
//        System.out.println("CallableC:" + futureTask.get());

        //方式D
//        ExecutorService service= Executors.newSingleThreadExecutor();
//        service.execute(()->{
//            int sum = sum();
//            System.out.println("newSingleThreadExecutor:" + sum);
//        });
//        service.shutdown();
//        Thread.sleep(100);

        //方式E
//        ExecutorService service = Executors.newFixedThreadPool(2);
//        service.execute(() -> {
//            int sum = sum();
//            System.out.println("newFixedThreadPool(2):" + sum);
//        });
//        service.shutdown();
//        Thread.sleep(100);


        //方式F
//        ExecutorService service = Executors.newCachedThreadPool();
//        service.execute(() -> {
//            int sum = sum();
//            System.out.println("newCachedThreadPool:" + sum);
//        });
//        service.shutdown();
//        Thread.sleep(100);


        //方式G
//        ExecutorService service = Executors.newScheduledThreadPool(20);
//        service.execute(() -> {
//            int sum = sum();
//            System.out.println("newScheduledThreadPool(20):" + sum);
//        });
//        service.shutdown();
//        Thread.sleep(100);

        //方法H
//        ExecutorService service = new ThreadPoolExecutor(10, 10, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10));
//        service.execute(() -> {
//            int sum = sum();
//            System.out.println("ThreadPoolExecutor1:" + sum);
//        });
//        service.shutdown();
//        Thread.sleep(100);


        //方法I
//        ExecutorService service = new ThreadPoolExecutor(10, 10, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10),new ThreadPoolExecutor.DiscardPolicy());
//        service.execute(() -> {
//            int sum = sum();
//            System.out.println("ThreadPoolExecutor2:" + sum);
//        });
//        service.shutdown();
//        Thread.sleep(100);

        //方法J
//        ExecutorService service = new ThreadPoolExecutor(10, 10, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10),new ThreadPoolExecutor.DiscardOldestPolicy());
//        service.execute(() -> {
//            int sum = sum();
//            System.out.println("ThreadPoolExecutor3:" + sum);
//        });
//        service.shutdown();
//        Thread.sleep(100);

        //方法K
//        ExecutorService service = new ThreadPoolExecutor(10, 10, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10),new ThreadPoolExecutor.CallerRunsPolicy());
//        service.execute(() -> {
//            int sum = sum();
//            System.out.println("ThreadPoolExecutor4:" + sum);
//        });
//        service.shutdown();
//        Thread.sleep(100);


        //等等 使用ThreadPoolExecutor创建线程池的其他构造方法实现


        System.out.println("使用时间：" + (System.currentTimeMillis() - start) + " ms");

        // 然后退出main线程
    }

    static int sum() {
        return fibo(36);
    }

    private static int fibo(int a) {
        if (a < 2) {
            return 1;
        }
        return fibo(a - 1) + fibo(a - 2);
    }

}
