package io.xsun.minecraft.chatsync.common.communication.netty;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.json.JsonObjectDecoder;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

class CodecUtility {
    private static final Gson GSON = new GsonBuilder().create();

    public static ByteToMessageDecoder newJsonDecoder() {
        return new JsonObjectDecoder() {
            @Override
            protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
                List<Object> out0 = new ArrayList<>();
                super.decode(ctx, in, out0);
                out0.forEach(
                        buf -> out.add(GSON.fromJson(((ByteBuf) buf).toString(StandardCharsets.UTF_8), JsonObject.class))
                );
            }
        };
    }

    public static MessageToByteEncoder<JsonObject> newJsonEncoder() {
        return new MessageToByteEncoder<JsonObject>() {
            @Override
            protected void encode(ChannelHandlerContext ctx, JsonObject msg, ByteBuf out) throws Exception {
                out.writeCharSequence(GSON.toJson(msg), StandardCharsets.UTF_8);
            }
        };
    }

    public static ChannelHandler newWebsocketJsonCodec() {
        return new MessageToMessageCodec<WebSocketFrame, JsonObject>() {
            @Override
            protected void encode(ChannelHandlerContext ctx, JsonObject msg, List<Object> out) throws Exception {
                String json = GSON.toJson(msg);
                TextWebSocketFrame wsFrame = new TextWebSocketFrame(json);
                out.add(wsFrame);
            }

            @Override
            protected void decode(ChannelHandlerContext ctx, WebSocketFrame msg, List<Object> out) throws Exception {
                if (msg instanceof TextWebSocketFrame) {
                    TextWebSocketFrame textWsFrame = (TextWebSocketFrame) msg;
                    String json = textWsFrame.text();
                    JsonObject object = GSON.fromJson(json, JsonObject.class);
                    out.add(object);
                } else if (msg instanceof CloseWebSocketFrame) {
                    ctx.channel().close().sync();
                }
            }
        };
    }
}
