package com.sc4051.client;

import java.net.*;
import java.util.HexFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import com.google.common.primitives.Bytes;
import com.sc4051.entity.FlightInfo;
import com.sc4051.entity.Message;
import com.sc4051.entity.messageFormats.QueryFlightID;
import com.sc4051.entity.messageFormats.QueryFlightSrcnDest;
import com.sc4051.marshall.CustomMarshaller;
import com.sc4051.marshall.MarshallUtils;
import com.sc4051.marshall.Marshaller;

import java.io.*;
public class Client{
public static void main(String args[]){
    Scanner sc = new Scanner(System.in);
    int serverPort = 6789;

    DatagramSocket aSocket = null;
    InetAddress aHost = null;
    try{
        aHost = InetAddress.getLocalHost();
        aSocket = new DatagramSocket();
    } catch(Exception e){
        System.out.println( e.toString());
        return;
    }

    int messageID = 0;

    while(true){
        ClientView.printMenu();
        messageID +=1;

        int choice = ClientView.getUserChoice();

        switch(choice){
            case 0: // ping
                try{
                    List<Byte> messageBody = Bytes.asList(HexFormat.ofDelimiter(":").parseHex("00:00:00:05:68:65:6c:6c:6f"));//5 hello
                    Message requestBody = new Message(999,999,0, messageBody);
                    DatagramPacket request = new DatagramPacket(requestBody.marshall(), requestBody.marshall().length, aHost, serverPort);
                    aSocket.send(request);

                    byte[] replyBuffer = new byte[100];
                    DatagramPacket reply = new DatagramPacket(replyBuffer, replyBuffer.length);
                    aSocket.receive(reply);

                    List<Byte> byteList = new LinkedList<Byte>(Bytes.asList(reply.getData()));
                    Message message = new Message(byteList);
                    String hello = MarshallUtils.unmarshallString(message.getBody());
                    System.out.println(hello);
                } catch(IOException e){System.out.println(e);}
                break;
            case 1: //Search given src and dest
                try{
                    String[] t = ClientView.getSrcnDest();
                    String src = t[0];
                    String dest = t[1];
                    QueryFlightSrcnDest queryFlight = new QueryFlightSrcnDest(src, dest);
                    Message requestBody = new Message(messageID, 0, 1, queryFlight.marshall());
                    System.out.println(requestBody);
                    DatagramPacket request = new DatagramPacket(requestBody.marshall(), requestBody.marshall().length, aHost, serverPort);
                    aSocket.send(request);

                    byte[] replyBuffer = new byte[100];
                    DatagramPacket reply = new DatagramPacket(replyBuffer, replyBuffer.length);
                    aSocket.receive(reply);

                    List<Byte> byteList = new LinkedList<Byte>(Bytes.asList(reply.getData()));
                    Message message = new Message(byteList);
                    System.out.println(message);
                    if (message.isErr()){
                        message.printErr();
                    } else {
                        List<FlightInfo> flightInfoList = CustomMarshaller.unmarshallFlightList(message.getBody());
                        System.out.println(flightInfoList.toString());
                    }

                } catch(IOException e){System.out.println(e);}
                break;
            case 2: //
                try{
                    int id = ClientView.getFlightID();

                    QueryFlightID queryFlight = new QueryFlightID(id);
                    Message requestBody = new Message(messageID, 0, 2, queryFlight.marshall());
                    System.out.println(requestBody);
                    DatagramPacket request = new DatagramPacket(requestBody.marshall(), requestBody.marshall().length, aHost, serverPort);
                    aSocket.send(request);

                    byte[] replyBuffer = new byte[100];
                    DatagramPacket reply = new DatagramPacket(replyBuffer, replyBuffer.length);
                    aSocket.receive(reply);

                    List<Byte> byteList = new LinkedList<Byte>(Bytes.asList(reply.getData()));
                    Message message = new Message(byteList);
                    if (message.isErr()){
                        message.printErr();
                    } else {
                        List<FlightInfo> flightInfoList = CustomMarshaller.unmarshallFlightList(message.getBody());
                        System.out.println(flightInfoList.get(0).toString());
                    }
                } catch(IOException e){System.out.println(e);}
                break;
                
            default:
                System.out.println("***** Invalid Input *****");
            }
            

        }

        // byte[] bytes = HexFormat.ofDelimiter(":").parseHex("00:00:00:00:00:00:00:05:68:65:6c:6c:6f");// 0, 5, hello

        // DatagramSocket aSocket = null;
        // try {
        //     aSocket = new DatagramSocket();
        //     byte[] m = bytes;
        //     InetAddress aHost = InetAddress.getLocalHost();

        //     int serverPort = 6789;

        //     DatagramPacket request = new DatagramPacket(m, m.length, aHost, serverPort);
        //     aSocket.send(request);

        //     byte[] buffer = new byte[1000];

        //     DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
        //     aSocket.receive(reply);
            
        //     List<Byte> byteList = new LinkedList<Byte>(Bytes.asList(reply.getData()));
        //     System.out.println(byteList);

        //     System.out.println("Reply: "+ new String(reply.getData()));
        // } catch (Exception e){System.out.println("err::");} //handle exceptions

        // if (aSocket != null) aSocket.close();
    }
}