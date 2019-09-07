package com.xcm.message;

import com.xcm.proto.Protocol;

public class MessageCreater {

    public static final int VERSION = 1;

    public static Protocol.Request generateRequest(Command command, Protocol.Param... params) {
        Protocol.RequestHeader header = Protocol.RequestHeader.newBuilder()
                .setVersion(VERSION)
                .setCommand(command.name())
                .build();
        Protocol.RequestBody.Builder bodyBuilder = Protocol.RequestBody.newBuilder();
        for (Protocol.Param param : params) {
            bodyBuilder.addParam(param);

        }
        Protocol.RequestBody body = bodyBuilder.build();
        Protocol.Request request = Protocol.Request.newBuilder()
                .setHeader(header)
                .setBody(body)
                .build();
        return request;
    }

}
