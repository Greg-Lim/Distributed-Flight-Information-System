package com.sc4051.network;

import java.net.SocketAddress;
import java.util.List;

public class PoorUDPCommunicator extends UDPCommunicator {
    double sendProbibility;

    public PoorUDPCommunicator(SocketAddress socketAddress, int timeoutTime, double sendProbibility) throws NetworkErrorException {
        super(socketAddress, timeoutTime);
        this.sendProbibility = sendProbibility;
    }

    @Override
    public void sendMessage(List<Byte> byteList, SocketAddress socketAddress){
        if(isToSend()){
            super.sendMessage(byteList, socketAddress);
        }
    }

    public boolean isToSend(){
        boolean toSend = Math.random() < sendProbibility;
        // if(!toSend) System.out.println("Packet Lost");
        return toSend;
    }
    
}
