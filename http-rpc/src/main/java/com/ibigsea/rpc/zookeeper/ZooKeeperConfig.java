package com.ibigsea.rpc.zookeeper;
/**
 * zk配置
 * @author jiang
 *
 */
public class ZooKeeperConfig {

	private String host;
	
	public ZooKeeperConfig(){
		
	}
	
	public ZooKeeperConfig(String host) {
		this.host = host;
	}


	public String getHost() {
		return host;
	}


	public void setHost(String host) {
		this.host = host;
	}

}
