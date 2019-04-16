package com.ibigsea.rpc.netty;

import com.ibigsea.rpc.config.ProviderConfig;
import com.ibigsea.rpc.zookeeper.ZooKeeperConfig;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author jiang 
 * 2019年1月24日
 */
public class RPCResponseNet {
	private static ProviderConfig providerConfig;
	private ZooKeeperConfig zkConfig;

	/**
	 * 构造方法
	 * 
	 * @param httpHandler
	 * @param providerConfig
	 */
	public RPCResponseNet(ProviderConfig providerConfig, ZooKeeperConfig zkConfig) {
		this.providerConfig = providerConfig;
		this.zkConfig = zkConfig;
	}
	public static void connect() throws InterruptedException {
		EventLoopGroup group = new NioEventLoopGroup();
        try {
            ServerBootstrap sb = new ServerBootstrap();
            sb.group(group) // 绑定线程池
                    .channel(NioServerSocketChannel.class) // 指定使用的channel
                    .localAddress(providerConfig.getIp(),providerConfig.getPort())
                    .childHandler(new ChannelInitializer<SocketChannel>() { // 绑定客户端连接时候触发操作

                                @Override
                                protected void initChannel(SocketChannel ch) throws Exception {
                                    System.out.println("connected...; Client:" + ch.remoteAddress());
                                    ch.pipeline().addLast(new RPCResponseHandler()); // 客户端触发操作
                                }
                            });
            ChannelFuture cf = sb.bind().sync(); // 服务器异步创建绑定
            System.out.println(RPCResponseNet.class + " started and listen on " + cf.channel().localAddress());
            cf.channel().closeFuture().sync(); // 关闭服务器通道
        } finally {
            group.shutdownGracefully().sync(); // 释放线程池资源
        }
	}

}
