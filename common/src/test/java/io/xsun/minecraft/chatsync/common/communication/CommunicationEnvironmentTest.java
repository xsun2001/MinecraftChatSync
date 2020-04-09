package io.xsun.minecraft.chatsync.common.communication;

import com.google.gson.JsonObject;
import io.xsun.minecraft.chatsync.common.communication.netty.NettyEnvironment;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;

import static io.xsun.minecraft.chatsync.common.communication.TransferProtocol.TCP;
import static io.xsun.minecraft.chatsync.common.communication.TransferProtocol.WEBSOCKET;

class CommunicationEnvironmentTest {

    CommunicationEnvironment env;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        env = NettyEnvironment.defaultEnv();
    }

    private void runChannel(IChannel channel) {
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                JsonObject json = new JsonObject();
                json.addProperty("index", i);
                channel.send(json);
                System.out.println("Sending: " + i);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            channel.close();
        }).start();
    }

    @Test
    void testTcp() throws InterruptedException {
        InetSocketAddress addr = new InetSocketAddress(8088);
        IServer server = env.bind(TCP, addr);
        CountDownLatch cdl = new CountDownLatch(1);
        server.setOnChannelConnected(channel -> channel.setMessageHandler(json -> System.out.println("Got: " + json.get("index").getAsInt())));
        server.setOnChannelDisconnected(channel -> cdl.countDown());
        IChannel channel = env.connect(TCP, addr);
        runChannel(channel);
        cdl.await();
        server.close();
    }

    @Test
    void testWebsocket() throws InterruptedException {
        InetSocketAddress addr = new InetSocketAddress(8088);
        IServer server = env.bind(WEBSOCKET, addr);
        CountDownLatch cdl = new CountDownLatch(1);
        server.setOnChannelConnected(channel -> channel.setMessageHandler(json -> System.out.println("Got: " + json.get("index").getAsInt())));
        server.setOnChannelDisconnected(channel -> cdl.countDown());
        IChannel channel = env.connect(WEBSOCKET, addr);
        runChannel(channel);
        cdl.await();
        server.close();
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        env.shutdown();
    }
}