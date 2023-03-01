package com.sc4051.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.LinkedList;
import java.util.List;

import com.google.common.primitives.Bytes;
import com.sc4051.entity.Message;
import com.sc4051.marshall.MarshallUtils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientNetwork {
    int destinationPort;
    DatagramSocket socket = null;
    InetAddress host = null;

    // int messageID;

    public ClientNetwork(int destinationPort) throws NetworkErrorException{
        // this.messageID = messageID;
        this.destinationPort = destinationPort;
        try{
            host = InetAddress.getLocalHost();
            socket = new DatagramSocket();
        } catch(Exception e){
            System.out.println( e.toString());
            throw new NetworkErrorException();
        }
    }

    //Simulate network here
    public void sendMessage(Message message){
        // messageID+=1;

        List<Byte> byteList = new LinkedList<Byte>();
        // MarshallUtils.marshallInt(messageID, byteList);

        message.marshall(byteList);

        byte[] bytes = Bytes.toArray(byteList);

        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, host, destinationPort);
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

        // messageID = MarshallUtils.unmarshallInt(byteList);

        // byte[] bytes = Bytes.toArray(byteList);

        Message message = new Message(byteList);

        return message;
    }
    
    
}
