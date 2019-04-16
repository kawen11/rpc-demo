package com.ibigsea.rpc.config;

/**
 * 服务提供者配置
 * 
 * @author jiang
 *
 */
public class ProviderConfig {
	
	private String ip;

	/**
	 * 监听端口 服务提供者监听请求端口
	 */
	private int port;

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public ProviderConfig() {
	}

	public ProviderConfig(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

}
