package com.ibigsea.service;

import org.springframework.stereotype.Service;

import com.ibigsea.facade.HelloInterface;
import com.ibigsea.vo.People;

/**
 * 实现接口,通过spring配置文件,暴漏出一个服务
 * @author jiang
 *
 */
@Service("helloInterface")
public class HelloService implements HelloInterface {

	/**
	 * 方法实现,服务消费者最终执行到该方法
	 */
	public String speak(People people) {
		return "Hello " + people.getName();
	}

}
