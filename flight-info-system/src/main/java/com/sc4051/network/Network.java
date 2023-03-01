package com.sc4051.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.LinkedList;
import java.util.List;

import com.google.common.primitives.Bytes;
import com.sc4051.entity.Message;
import com.sc4051.marshall.MarshallUtils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Network {
    // int destinationPort;
    DatagramSocket socket = null;
    InetAddress host = null;
    int replyPort;

    // int messageID;

    public Network(int port) throws NetworkErrorException{
        try{
            host = InetAddress.getLocalHost();
            socket = new DatagramSocket(port);
        } catch(Exception e){
            System.out.println( e.toString());
            throw new NetworkErrorException();
        }
    }

    //Simulate network here
    public void sendMessage(Message message, int destinationPort){
        List<Byte> byteList = new LinkedList<Byte>();
        message.marshall(byteList);
        byte[] bytes = Bytes.toArray(byteList);
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, host, destinationPort);
        try{
            socket.send(packet);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void sendReply(Message message){
        List<Byte> byteList = new LinkedList<Byte>();
        message.marshall(byteList);
        byte[] bytes = Bytes.toArray(byteList);
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, host, replyPort);
        try{
            socket.send(packet);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public Message recieveMessage(){
        System.out.println("*** here just after call recieve");
        byte[] recieveBuffer = new byte[100];
        DatagramPacket packet = new DatagramPacket(recieveBuffer, recieveBuffer.length);
        try{
            System.out.println("*** here just before recieve");
            socket.receive(packet);
        } catch (IOException e) {System.out.println(e);}
        List<Byte> byteList = new LinkedList<Byte>(Bytes.asList(packet.getData()));
        replyPort = packet.getPort();
        Message message = new Message(byteList);
        return message;
    }
    
}
