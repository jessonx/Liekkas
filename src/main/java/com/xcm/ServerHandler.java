package com.xcm;


import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;

import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Date;

public class ServerHandler extends ChannelInboundHandlerAdapter {
	

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println(String.format("====== 客户端连接 ===== : %s",
				ctx.channel().toString()));

		
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		String channelKey = getChannelKey(ctx.channel());
		System.out.println("msg received from: + " + channelKey);

		ctx.channel().write(msg);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {

		System.out.println("客户端断开连接============" + ctx.channel().toString());


		super.channelInactive(ctx);

	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx)
			throws Exception {
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
			throws Exception {

	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		System.out.println("exception is general");
	}

	private static String getChannelKey(Channel channel) {
		InetSocketAddress socketAddress = (InetSocketAddress) channel
				.remoteAddress();

		String host = socketAddress.getHostString();
		int port = socketAddress.getPort();

		String key = String.format("%s:%s", host, port);
		return key;

	}
}
