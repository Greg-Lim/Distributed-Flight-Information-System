package com.sc4051.network;

import java.net.SocketAddress;
import java.net.SocketTimeoutException;

import com.sc4051.entity.Message;


public class AtleastOnceNetwork extends Network{
    int maxAttempts;

    /**
     * Constructs a new instance of AtleastOnceNetwork with the specified {@link UDPCommunicator}
     * and the maximum number of attempts to send a message.
     *
     * @param udpCommunicator the UDPCommunicator to use for sending and receiving messages
     * @param maxAttempts the maximum number of attempts to send a message
     */
    public AtleastOnceNetwork(UDPCommunicator udpCommunicator, int maxAttempts) {
        super(udpCommunicator);
        this.maxAttempts = maxAttempts;
    }

    /**
     * Sends a message to the specified {@link SocketAddress} and waits for a reply message. If no
     * reply is received, this method will attempt to resend the message up to the maximum number of
     * attempts specified in the constructor.
     *
     * @param message the message to send
     * @param socketAddress the address to which to send the message
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
}
