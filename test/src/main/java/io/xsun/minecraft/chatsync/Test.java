package io.xsun.minecraft.chatsync;

import io.xsun.minecraft.chatsync.common.LogManager;
import io.xsun.minecraft.chatsync.common.communication.*;
import io.xsun.minecraft.chatsync.common.communication.insideprotocol.InsideProtocolResolver;
import io.xsun.minecraft.chatsync.common.communication.insideprotocol.message.MessageBase;
import io.xsun.minecraft.chatsync.common.communication.insideprotocol.message.MessageSourceInfo;
import io.xsun.minecraft.chatsync.common.communication.insideprotocol.message.player.PlayerDeadMessage;
import io.xsun.minecraft.chatsync.common.communication.netty.NettyEnvironment;
import org.slf4j.Logger;

import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;

public class Test {

    public static void main(String[] args) throws InterruptedException {
        Logger log = LogManager.getInstance().getLogger(Test.class);
        InsideProtocolResolver resolver = new InsideProtocolResolver();

        PlayerDeadMessage msg = PlayerDeadMessage.builder()
                .type("player_dead")
                .deadReason("test")
                .playerName("xsun2001")
                .source(
                        MessageSourceInfo.builder()
                                .name("test")
                                .version("1.1.1")
                                .side(MessageSourceInfo.SourceSide.INSIDE_NODE)
                                .build()
                )
                .build();

        CountDownLatch cdl = new CountDownLatch(1);
        CommunicationEnvironment env = NettyEnvironment.defaultEnv();
        env.init();
        ServerFactory serverFactory = env.getServerFactory();
        ClientFactory clientFactory = env.getClientFactory();

        IServer<MessageBase> server = ProtocolUtility.wrapJsonServer(serverFactory.newTcpJsonServer(8080), resolver);
        server.setOnChannelConnected(channel -> channel.setMessageHandler(message -> log.info("Receive: " + message.toString())));
        server.setOnChannelDisconnected(channel -> cdl.countDown());

        IChannel<MessageBase> client = ProtocolUtility.wrapJsonChannel(clientFactory.connectToTcpJsonServer(new InetSocketAddress("localhost", 8080)), resolver);
        log.info("Send: " + msg.toString());
        client.send(msg);
        client.close();

        cdl.await();
        server.close();
        env.shutdown();
    }

}
