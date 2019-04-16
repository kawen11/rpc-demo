package com.zto.test;

import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibigsea.comsumer.RefService;

public class RequestThread implements Runnable {

    private CountDownLatch signal;
    private CountDownLatch finish;
    private int taskNumber = 0;
    private RefService service;

    public RequestThread(RefService service, CountDownLatch signal, CountDownLatch finish, int taskNumber) {
        this.signal = signal;
        this.finish = finish;
        this.taskNumber = taskNumber;
        this.service = service;
    }

    public void run() {
        try {
            signal.await();
            service.sayHello(taskNumber+"");
            System.out.println("Print result:[" + taskNumber + "]");
        } catch (InterruptedException ex) {
            Logger.getLogger(RequestThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } finally {
            finish.countDown();
        }
    }
}

