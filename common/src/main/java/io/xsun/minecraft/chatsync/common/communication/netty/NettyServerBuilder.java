package io.xsun.minecraft.chatsync.common.communication.netty;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.json.JsonObjectDecoder;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.Slf4JLoggerFactory;
import io.xsun.minecraft.chatsync.common.communication.IServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class NettyServerBuilder {

    public static final NettyServerBuilder INSTANCE = new NettyServerBuilder();
    private static final Logger LOG = LoggerFactory.getLogger(NettyServerBuilder.class);
    private static final Gson GSON = new GsonBuilder().create();
    private static final ByteToMessageDecoder JSON_DECODER = new JsonObjectDecoder() {
        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
            List<Object> out0 = new ArrayList<>();
            super.decode(ctx, in, out0);
            out0.forEach(
                    buf -> out.add(GSON.fromJson(((ByteBuf) buf).toString(StandardCharsets.UTF_8), JsonObject.class))
            );
        }
    };
    private static final MessageToByteEncoder<JsonObject> JSON_ENCODER = new MessageToByteEncoder<JsonObject>() {
        @Override
        protected void encode(ChannelHandlerContext ctx, JsonObject msg, ByteBuf out) throws Exception {
            out.writeCharSequence(GSON.toJson(msg), StandardCharsets.UTF_8);
        }
    };

    static {
        InternalLoggerFactory.setDefaultFactory(Slf4JLoggerFactory.INSTANCE);
    }

    private final EventLoopGroup boss = new NioEventLoopGroup(), worker = new NioEventLoopGroup();

    public IServer<JsonObject> newNettyTcpJsonServer(int port) {
        LOG.debug("Creating new netty tcp json server on port {}.", port);
        return new NettyServer<>(boss, worker, port, JSON_DECODER, JSON_ENCODER);
    }

    public final void close() {
        boss.shutdownGracefully();
        worker.shutdownGracefully();
    }

}
