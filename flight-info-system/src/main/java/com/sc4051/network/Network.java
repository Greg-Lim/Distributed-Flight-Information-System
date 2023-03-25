package com.sc4051.network;

import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.LinkedList;
import java.util.List;

import com.sc4051.entity.Message;

import lombok.Getter;

@Getter
public abstract class Network {
    UDPCommunicator udpCommunicator;

    SocketAddress replyAddress;
    int messageID;

    public Network(UDPCommunicator udpCommunicator){
        this.udpCommunicator = udpCommunicator;
    }

    public void send(Message message, SocketAddress socketAddress){
        List<Byte> byteList = new LinkedList<Byte>();
        message.marshall(byteList);
        udpCommunicator.sendMessage(byteList, socketAddress);
    }

    public Message recieve() throws SocketTimeoutException, CacheHandledReply{
        List<Byte> byteList = new LinkedList<Byte>();
        try{
            byteList = udpCommunicator.recieveMessage();
            replyAddress = udpCommunicator.getReplyAddress();
        } catch (SocketTimeoutException e){
            // System.out.println(e);
            throw e;
        }
        Message message = new Message(byteList);
        messageID = message.getID();
        return message;
    }

    public Message recieve(int customeTimeout) throws SocketTimeoutException, CacheHandledReply{
        List<Byte> byteList = new LinkedList<Byte>();
        try{
            byteList = udpCommunicator.recieveMessage(customeTimeout);
            replyAddress = udpCommunicator.getReplyAddress();
        } catch (SocketTimeoutException e){
            // System.out.println(e);
            throw e;
        }
        Message message = new Message(byteList);
        messageID = message.getID();
        return message;
    }

    public void sendReply(Message replyMessage){
        List<Byte> byteList = new LinkedList<Byte>();
        replyMessage.marshall(byteList);
        udpCommunicator.sendMessage(byteList, replyAddress);
    }

    abstract public Message sendAndRecieve(Message message, SocketAddress socketAddress) throws NoReplyException;

    // public Message recieveAndAck(int customeTimeout) throws SocketTimeoutException, CacheHandledReply{
    //     List<Byte> byteList = new LinkedList<Byte>();
    //     try{
    //         byteList = udpCommunicator.recieveMessage(customeTimeout);
    //     } catch (SocketTimeoutException e){
    //         // System.out.println(e);
    //         throw e;
    //     }
    //     Message message = new Message(byteList);
    //     Message ack = new Message(0, message.getID()+1, message.getType()*10+1, "ack");
    //     send(ack, replyAddress);
    //     messageID = message.getID();
    //     return message;
    // }   

}
