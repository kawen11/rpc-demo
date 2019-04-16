package com.ibigsea;

import java.util.concurrent.CountDownLatch;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 
 * @author jiang
 *
 */
public class App {
	
	 public static void main(String[] args) throws Exception {
	     ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath*:spring-context.xml");
	     context.start();
	     CountDownLatch countDownLatch = new CountDownLatch(1);
	     countDownLatch.await();
	 }
	
}
