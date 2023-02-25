package com.sc4051.entity;

import java.net.SocketAddress;

import lombok.*;

@Getter
@Setter
public class ClientInfo {
    int queryID;
    SocketAddress socketAddress;
    long timeout;

    public ClientInfo(int queryID, SocketAddress socketAddress, long timeoutMS){
        timeout = timeout + System.currentTimeMillis();
    }
}
