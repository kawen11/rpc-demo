package com.ibigsea.rpc.config;

/**
 * 服务消费者配置
 * 
 * @author jiang
 *
 */
public class ConsumerConfig {

	/**
	 * 请求地址 服务提供者监听的地址和端口
	 */
	private String url;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
