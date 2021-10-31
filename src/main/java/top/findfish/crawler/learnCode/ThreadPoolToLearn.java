package top.findfish.crawler.learnCode;

import lombok.SneakyThrows;

import javax.xml.crypto.dsig.keyinfo.KeyValue;
import java.lang.reflect.InvocationHandler;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * TODO
 *
 * @author sun71
 * @version 1.0
 * @date 2021/10/24 19:33
 */
public class ThreadPoolToLearn {


    /**
     * 如果队列数 + 核心线程数 大于 最大线程数 ，而运行的线程数大于最大线程数则报错
     * @param args
     */
    public static void main(String[] args) {
//        ExecutorService executorService = Executors.newCachedThreadPool();
//        ExecutorService executorService1 = Executors.newSingleThreadExecutor();
//        ExecutorService executorService2 = Executors.newFixedThreadPool(10);
//        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10, 15, 1, TimeUnit.SECONDS, new LinkedBlockingDeque<>(), new ThreadPoolExecutor.AbortPolicy());
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10, 15, 1, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10), new ThreadPoolExecutor.AbortPolicy());

        for (int i = 1; i <= 21; i++) {
            threadPoolExecutor.execute(new MyTask(i));
        }

        ThreadLocal<Object> objectThreadLocal = new ThreadLocal<>();
        objectThreadLocal.set(new MyTask(1));
//        new KeyValue()

//        InvocationHandler
    }


}

class MyTask implements Runnable {

    int i = 1;

    public MyTask(int i) {
        this.i = i;
    }

    @SneakyThrows
    @Override
    public void run() {
        Thread.sleep(1000);
        System.out.println(Thread.currentThread().getId() + " --name ->" + Thread.currentThread().getName() + " -->" + i);
    }
}