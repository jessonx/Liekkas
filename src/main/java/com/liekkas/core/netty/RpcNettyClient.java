package com.liekkas.core.netty;

import com.liekkas.core.message.Command;
import com.liekkas.core.message.MessageCreater;
import com.liekkas.core.message.RpcRequest;
import com.liekkas.core.proto.Protocol;
import com.liekkas.core.rpc.RpcCallback;
import com.liekkas.core.rpc.RpcFuture;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;

public class RpcNettyClient {
    private static final int MAX_FRAME_LENGTH = 10240;

    String host;
    int port;
    Bootstrap bootstrap;
    Channel channel ;



    public RpcNettyClient(String host, int port) {
        this.host = host;
        this.port = port;
        try {
            init();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void init() throws InterruptedException {
        bootstrap = new Bootstrap();

        EventLoopGroup group = new NioEventLoopGroup();
        bootstrap.group(group);
        bootstrap.channel(NioSocketChannel.class).option(
                ChannelOption.SO_KEEPALIVE, true);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                // decoded
                pipeline.addLast(new LengthFieldBasedFrameDecoder(
                        MAX_FRAME_LENGTH, 0, 4, 0, 4));
                pipeline.addLast(new ProtobufDecoder(Protocol.Response
                        .getDefaultInstance()));
                // encoded
                pipeline.addLast(new LengthFieldPrepender(4));
                pipeline.addLast(new ProtobufEncoder());
                pipeline.addLast(new ClientHandler());
                pipeline.addLast(new RpcClientHandler());

            }
        });
        channel = bootstrap.connect(host, port).sync().channel();

    }

    public RpcFuture send(RpcRequest request, RpcCallback... callbacks) {
        RpcFuture rpcFuture = new RpcFuture();

        for (RpcCallback callback : callbacks) {
            rpcFuture.addCallback(callback);
        }
        RpcClientHandler.map.put(request.getRequestId(),rpcFuture);
        channel.writeAndFlush(request.getProtocolRequest());


        return rpcFuture;
    }

    public void shutdown() {
        bootstrap.group().shutdownGracefully();

    }

    public static void main(String[] args) throws Exception {
        RpcNettyClient rpcNettyClient = new RpcNettyClient("127.0.0.1", 5656);
        Protocol.Param userId = Protocol.Param.newBuilder().setKey("userId").setValue("123").build();
        Protocol.Param password = Protocol.Param.newBuilder().setKey("password").setValue("123").build();
        Protocol.Param sessionId = Protocol.Param.newBuilder().setKey("session").setValue("sessions").build();
        Protocol.Request request = MessageCreater.generateRequest(Command.Login, userId, password, sessionId);
    }
}