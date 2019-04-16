package com.ibigsea.rpc.serizlize;

import java.io.Serializable;
/**
 * 
 * @author jiang 
 * 2019年1月24日
 */
public class NettyRequest implements Serializable {
	private static final long serialVersionUID = -4363326153251862952L;

	private String uuid;
	
	private Class clazz;

	private String method;

	private Object param;

	public NettyRequest() {
	}

	public NettyRequest(Class clazz, String method, Object param) {
		this.clazz = clazz;
		this.method = method;
		this.param = param;
	}
	
	public NettyRequest(String uuid, Class clazz, String method, Object param){
		this.uuid = uuid;
		this.clazz = clazz;
		this.method = method;
		this.param = param;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
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
