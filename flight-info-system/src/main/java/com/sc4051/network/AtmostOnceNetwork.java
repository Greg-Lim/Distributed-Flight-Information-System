package com.sc4051.network;

import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.HashMap;

import com.sc4051.entity.Message;

/**
 * The AtmostOnceNetwork class extends the Network class to provide at-most-once
 * semantics to the communication between a client and a server. It maintains a
 * cache of the previously sent messages and their replies to prevent duplicate
 * requests.
 */
public class AtmostOnceNetwork extends Network{
    int maxAttempts;

    // Cache to store previously sent messages and their replies
    HashMap<String, Message> cache = new HashMap<String,Message>();

    /**
     * Constructs an AtmostOnceNetwork object with the given UDPCommunicator and
     * maximum number of attempts.
     *
     * @param udpCommunicator the UDPCommunicator to use for communication
     * @param maxAttempts the maximum number of attempts to send a message
     */
    public AtmostOnceNetwork(UDPCommunicator udpCommunicator, int maxAttempts ) {
        super(udpCommunicator);
        this.maxAttempts = maxAttempts;
    }

    /**
     * Sends the given message to the specified socket address and waits for a reply
     * message. If no reply is received after the maximum number of attempts, a
     * NoReplyException is thrown.
     *
     * @param message the message to send
     * @param socketAddress the address to send the message to
     * @return the reply message received
     * @throws NoReplyException if no reply is received after the maximum number of attempts
     */
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
            } catch (CacheHandledReply discard) {}
        }
        throw new NoReplyException();
    }

    /**
     * Waits for a message to be received. If the received message is a request and
     * there is a reply for it in the cache, the reply is sent and a CacheHandledReply
     * exception is thrown. Otherwise, the received message is returned.
     *
     * @return the received message
     * @throws SocketTimeoutException if no message is received before the timeout
     * @throws CacheHandledReply if the received message is a request and there is a reply
     * for it in the cache
     */
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

    /**
     * Sends the given reply message to the reply address and adds the message to the
     * cache.
     *
     * @param replyMessage the reply message to send
     */
    public void sendReply(Message replyMessage){
        String hashMapKey = super.replyAddress.toString();
        hashMapKey = hashMapKey.concat(Integer.toString(super.messageID));

        cache.put(hashMapKey, replyMessage);
        super.sendReply(replyMessage);
    }
}
