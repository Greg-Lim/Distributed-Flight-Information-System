package com.sc4051.network;

import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import com.google.common.primitives.Bytes;
import com.sc4051.entity.ClientInfo;
import com.sc4051.entity.Message;

public abstract class Network {
    UDPCommunicator udpCommunicator;
    List<List<Bytes>> MessageHistory;
    List<Integer> MessageSource;

    SocketAddress replyAddress;

    public Network(UDPCommunicator udpCommunicator){
        this.udpCommunicator = udpCommunicator;
    }

    public void send(Message message, SocketAddress socketAddress){
        List<Byte> byteList = new LinkedList<Byte>();
        message.marshall(byteList);
        udpCommunicator.sendMessage(byteList, socketAddress);
    }

    public Message recieve() throws SocketTimeoutException{
        List<Byte> byteList = new LinkedList<Byte>();
        try{
            byteList = udpCommunicator.recieveMessage();
            replyAddress = udpCommunicator.getReplyAddress();
        } catch (SocketTimeoutException e){
            // System.out.println(e);
            throw e;
        }
        Message message = new Message(byteList);
        return message;
    }

    public void sendReply(Message replyMessage){
        List<Byte> byteList = new LinkedList<Byte>();
        replyMessage.marshall(byteList);
        udpCommunicator.sendMessage(byteList, replyAddress);
    }

    // abstract public Message sendAndRecieve(Message message, int destinationPort);
    
}
