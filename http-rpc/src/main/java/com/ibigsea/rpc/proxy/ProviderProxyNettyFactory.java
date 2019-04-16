package com.ibigsea.rpc.proxy;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.mortbay.log.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibigsea.rpc.config.ProviderConfig;
import com.ibigsea.rpc.exception.RpcException;
import com.ibigsea.rpc.invoke.HttpInvoke;
import com.ibigsea.rpc.netty.RPCResponseNet;
import com.ibigsea.rpc.zookeeper.ZKServerService;
import com.ibigsea.rpc.zookeeper.ZkConnect;
import com.ibigsea.rpc.zookeeper.ZooKeeperConfig;

public class ProviderProxyNettyFactory {
	private Logger LOG = LoggerFactory.getLogger(ProviderProxyFactory.class);

	/**
	 * 提供服务需要注册,这里使用map类实现简单的注册 约定俗成的,暴漏服务是需要注册的
	 */
	private Map<Class, Object> providers = new ConcurrentHashMap<Class, Object>();
	
	private Map<String, String> serviceMap = new ConcurrentHashMap<String, String>();

	/**
	 * 这里用来获取暴露的服务
	 */
	private static ProviderProxyNettyFactory factory;

	private static HttpInvoke invoke = HttpInvoke.getInstance();

	/**
	 * 构造方法 注册服务 创建http容器,并启动
	 * 
	 * @param providers
	 * @param config
	 */
	public ProviderProxyNettyFactory(Map<Class, Object> providers, ProviderConfig config, ZooKeeperConfig zkConfig) {
		this.providers = providers;
		factory = this;
		for (Map.Entry<Class, Object> entry : providers.entrySet()) {
			System.out.println(entry.getKey().getSimpleName() + " register");
			Log.info(entry.getKey().getSimpleName() + " register");
		}
		this.serviceMap = convertToServiceMap(providers);
		ZkConnect zkConnect = new ZkConnect(zkConfig);
		try {
			ZooKeeper zookeeper = zkConnect.getZkConnection();
			ZKServerService zkServerService=new ZKServerService(zookeeper);
			zkServerService.initZnode(getServiceMap());
            //创建所有提供者服务的znode
            zkServerService.createServerService(config, getServiceMap());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (KeeperException e) {
			e.printStackTrace();
		}
		RPCResponseNet container = new RPCResponseNet(config, zkConfig);
		try {
			System.out.println("Netty connect......!");
			container.connect();
		} catch (InterruptedException e1) {
			System.out.println("Netty connect......error!");
			e1.printStackTrace();
		}
	}

	private Map<String, String> convertToServiceMap(Map<Class, Object> providers2) {
		Map<String, String> serviceMap = new ConcurrentHashMap<String, String>();
		for (Map.Entry<Class, Object> entry :providers2.entrySet()){
			serviceMap.put(entry.getValue().toString().split("@")[0], entry.getKey().getName());
		}
		return serviceMap;
	}

	public Object getBeanByClass(Class clazz) throws RpcException {
		Object bean = providers.get(clazz);
		if (bean != null) {
			return bean;
		}
		throw new RpcException("service no register", new NullPointerException(), clazz);
	}

	public static ProviderProxyNettyFactory getInstance() {
		return factory;
	}

	public Map<Class, Object> getProviders() {
		return providers;
	}

	public void setProviders(Map<Class, Object> providers) {
		this.providers = providers;
	}

	public Map<String, String> getServiceMap() {
		return serviceMap;
	}

	public void setServiceMap(Map<String, String> serviceMap) {
		this.serviceMap = serviceMap;
	}
}
