package com.sc4051.network;

import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.HashMap;

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
        if(cache.containsKey(hashMapKey)){
            System.out.println("key match response from cache");
            super.sendReply(cache.get(hashMapKey));
            throw new CacheHandledReply();
        }

        return message;
    }


    public void sendReply(Message replyMessage){
        String hashMapKey = super.replyAddress.toString();
        hashMapKey = hashMapKey.concat(Integer.toString(super.messageID));

        cache.put(hashMapKey, replyMessage);
        // System.out.println("Cache: ");
        // System.out.println(cache);

        
        super.sendReply(replyMessage);
    }
}