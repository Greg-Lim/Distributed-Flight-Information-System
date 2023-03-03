package com.sc4051.entity;

import java.net.SocketAddress;

import lombok.*;

@Getter
@Setter
public class ClientInfo {
    int queryID; // This is for callback ack;
    SocketAddress socketAddress;
    long timeout;

    public ClientInfo(int queryID, SocketAddress socketAddress, long timeoutMS){
        this.queryID = queryID;
        this.socketAddress = socketAddress;
        timeout = timeout + System.currentTimeMillis();
    }

    public boolean isTimeout(){
        return false;
        //return timeout<System.currentTimeMillis();
    }
}
