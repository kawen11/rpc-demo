package com.ibigsea.rpc.serizlize;

import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONType;

/**
 * 请求信息
 * 
 * @author jiang
 *
 */
public class Request implements Serializable {

	private static final long serialVersionUID = -4363326153251862952L;

	private Class clazz;

	private String method;

	private Object param;

	public Request() {
	}

	public Request(Class clazz, String method, Object param) {
		this.clazz = clazz;
		this.method = method;
		this.param = param;
	}

	public Class getClazz() {
		return clazz;
	}

	public void setClazz(Class clazz) {
		this.clazz = clazz;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public Object getParam() {
		return param;
	}

	public void setParam(Object param) {
		this.param = param;
	}

	/**
	 * 通过反射执行对应的方法
	 * 
	 * @param bean
	 * @return
	 * @throws Exception
	 */
	public Object invoke(Object bean) throws Exception {
		return clazz.getMethod(method, param.getClass()).invoke(bean, param);
	}

}
