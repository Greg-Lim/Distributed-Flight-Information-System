package com.sc4051.network;

import java.net.DatagramSocket;
import java.net.InetAddress;

public class Network {
    int destinationPort;
    DatagramSocket socket = null;
    InetAddress host = null;

    int messageID;

    public Network(){
        try{
            host = InetAddress.getLocalHost();
            socket = new DatagramSocket();
        } catch(Exception e){
            System.out.println( e.toString());
            return;
        }
    }
    
    
}
