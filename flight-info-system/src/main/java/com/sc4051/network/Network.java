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

    /**
     * Constructor for the Network class.
     *
     * @param udpCommunicator the UDPCommunicator instance to use for communication
     */
    public Network(UDPCommunicator udpCommunicator){
        this.udpCommunicator = udpCommunicator;
    }

    /**
     * Sends a message to a given socket address.
     *
     * @param message the message to send
     * @param socketAddress the socket address to which to send the message
     */
    public void send(Message message, SocketAddress socketAddress){
        List<Byte> byteList = new LinkedList<Byte>();
        message.marshall(byteList);
        udpCommunicator.sendMessage(byteList, socketAddress);
    }

    /**
     * Receives a message.
     *
     * @return the received message
     * @throws SocketTimeoutException if a message is not received within the timeout period
     * @throws CacheHandledReply if the received message is a cached reply
     */
    public Message recieve() throws SocketTimeoutException, CacheHandledReply{
        List<Byte> byteList = new LinkedList<Byte>();
        try{
            byteList = udpCommunicator.recieveMessage();
            replyAddress = udpCommunicator.getReplyAddress();
        } catch (SocketTimeoutException e){
            throw e;
        }
        Message message = new Message(byteList);
        messageID = message.getID();
        return message;
    }

    /**
     * Receives a message with a custom timeout period.
     *
     * @param customeTimeout the custom timeout period in milliseconds
     * @return the received message
     * @throws SocketTimeoutException if a message is not received within the timeout period
     * @throws CacheHandledReply if the received message is a cached reply
     */
    public Message recieve(int customeTimeout) throws SocketTimeoutException, CacheHandledReply{
        List<Byte> byteList = new LinkedList<Byte>();
        try{
            byteList = udpCommunicator.recieveMessage(customeTimeout);
            replyAddress = udpCommunicator.getReplyAddress();
        } catch (SocketTimeoutException e){
            throw e;
        }
        Message message = new Message(byteList);
        messageID = message.getID();
        return message;
    }

    /**
     * Sends a reply to the reply address stored in the object.
     *
     * @param replyMessage the message to send as a reply
     */
    public void sendReply(Message replyMessage){
        List<Byte> byteList = new LinkedList<Byte>();
        replyMessage.marshall(byteList);
        udpCommunicator.sendMessage(byteList, replyAddress);
    }

    /**
     * Sends a message to a given socket address and receives a reply.
     *
     * @param message the message to send
     * @param socketAddress the socket address to which to send the message
     * @return the received reply message
     * @throws NoReplyException if a reply is not received within the timeout period
     */
    abstract public Message sendAndRecieve(Message message, SocketAddress socketAddress) throws NoReplyException; 
}
