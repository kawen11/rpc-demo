package com.ibigsea.rpc.invoke;

import java.io.OutputStream;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.ibigsea.rpc.exception.RpcException;
import com.ibigsea.rpc.netty.MessageCallBack;
import com.ibigsea.rpc.netty.RPCRequestNet;
import com.ibigsea.rpc.serizlize.NettyRequest;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.util.CharsetUtil;

/**
 * Netty 
 * @author jiang 
 * 2019年1月22日
 */
public class NettyInvoke {
	/**
	 * 单例
	 */
	private static NettyInvoke nettyInvoke;
	
	public static ConcurrentHashMap<String, MessageCallBack> callbacks = new ConcurrentHashMap<String, MessageCallBack>();

	private NettyInvoke() {

	}

	public static synchronized NettyInvoke getInstance() {
		if (nettyInvoke == null) {
			nettyInvoke = new NettyInvoke();
		}
		return nettyInvoke;
	}
	/**
	 * 发送请求
	 * 
	 * @param request
	 *            服务消费者将 (类信息、方法、参数)封装成请求报文,序列化后的字符串
	 * @param consumerConfig
	 *            服务消费者请求的地址
	 * @return 请求结果
	 * @throws RpcException
	 */
	public String request(NettyRequest request, String hostUrl) throws RpcException, Exception {
		MessageCallBack callBack = new MessageCallBack(request);
		System.out.println("Send netty Server:"+hostUrl);
		RPCRequestNet nettyUtil = new RPCRequestNet(hostUrl.split(":")[0], Integer.parseInt(hostUrl.split(":")[1]));
		Channel channel = nettyUtil.create();
//		channel.writeAndFlush(request);
		callbacks.put(request.getUuid(), callBack);
		channel.writeAndFlush(Unpooled.copiedBuffer(JSON.toJSONString(request, SerializerFeature.WriteClassName), CharsetUtil.UTF_8)); // 必须有flush
		return callBack.start(channel);
	}
	
	/**
	 * 响应结果 服务提供者根据服务消费者的请求报文执行后返回结果信息
	 * 
	 * @param response
	 * @param outputStream
	 * @throws RpcException
	 */
	public void response(String response, OutputStream outputStream) throws RpcException {
		try {
			outputStream.write(response.getBytes("UTF-8"));
			outputStream.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
