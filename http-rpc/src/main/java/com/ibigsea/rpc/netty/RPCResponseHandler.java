package com.ibigsea.rpc.netty;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.ibigsea.rpc.exception.RpcException;
import com.ibigsea.rpc.proxy.ProviderProxyNettyFactory;
import com.ibigsea.rpc.serizlize.JsonFormatter;
import com.ibigsea.rpc.serizlize.NettyRequest;
import com.ibigsea.rpc.serizlize.NettyResponse;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.CharsetUtil;

/**
 * 
 * @author jiang 
 * 2019年1月24日
 */
public class RPCResponseHandler extends ChannelHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		ByteBuf msgbuf = (ByteBuf) msg;
		System.out.println("server channelRead...; received:" + msgbuf.toString(CharsetUtil.UTF_8));
		// 反序列化
		NettyRequest req = JSON.parseObject(msgbuf.toString(CharsetUtil.UTF_8), NettyRequest.class);
		// 获取到注册的服务,并通过反射执行方法
		Object res = null;
		try {
			res = req.invoke(ProviderProxyNettyFactory.getInstance().getBeanByClass(req.getClazz()));
		} catch (RpcException e) {
			e.printStackTrace();
		}
		// 返回结果
		NettyResponse nettyResponse = new NettyResponse();
		nettyResponse.setUuid(req.getUuid());
		nettyResponse.setResponse(JsonFormatter.resbFormatter(res));
		System.out.println("server channelRead...; return:" + JSON.toJSONString(nettyResponse, SerializerFeature.WriteClassName));
		ByteBuf returnBuf = Unpooled.copiedBuffer(JSON.toJSONString(nettyResponse, SerializerFeature.WriteClassName), CharsetUtil.UTF_8);
		ctx.write(returnBuf);
	}


    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("server channelReadComplete..");
        // 写一个空的buf，并刷新写出区域。完成后关闭sock channel连接。
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("server occur exception:" + cause.getMessage());
        cause.printStackTrace();
        ctx.close(); // 关闭发生异常的连接
    }

	public String convertByteBufToString(ByteBuf buf) {
		String str;
		if (buf.hasArray()) { // 处理堆缓冲区
			str = new String(buf.array(), buf.arrayOffset() + buf.readerIndex(), buf.readableBytes());
		} else { // 处理直接缓冲区以及复合缓冲区
			byte[] bytes = new byte[buf.readableBytes()];
			buf.getBytes(buf.readerIndex(), bytes);
			str = new String(bytes, 0, buf.readableBytes());
		}
		return str;
	}

}
