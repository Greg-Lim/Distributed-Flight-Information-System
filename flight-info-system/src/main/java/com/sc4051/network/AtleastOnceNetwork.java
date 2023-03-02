package com.sc4051.network;

import java.net.SocketAddress;
import java.net.SocketTimeoutException;

import com.sc4051.entity.Message;

public class AtleastOnceNetwork extends Network{

    public AtleastOnceNetwork(UDPCommunicator udpCommunicator) {
        super(udpCommunicator);
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
                if (attempts>=5) break;
                System.out.printf("Resend attempt %d: ", attempts);
            }
        }

        throw new NoReplyException();
    }
    
}
