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
    static final int bufferSize = 1000;
    DatagramSocket socket = null;
    InetAddress host = null;
    int timeOutTime = 1000;
    SocketAddress replyAddress;
    double sendProbibility;

    
    /**
     * Constructor for UDPCommunicator with timeout time.
     *
     * @param socketAddress the address of the socket to be used
     * @param timeOutTime the timeout time in milliseconds
     * @throws NetworkErrorException if an error occurs during the creation of the DatagramSocket
     */
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

    // /**
    //  * Constructor for UDPCommunicator with timeout time and send probability.
    //  *
    //  * @param socketAddress the address of the socket to be used
    //  * @param timeOutTime the timeout time in milliseconds
    //  * @param sendProbibility the probability of successfully sending a message (0.0 to 1.0)
    //  * @throws NetworkErrorException if an error occurs during the creation of the DatagramSocket
    //  */
    // public UDPCommunicator(SocketAddress socketAddress, int timeOutTime, double sendProbibility) throws NetworkErrorException{
    //     this.timeOutTime = timeOutTime;
    //     this.sendProbibility = sendProbibility;
    //     try{
    //         host = InetAddress.getLocalHost();
    //         socket = new DatagramSocket(socketAddress);
    //     } catch(Exception e){
    //         System.out.println(e.toString());
    //         throw new NetworkErrorException();
    //     }
    // }

    /**
     * Sends a message to the specified socket address.
     *
     * @param byteList the list of bytes to send as the message
     * @param socketAddress the address of the socket to send the message to
     */
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

    /**
     * Receives a message from the socket.
     *
     * @return the list of bytes received as the message
     * @throws SocketTimeoutException if the timeout time is reached while waiting for a message
     */
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

    /**
     * Receives a message from the socket with a custom timeout
     *
     * @param customTimeout a custom timeout time in miliseconds
     * @return the list of bytes received as the message
     * @throws SocketTimeoutException if the timeout time is reached while waiting for a message
     */
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
