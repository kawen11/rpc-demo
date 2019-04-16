package com.zto.test;

import java.util.concurrent.CountDownLatch;

import org.apache.commons.lang3.time.StopWatch;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ibigsea.comsumer.RefService;
import com.ibigsea.facade.HelloInterface;
import com.ibigsea.rpc.serizlize.JsonFormatter;

/**
 * 测试类
 * @author jiang
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:spring-*.xml"})
public class HttpRpcTest
{
    private static final Logger logger = LoggerFactory.getLogger(HttpRpcTest.class);

    @Autowired
    private RefService service;

    @Test
    public void test() throws InterruptedException {
//		service.sayHello("张三1");
    	//高并发测试
    	parallelTask(service,  10);
    }
    @Test
    public void test1() throws InterruptedException {
		service.printWord("1213123133");
    }
    
    public static void parallelTask(RefService service, int parallel) throws InterruptedException {
        //开始计时
        StopWatch sw = new StopWatch();
        sw.start();

        CountDownLatch signal = new CountDownLatch(1);
        CountDownLatch finish = new CountDownLatch(parallel);

        for (int index = 0; index < parallel; index++) {
            RequestThread client = new RequestThread(service, signal, finish, index);
            new Thread(client).start();
        }

        signal.countDown();
        finish.await();
        sw.stop();

        String tip = String.format("RPC调用总共耗时: [%s] 毫秒", sw.getTime());
        System.out.println(tip);
    }
}
