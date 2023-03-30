package com.sc4051.network;

import java.net.SocketAddress;
import java.util.List;

/**
 * A UDPCommunicator implementation that simulates poor network conditions by randomly discarding packets.
 */
public class PoorUDPCommunicator extends UDPCommunicator {

    /**
     * The probability that a packet will be sent.
     */
    private double sendProbibility;

    /**
     * Constructs a new PoorUDPCommunicator.
     *
     * @param socketAddress the address of the remote host
     * @param timeoutTime the timeout time in milliseconds
     * @param sendProbibility the probability that a packet will be sent
     * @throws NetworkErrorException if there is an error initializing the network
     */
    public PoorUDPCommunicator(SocketAddress socketAddress, int timeoutTime, double sendProbibility) throws NetworkErrorException {
        super(socketAddress, timeoutTime);
        this.sendProbibility = sendProbibility;
    }

    /**
     * Sends a message if it is determined that it should be sent based on the send probability.
     * This is the simulated fail to send function
     *
     * @param byteList the list of bytes to send
     * @param socketAddress the address of the remote host
     */
    @Override
    public void sendMessage(List<Byte> byteList, SocketAddress socketAddress){
        if(isToSend()){
            super.sendMessage(byteList, socketAddress);
        }
    }

    /**
     * Determines if a packet should be sent based on the send probability.
     *
     * @return true if a packet should be sent, false otherwise
     */
    public boolean isToSend(){
        boolean toSend = Math.random() < sendProbibility;
        return toSend;
    }
}
