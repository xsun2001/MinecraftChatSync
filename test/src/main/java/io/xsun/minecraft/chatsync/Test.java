package io.xsun.minecraft.chatsync;

import io.xsun.minecraft.chatsync.common.communication.*;
import io.xsun.minecraft.chatsync.common.communication.insideprotocol.InsideProtocolResolver;
import io.xsun.minecraft.chatsync.common.communication.insideprotocol.message.MessageBase;
import io.xsun.minecraft.chatsync.common.communication.insideprotocol.message.MessageSourceInfo;
import io.xsun.minecraft.chatsync.common.communication.insideprotocol.message.player.PlayerDeadMessage;
import io.xsun.minecraft.chatsync.common.communication.netty.NettyEnvironment;
import io.xsun.minecraft.chatsync.common.logging.CSLogger;
import io.xsun.minecraft.chatsync.common.logging.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;

public class Test {

    public static void main(String[] args) throws InterruptedException {
        LogManager.setLogManagerFactory(Log4j2LogManager::new);
        CSLogger log = LogManager.getInstance().getLogger(Test.class);
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

    public static class Log4j2CSLoggerAdapter implements CSLogger {

        private final Logger log4j2Impl;

        public Log4j2CSLoggerAdapter(Logger log4j2Impl) {
            this.log4j2Impl = log4j2Impl;
        }

        @Override
        public void trace(String msg) {
            log4j2Impl.trace(msg);
        }

        @Override
        public void debug(String msg) {
            log4j2Impl.debug(msg);
        }

        @Override
        public void info(String msg) {
            log4j2Impl.info(msg);
        }

        @Override
        public void warn(String msg) {
            log4j2Impl.warn(msg);
        }

        @Override
        public void warn(String msg, Throwable error) {
            log4j2Impl.warn(msg, error);
        }

        @Override
        public void error(String msg) {
            log4j2Impl.error(msg);
        }

        @Override
        public void error(String msg, Throwable error) {
            log4j2Impl.error(msg, error);
        }
    }

    public static class Log4j2LogManager extends LogManager {

        @Override
        public CSLogger getLogger(Class<?> clazz) {
            return new Log4j2CSLoggerAdapter(org.apache.logging.log4j.LogManager.getLogger(clazz));
        }
    }

}
