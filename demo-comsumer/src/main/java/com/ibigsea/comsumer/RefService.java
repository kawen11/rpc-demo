package com.ibigsea.comsumer;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ibigsea.facade.HelloInterface;
import com.ibigsea.facade.WordInterface;
import com.ibigsea.vo.People;

@Service("refService")
public class RefService {
	
	//这里引用到的是java生成的代理对象
	@Resource
	private HelloInterface helloInterface;
	
	@Resource
	private WordInterface wordInterface;
	
	public void sayHello(String name) {
		System.out.println(helloInterface.speak(new People(name)));
	}
	
	public void printWord(String key){
		System.out.println("ZHI:"+wordInterface.PrintHelloWorld(key));
	}
}
