package com.ibigsea.rpc.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
/**
 * 
 * @author jiang 
 * 2019年1月24日
 */
public class RPCRequestNet {
	private String ip;
	private Integer port;
	// netty线程组 同一个服务的连接池内各个连接共用
	private EventLoopGroup group = new NioEventLoopGroup();

	public RPCRequestNet(String ip, Integer port) {
        this.ip = ip;
        this.port = port;
    }

	public EventLoopGroup getGroup() {
		return group;
	}

	public Channel create() throws Exception {
		// 启动辅助类 用于配置各种参数
		Bootstrap b = new Bootstrap();
		b.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true)
				.handler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel socketChannel) throws Exception {
//						socketChannel.pipeline().addLast(RPC.getClientConfig().getDecoder());// 自定义协议编码器解码器
//						socketChannel.pipeline().addLast(RPC.getClientConfig().getEncoder());
						// 添加相应回调处理和编解码器
						socketChannel.pipeline().addLast(new RPCRequestHandler());
					}
				});
		ChannelFuture f = b.connect(ip, port).sync();
		System.out.println("pool create channel " + ip + ":" + port);
		return f.channel();
	}
}
