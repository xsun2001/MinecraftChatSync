package io.xsun.minecraft.chatsync.insidenode.common;

import io.xsun.minecraft.chatsync.common.communication.*;
import io.xsun.minecraft.chatsync.common.communication.insideprotocol.InsideIProtocolResolver;
import io.xsun.minecraft.chatsync.common.communication.insideprotocol.message.ChatMessage;
import io.xsun.minecraft.chatsync.common.communication.insideprotocol.message.MessageBase;
import io.xsun.minecraft.chatsync.common.communication.insideprotocol.message.MessageSourceInfo;
import org.slf4j.Logger;

import java.net.InetSocketAddress;
import java.util.Timer;
import java.util.TimerTask;

public class InsideNode {
    private final Logger log;
    private final InsideNodeConfig config;
    private final IMinecraftServerApi mcServerApi;
    private final ClientFactory clientFactory;
    private final IProtocolResolver<MessageBase> resolver;
    private IChannel<MessageBase> channel;


    public InsideNode(InsideNodeConfig config, IMinecraftServerApi mcServerApi, CommunicationEnvironment commEnv) {
        this.config = config;
        this.mcServerApi = mcServerApi;
        this.log = mcServerApi.getLogger();
        this.clientFactory = commEnv.getClientFactory();
        this.resolver = new InsideIProtocolResolver();
        connectToMaster();
        initChannel();
    }

    public void sendMessage(MessageBase msg){
        if(channel != null){
            channel.send(msg);
        }else {
            log.warn("Discarding message [{}] because channel is not available", msg);
        }
    }

    private void connectToMaster() {
        String masterHost = config.getMasterHost();
        int masterPort = config.getMasterPort();
        log.info("Connecting to Master [{}:{}]", masterHost, masterPort);
        channel = ProtocolUtility.wrapJsonChannel(
                clientFactory.connectToTcpJsonServer(
                        new InetSocketAddress(
                                masterHost,
                                masterPort
                        )
                ),
                resolver
        );
        log.info("Connected to master");
    }

    private void initChannel() {
        channel.setMessageHandler(this::onChannelReceivedMessage);
        channel.setCloseHandler(this::onChannelClosed);
        channel.setExceptionHandler(this::onChannelError);
    }

    private boolean onChannelError(Throwable error) {
        log.error("Unexpected error", error);
        return false;
    }

    private void onChannelReceivedMessage(MessageBase msg) {
        log.debug("Receive message: {}", msg);
        String typeStr = msg.getType();
        MessageSourceInfo sourceInfo = msg.getSource();
        if ("chat".equals(typeStr)) {
            ChatMessage chatMessage = (ChatMessage) msg;
            log.info("Chat message: [{}]:{}", chatMessage.getSenderName(), chatMessage.getChatMessage());
            mcServerApi.broadcastFormattedMessage("chat", sourceInfo.getName(), chatMessage.getSenderName(), chatMessage.getChatMessage());
        } else {
            log.warn("Unsupported message type: {}", typeStr);
        }
    }

    private void onChannelClosed() {
        log.warn("Channel closed");
        channel = null;
        if (mcServerApi.isRunning()) {
            int reconnectInterval = config.getReconnectInterval();
            if (reconnectInterval == 0) {
                log.warn("Reconnect");
                new Thread(this::connectToMaster).start();
            } else if (reconnectInterval > 0) {
                log.warn("Reconnect after {} seconds", reconnectInterval);
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        connectToMaster();
                    }
                }, reconnectInterval);
            } else {
                log.warn("Auto reconnect is disabled");
            }
        }
    }
}
