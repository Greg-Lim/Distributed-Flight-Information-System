package com.sc4051.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.LinkedList;
import java.util.List;

import com.google.common.primitives.Bytes;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UDPCommunicator {
    // int destinationPort;
    static final int bufferSize = 1000;
    DatagramSocket socket = null;
    InetAddress host = null;
    int timeOutTime = 1000;
    SocketAddress replyAddress;
    double sendProbibility;

    public UDPCommunicator(SocketAddress socketAddress, int timeOutTime) throws NetworkErrorException{
        this.timeOutTime = timeOutTime;
        sendProbibility = 1;
        try{
            host = InetAddress.getLocalHost();
            socket = new DatagramSocket(socketAddress);
        } catch(Exception e){
            System.out.println(e.toString());
            throw new NetworkErrorException();
        }
    }

    public UDPCommunicator(SocketAddress socketAddress, int timeOutTime, double sendProbibility) throws NetworkErrorException{
        this.timeOutTime = timeOutTime;
        this.sendProbibility = sendProbibility;
        try{
            host = InetAddress.getLocalHost();
            socket = new DatagramSocket(socketAddress);
        } catch(Exception e){
            System.out.println(e.toString());
            throw new NetworkErrorException();
        }
    }

    //Simulate network here
    //VVV this method should be removed
    // public void sendMessage(List<Byte> byteList, int destinationPort){
    //     // List<Byte> byteList = new LinkedList<Byte>();
    //     // message.marshall(byteList);
    //     byte[] bytes = Bytes.toArray(byteList);
    //     DatagramPacket packet = new DatagramPacket(bytes, bytes.length, host, destinationPort);
    //     try{
    //         socket.send(packet);
    //     } catch (IOException e) {
    //         System.out.println(e);
    //     }
    // }

    public void sendMessage(List<Byte> byteList, SocketAddress socketAddress){
        // List<Byte> byteList = new LinkedList<Byte>();
        // message.marshall(byteList);
        byte[] bytes = Bytes.toArray(byteList);
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, socketAddress);
        try{
            socket.send(packet);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    // reply should be handled at network.java, session level
    // public void sendReply(List<Byte> byteList){
    //     // List<Byte> byteList = new LinkedList<Byte>();
    //     // message.marshall(byteList);
    //     byte[] bytes = Bytes.toArray(byteList);
    //     DatagramPacket packet = new DatagramPacket(bytes, bytes.length, host, replyPort);
    //     try{
    //         socket.send(packet);
    //     } catch (IOException e) {
    //         System.out.println(e);
    //     }
    // }

    public List<Byte> recieveMessage() throws SocketTimeoutException{
        // System.out.println("*** here just after call recieve");
        byte[] recieveBuffer = new byte[bufferSize];
        DatagramPacket packet = new DatagramPacket(recieveBuffer, recieveBuffer.length);
        try{
            // System.out.println("*** here just before recieve");
            if(timeOutTime>=0)
                socket.setSoTimeout(timeOutTime);
            socket.receive(packet);
        } catch (SocketTimeoutException e){
            throw e;
        } catch (IOException e) {
            System.out.println(e);
        }
        replyAddress = packet.getSocketAddress();
        List<Byte> byteList = new LinkedList<Byte>(Bytes.asList(packet.getData()));
        return byteList;
    }

    public List<Byte> recieveMessage(int customTimeout) throws SocketTimeoutException{
        // System.out.println("*** here just after call recieve");
        byte[] recieveBuffer = new byte[bufferSize];
        DatagramPacket packet = new DatagramPacket(recieveBuffer, recieveBuffer.length);
        try{
            // System.out.println("*** here just before recieve");
            if(timeOutTime>=0)
                socket.setSoTimeout(customTimeout);
            socket.receive(packet);
        } catch (SocketTimeoutException e){
            throw e;
        } catch (IOException e) {
            System.out.println(e);
        }
        replyAddress = packet.getSocketAddress();
        List<Byte> byteList = new LinkedList<Byte>(Bytes.asList(packet.getData()));
        return byteList;
    }

}
