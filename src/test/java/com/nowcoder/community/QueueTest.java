package com.nowcoder.community;


import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class QueueTest {

    public static void main(String[] args) {
        BlockingQueue queue = new ArrayBlockingQueue(10);
        new Thread(new Producer(queue)).start();
        new Thread(new Consumer(queue)).start();
    }

    private static class Producer implements Runnable {
        private BlockingQueue<Integer> blockingQueue;

        public Producer(BlockingQueue<Integer> blockingDeque) {
            this.blockingQueue = blockingDeque;
        }

        @Override
        public void run() {
            for (int i = 0; i < 100; i++) {
                try {
                    Thread.sleep(20);
                    blockingQueue.put(i);
                    System.out.println(Thread.currentThread().getName() + "-生产:" + blockingQueue.size());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private static class Consumer implements Runnable {
        private BlockingQueue<Integer> blockingQueue;

        public Consumer(BlockingQueue<Integer> blockingDeque) {
            this.blockingQueue = blockingDeque;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    Thread.sleep(new Random().nextInt(1000));
                    blockingQueue.take();
                    System.out.println(Thread.currentThread().getName() + "-消费:" + blockingQueue.size());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

}
