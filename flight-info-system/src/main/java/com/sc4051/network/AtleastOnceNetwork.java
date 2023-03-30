package com.sc4051.network;


public class AtleastOnceNetwork extends Network{
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

}
