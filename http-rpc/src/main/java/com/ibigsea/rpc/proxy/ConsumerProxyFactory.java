package com.ibigsea.rpc.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.apache.zookeeper.ZooKeeper;

import com.ibigsea.rpc.invoke.HttpInvoke;
import com.ibigsea.rpc.invoke.NettyInvoke;
import com.ibigsea.rpc.serizlize.JsonFormatter;
import com.ibigsea.rpc.serizlize.JsonParser;
import com.ibigsea.rpc.serizlize.NettyRequest;
import com.ibigsea.rpc.zookeeper.ZKServerService;
import com.ibigsea.rpc.zookeeper.ZkConnect;
import com.ibigsea.rpc.zookeeper.ZooKeeperConfig;

/**
 * 服务消费者代理
 * 
 * @author jiang
 *
 */
public class ConsumerProxyFactory implements InvocationHandler {

	/**
	 * 消费者配置
	 */
	private ZooKeeperConfig zkConfig;

	/**
	 * 需要通过远程调用的服务
	 */
	private String clazz;
	
	private String protocol;

	private static HttpInvoke invoke = HttpInvoke.getInstance();

	private static NettyInvoke nettyInvoke = NettyInvoke.getInstance();
	/**
	 * 创建一个动态代理对象,创建出来的动态代理对象会执行invoke方法
	 * 
	 * @return
	 * @throws ClassNotFoundException
	 */
	public Object create() throws ClassNotFoundException {
		Class interfaceClass = Class.forName(clazz);
		return Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[] { interfaceClass }, this);
	}

	/**
	 * 动态代理对象执行该方法 获取(类信息,方法,参数)通过序列化封装成请求报文,通过http请求发送报文到服务提供者
	 */
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		// 获取类信息
		Class interfaceClass = proxy.getClass().getInterfaces()[0];
		// 封装成请求报文
		String req = JsonFormatter.reqFormatter(interfaceClass, method.getName(), args[0]);
		System.out.println("Request***************************:" + req);
		
		ZkConnect zkConnect = new ZkConnect(zkConfig);
		ZooKeeper zookeeper = zkConnect.getZkConnection();
		ZKServerService zkServerService=new ZKServerService(zookeeper);
		System.out.println("interfaceClass.getName():***************************:" + interfaceClass.getName());
		List<String> allIp = zkServerService.getAllServiceIP(interfaceClass.getName());
		System.out.println("ALL IPs:***************************:" + allIp.toString());
		String host = this.discover(allIp);
		System.out.println("End IP:***************************:" + host);
		// 发送请求报文
		String resb = "success";
		if("jetty".equals(protocol)){
			resb = JsonParser.resbParse(invoke.request(req, host));
		} else if("netty".equals(protocol)){
			NettyRequest nettyRequest = new NettyRequest(UUID.randomUUID().toString(),interfaceClass, method.getName(), args[0]);
			resb = nettyInvoke.request(nettyRequest, host);
		}
		System.out.println("success:***************************" + resb);
		// 解析响应报文
		return resb;
	}
	// 从dataList列表随机获取一个可用的服务端的地址信息给rpc-client
    public String discover(List<String> dataList){
        int size=dataList.size();
        if(size>0){
            int index= new Random().nextInt(size);
            return dataList.get(index);
        }
        throw new RuntimeException("没有找到对应的服务器");
    } 

	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	public ZooKeeperConfig getZkConfig() {
		return zkConfig;
	}

	public void setZkConfig(ZooKeeperConfig zkConfig) {
		this.zkConfig = zkConfig;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

}
