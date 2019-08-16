package com.ibigsea.rpc.proxy;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.mortbay.jetty.handler.AbstractHandler;
import org.mortbay.log.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibigsea.rpc.config.ProviderConfig;
import com.ibigsea.rpc.container.HttpContainer;
import com.ibigsea.rpc.exception.RpcException;
import com.ibigsea.rpc.invoke.HttpInvoke;
import com.ibigsea.rpc.serizlize.JsonFormatter;
import com.ibigsea.rpc.serizlize.JsonParser;
import com.ibigsea.rpc.serizlize.Request;
import com.ibigsea.rpc.zookeeper.ZKServerService;
import com.ibigsea.rpc.zookeeper.ZkConnect;
import com.ibigsea.rpc.zookeeper.ZooKeeperConfig;

/**
 * 服务提供者代理
 * 
 * @author jiang
 *
 */
public class ProviderProxyFactory extends AbstractHandler {

	private Logger LOG = LoggerFactory.getLogger(ProviderProxyFactory.class);

	/**
	 * 提供服务需要注册,这里使用map类实现简单的注册 约定俗成的,暴漏服务是需要注册的
	 */
	private Map<Class, Object> providers = new ConcurrentHashMap<Class, Object>();
	
	private Map<String, String> serviceMap = new ConcurrentHashMap<String, String>();

	/**
	 * 这里用来获取暴露的服务
	 */
	private static ProviderProxyFactory factory;

	private static HttpInvoke invoke = HttpInvoke.getInstance();

	/**
	 * 构造方法 注册服务 创建http容器,并启动
	 * 
	 * @param providers
	 * @param config
	 */
	public ProviderProxyFactory(Map<Class, Object> providers, ProviderConfig config, ZooKeeperConfig zkConfig) {
		this.providers = providers;
		HttpContainer container = new HttpContainer(this, config, zkConfig);
		//启动jetty容器
		container.start();
		factory = this;
		for (Map.Entry<Class, Object> entry : providers.entrySet()) {
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
	}

	private Map<String, String> convertToServiceMap(Map<Class, Object> providers2) {
		Map<String, String> serviceMap = new ConcurrentHashMap<String, String>();
		for (Map.Entry<Class, Object> entry :providers2.entrySet()){
			serviceMap.put(entry.getValue().toString().split("@")[0], entry.getKey().getName());
		}
		return serviceMap;
	}

	/**
	 * 处理请求 服务消费者发送请求报文过来,服务提供者解析请求报文,通过反射执行方法
	 */
	public void handle(String target, HttpServletRequest request, HttpServletResponse response, int dispatch)
			throws IOException, ServletException {
		// 获取请求报文
		String data = request.getParameter("data");
		System.out.println("Accept Data************************:"+ data);
		try {
			// 反序列化
			Request req = JsonParser.reqParse(data);
			// 获取到注册的服务,并通过反射执行方法
			Object res = req.invoke(ProviderProxyFactory.getInstance().getBeanByClass(req.getClazz()));
			// 返回结果
			invoke.response(JsonFormatter.resbFormatter(res), response.getOutputStream());
		} catch (Exception e) {
			LOG.error(e.getMessage());
		} catch (RpcException e) {
			LOG.error(e.getMessage());
		}

	}

	public Object getBeanByClass(Class clazz) throws RpcException {
		Object bean = providers.get(clazz);
		if (bean != null) {
			return bean;
		}
		throw new RpcException("service no register", new NullPointerException(), clazz);
	}

	public static ProviderProxyFactory getInstance() {
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
