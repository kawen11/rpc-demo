package com.ibigsea;

import java.util.concurrent.CountDownLatch;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class App3 {
	public static void main(String[] args) throws Exception {
	     ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath*:spring-context3.xml");
	     context.start();
	     CountDownLatch countDownLatch = new CountDownLatch(1);
	     countDownLatch.await();
	 }
}

