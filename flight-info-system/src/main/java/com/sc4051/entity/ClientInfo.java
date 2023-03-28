/**
 * Represents information about a client for callback.
 */
package com.sc4051.entity;

import java.net.SocketAddress;

import lombok.*;

@Getter
@Setter
@ToString
public class ClientInfo {
    /**
     * The query ID, used for callback acknowledgement.
     */
    int queryID;
    
    /**
     * The client's socket address.
     */
    SocketAddress socketAddress;
    
    /**
     * The timeout value in milliseconds.
     */
    long timeout;

    /**
     * Constructs a new ClientInfo object with the specified query ID, socket address, and timeout value.
     * @param queryID the query ID.
     * @param socketAddress the client's socket address.
     * @param timeoutMS the timeout value in milliseconds.
     */
    public ClientInfo(int queryID, SocketAddress socketAddress, long timeoutMS){
        this.queryID = queryID;
        this.socketAddress = socketAddress;
        this.timeout = timeoutMS + System.currentTimeMillis();
    }

    /**
     * Returns true if the client has timed out, false otherwise.
     * @return true if the client has timed out, false otherwise.
     */
    public boolean isTimeout(){
        return System.currentTimeMillis()>timeout;
    }
}
