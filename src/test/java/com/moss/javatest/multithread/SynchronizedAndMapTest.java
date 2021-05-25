package com.moss.javatest.multithread;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.util.StopWatch;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.IntStream;

public class SynchronizedAndMapTest {
    @Test
    public void hashMapNotSafe() {
        int loopSize = 30;
        CountDownLatch countDownLatch = new CountDownLatch(loopSize);
        ExecutorService tp = Executors.newFixedThreadPool(10);

        Map<String, LongAdder> testMap = new HashMap<>();

        for(int i = 0; i < loopSize; i++) {
            int selector = (i % 3);
            String type = (selector == 0) ? "HR" : (selector == 1) ? "SALES" : "IT";
            tp.submit(new Runner(type , countDownLatch, testMap));
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(testMap.toString());
        tp.shutdown();
    }

    static class Runner implements Runnable {
        private final String runnerNo;
        private final CountDownLatch countDownLatch;
        private Map<String, LongAdder> testMap;
        Runner(String runnerNo, CountDownLatch countDownLatch, Map<String, LongAdder> testMap){
            this.runnerNo = runnerNo;
            this.countDownLatch = countDownLatch;
            this.testMap = testMap;
        }
        @Override
        public void run() {
            try {
                testMap.computeIfAbsent(runnerNo, (value) -> new LongAdder()).increment();
            } catch (Exception e){
                e.printStackTrace();
            } finally {
                countDownLatch.countDown();
            }

        }
    }

    private static final Integer TestSize = 1000;
    private static final int DivideBy = 10;
    private static final Integer MaxThreadPoolSize = Runtime.getRuntime().availableProcessors();


    @Test
    public void hashMap() throws InterruptedException {
        // Arrange
        StopWatch stopWatch = new StopWatch();
        CountDownLatch countDownLatch = new CountDownLatch(TestSize);
        //Executors.newFixedThreadPool(MaxThreadPoolSize);
        ExecutorService executorService = Executors.newFixedThreadPool(1000);// Executors.newWorkStealingPool();

        Map<String, LongAdder> map = new HashMap<>();

        // Act
        IntStream.range(0, TestSize)
                .forEach(i -> executorService.submit(new HashMapWorker(i, countDownLatch, map)));

        // Assert
        countDownLatch.await();
        IntStream.range(0, DivideBy).forEach(i -> {
            print("range: " + i + ", " + map.get(String.valueOf(i)).intValue());
        });

        IntStream.range(0, DivideBy).forEach(i -> {
            Assertions.assertEquals(TestSize / DivideBy, map.get(String.valueOf(i)).intValue());
        });
        //printAndAssertion(hashMap);
    }

    public static class HashMapWorker implements Runnable {
        private Map<String, LongAdder> hashMap;
        private CountDownLatch countDownLatch;
        private int index;

        public HashMapWorker(int index, CountDownLatch countDownLatch, Map<String, LongAdder> hashMap) {
            this.index = index;
            this.countDownLatch = countDownLatch;
            this.hashMap = hashMap;
        }

        @Override
        public void run() {
            try {
                System.out.println("index: " + index);
                Thread.sleep(10);
                int i = index % 10;
                hashMap.computeIfAbsent(String.valueOf(i), str -> new LongAdder()).increment();

                Thread.sleep(20);
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                countDownLatch.countDown();
            }
        }
    }

    @Test
    public void hashMapWithSynchronized() throws InterruptedException {
        // Arrange
        CountDownLatch countDownLatch = new CountDownLatch(TestSize);
        ExecutorService executorService = Executors.newFixedThreadPool(300);// Executors.newWorkStealingPool();
        //ExecutorService executorService = Executors.newWorkStealingPool(); // Executors.newFixedThreadPool(MaxThreadPoolSize);
        Map<String, LongAdder> hashMap = new HashMap<>();

        // Act
        IntStream.range(0, TestSize)
                .forEach(i -> executorService.submit(new HashMapWithSynchronizedWorker(i, countDownLatch, hashMap)));

        // Assert
        countDownLatch.await();
        printAndAssertion(hashMap);
    }

    public static class HashMapWithSynchronizedWorker implements Runnable {
        private Map<String, LongAdder> hashMap;
        private CountDownLatch countDownLatch;
        private int index;

        public HashMapWithSynchronizedWorker(int index, CountDownLatch countDownLatch, Map<String, LongAdder> hashMap) {
            this.index = index;
            this.countDownLatch = countDownLatch;
            this.hashMap = hashMap;
        }

        @Override
        public void run() {
            try {
                System.out.println("index: " + index);
                synchronized (hashMap) {
                    Thread.sleep(10);
                    int i = index % 10;
                    hashMap.computeIfAbsent(String.valueOf(i), str -> new LongAdder()).increment();
                    Thread.sleep(20);
                    Thread.sleep(20);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                countDownLatch.countDown();
            }
        }
    }

    public static class ConcurrentHashMapWorker implements Runnable {
        private ConcurrentHashMap<String, LongAdder> hashMap;
        private CountDownLatch countDownLatch;
        private int index;

        public ConcurrentHashMapWorker(int index, CountDownLatch countDownLatch, ConcurrentHashMap<String, LongAdder> hashMap) {
            this.index = index;
            this.countDownLatch = countDownLatch;
            this.hashMap = hashMap;
        }

        @Override
        public void run() {
            try {
                System.out.println("index: " + index);
                Thread.sleep(10);
                int i = index % 10;
                hashMap.computeIfAbsent(String.valueOf(i), str -> new LongAdder()).increment();
                Thread.sleep(20);
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                countDownLatch.countDown();
            }
        }
    }

    @Test
    public void concurrentHashMap() throws InterruptedException {
        // Arrange
        CountDownLatch countDownLatch = new CountDownLatch(TestSize);
        ExecutorService executorService = Executors.newFixedThreadPool(1000);// Executors.newWorkStealingPool();
        //ExecutorService executorService = Executors.newFixedThreadPool(500);// Executors.newWorkStealingPool();
        //ExecutorService executorService = Executors.newFixedThreadPool(100);// Executors.newWorkStealingPool();
        //ExecutorService executorService = Executors.newFixedThreadPool(50);// Executors.newWorkStealingPool();

        //ExecutorService executorService = Executors.newWorkStealingPool(); // Executors.newFixedThreadPool(MaxThreadPoolSize);
        ConcurrentHashMap<String, LongAdder> hashMap = new ConcurrentHashMap<>();

        // Act
        IntStream.range(0, TestSize)
                .forEach(i -> executorService.submit(new ConcurrentHashMapWorker(i, countDownLatch, hashMap)));

        // Assert
        countDownLatch.await();
        printAndAssertion(hashMap);
    }

    @Test
    public void joinTest() throws InterruptedException {
        // Arrange
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        int delay=  1000;
        var thread = new Thread(() -> {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        // Act
        thread.run();
        thread.join();

        // Assert
        stopWatch.stop();
        Assertions.assertTrue(stopWatch.getTotalTimeMillis() > delay);

    }



    public static class Worker implements Runnable {
        private CountDownLatch countDownLatch;
        private int index;

        public Worker(final int index, final CountDownLatch countDownLatch) {
            this.index = index;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            try {
                System.out.println("Starting thread... " + index);
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                System.out.println("Finishing Thread... " + index);
                countDownLatch.countDown();
            }
        }
    }

    private void printAndAssertion(Map<String, LongAdder> map) {
        IntStream.range(0, DivideBy).forEach(i -> {
            print("range: " + i + ", " + map.get(String.valueOf(i)).intValue());
        });

        IntStream.range(0, DivideBy).forEach(i -> {
            Assertions.assertEquals(TestSize / DivideBy, map.get(String.valueOf(i)).intValue());
        });
    }

    private void print(String message) {
        System.out.println(message);
    }
}
