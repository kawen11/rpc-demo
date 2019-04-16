package com.ibigsea.rpc.netty;

import java.nio.charset.Charset;

import com.alibaba.fastjson.JSON;
import com.ibigsea.rpc.invoke.NettyInvoke;
import com.ibigsea.rpc.serizlize.NettyResponse;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
/**
 * 
 * @author jiang 
 * 2019年1月24日
 */
public class RPCRequestHandler extends ChannelHandlerAdapter {
//	@Override
//    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        System.out.println("client channelActive..");
//        ctx.writeAndFlush(Unpooled.copiedBuffer("Netty rocks111!", CharsetUtil.UTF_8)); // 必须有flush
//
//        // 必须存在flush
//        // ctx.write(Unpooled.copiedBuffer("Netty rocks!", CharsetUtil.UTF_8));
//        // ctx.flush();
//    }

    @Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("client channelRead..");
        ByteBuf msgbuf = (ByteBuf)msg;
        ByteBuf buf = msgbuf.readBytes(msgbuf.readableBytes());
        System.out.println("Client received:" + ByteBufUtil.hexDump(buf) + "; The value is:" + buf.toString(Charset.forName("utf-8")));
        //ctx.channel().close().sync();// client关闭channel连接
        NettyResponse response = JSON.parseObject(buf.toString(Charset.forName("utf-8")), NettyResponse.class);
        MessageCallBack callBack = NettyInvoke.callbacks.get(response.getUuid());
        if(null != callBack){
        	callBack.over(response.getResponse());
        } else {
        	System.out.println("client channelRead..call back is null, check it please");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
