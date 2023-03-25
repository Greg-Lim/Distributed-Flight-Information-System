package com.sc4051.network;

import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.sc4051.entity.Message;

public class AtmostOnceNetwork extends Network{
    int maxAttempts;

    HashMap<String, Message> cache = new HashMap<String,Message>();
    //string to check is address.tostring + messageID

    public AtmostOnceNetwork(UDPCommunicator udpCommunicator, int maxAttempts ) {
        super(udpCommunicator);
        this.maxAttempts = maxAttempts;

    }

    public Message sendAndRecieve(Message message, SocketAddress socketAddress) throws NoReplyException{
        boolean noReply = true;
        int attempts = 0;
        while(noReply){
            System.out.print("Sending Request ... ");
            send(message, socketAddress);
            try{
                Message reply = recieve();
                System.out.println(""); //just to make a new line
                return reply;
            } catch (SocketTimeoutException e){
                System.out.println("Request Timeout");
                attempts+=1;
                if (attempts>=maxAttempts) break;
                System.out.printf("Resend attempt %d: ", attempts);
            } catch (CacheHandledReply _) {}
        }
        throw new NoReplyException();
    }

    public Message recieve() throws SocketTimeoutException, CacheHandledReply{
        Message message = super.recieve();

        String hashMapKey = super.replyAddress.toString();
        hashMapKey = hashMapKey.concat(Integer.toString(super.getMessageID()));
        if(cache.containsKey(hashMapKey) & message.isRequest()){
            Message reply = cache.get(hashMapKey);
            System.out.println(String.format("Message match cache ip:%s, ID:%d, replying with message id %d",super.replyAddress.toString(),super.getMessageID(), reply.getID()));//TODO: need to change log message
            super.sendReply(reply);
            throw new CacheHandledReply();
        }
        return message;
    }


    public void sendReply(Message replyMessage){
        String hashMapKey = super.replyAddress.toString();
        hashMapKey = hashMapKey.concat(Integer.toString(super.messageID));

        cache.put(hashMapKey, replyMessage);
        super.sendReply(replyMessage);
    }

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
    //     super.send(ack, replyAddress);
    //     messageID = message.getID();
    //     return message;
    // }
}
