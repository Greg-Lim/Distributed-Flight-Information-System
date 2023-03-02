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
import com.sc4051.network.UDPCommunicator;
import com.sc4051.network.AtleastOnceNetwork;
import com.sc4051.network.NetworkErrorException;
import com.sc4051.network.PoorUDPCommunicator;

import java.io.*;
public class Client{
    Scanner sc = new Scanner(System.in);
    static AtleastOnceNetwork network;
    static int serverPort;
    static int sendingMessageID;
    static SocketAddress socketAddress;
    public static void main(String args[]){
        sendingMessageID=100;
        System.out.println("Connecting Client...");
        try{
            InetAddress address = InetAddress.getByName("localhost");
            int port = 6677;
            SocketAddress socketAddress = new InetSocketAddress(address, port);
            UDPCommunicator udpCommunicator = new PoorUDPCommunicator(socketAddress, 1000, 0.5); 
            network = new AtleastOnceNetwork(udpCommunicator);
        } catch (NetworkErrorException e) {
            System.out.println(e);
            return;
        } catch (UnknownHostException e) {
            System.out.println(e);
            return;
        }

        System.out.println("Client Ready");


        InetAddress address;
        try {
            address = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            System.out.println(e);
            return;
        }
        serverPort = 8899;
        SocketAddress serverAddresss = new InetSocketAddress(address, serverPort);

        while(true){
            sendingMessageID += 1;
            ClientView.printMenu();

            int choice = ClientView.getUserChoice();

            Message messageToSend;
            Message messageRecieve;

            switch(choice){
                case 0: //ping
                    List<Byte> messageBody = Bytes.asList(HexFormat.ofDelimiter(":").parseHex("00:00:00:05:68:65:6c:6c:6f"));//5 hello
                    messageToSend = new Message(999,999,0, messageBody);
                    try{
                        messageRecieve = network.sendAndRecieve(messageToSend, serverAddresss);
                    } catch (Exception e){
                        System.out.println(e);
                        continue;
                    }
                    String pingReplySting = MarshallUtils.unmarshallString(messageRecieve.getBody());
                    System.out.println(pingReplySting);
                    break;
                case 1: // Search given src and dest 
                    String[] t = ClientView.getSrcnDest();
                    String src = t[0];
                    String dest = t[1];
                    QueryFlightSrcnDest queryFlight = new QueryFlightSrcnDest(src, dest);
                    messageToSend = new Message(sendingMessageID, 0, 1, queryFlight.marshall());
                    try{
                        messageRecieve = network.sendAndRecieve(messageToSend, serverAddresss);
                    } catch (Exception e){
                        System.out.println(e);
                        continue;
                    }

                    if (messageRecieve.isErr()){
                        messageRecieve.printErr();
                    } else {
                        List<FlightInfo> flightInfoList = CustomMarshaller.unmarshallFlightList(messageRecieve.getBody());
                        System.out.println(flightInfoList.toString());
                    }

            }

            // switch(choice){
            //     case 0: // ping
            //         try{
            //             List<Byte> messageBody = Bytes.asList(HexFormat.ofDelimiter(":").parseHex("00:00:00:05:68:65:6c:6c:6f"));//5 hello
            //             Message requestBody = new Message(999,999,0, messageBody);
            //             DatagramPacket request = new DatagramPacket(requestBody.marshall(), requestBody.marshall().length, aHost, serverPort);
            //             aSocket.send(request);

            //             byte[] replyBuffer = new byte[100];
            //             DatagramPacket reply = new DatagramPacket(replyBuffer, replyBuffer.length);
            //             aSocket.receive(reply);

            //             List<Byte> byteList = new LinkedList<Byte>(Bytes.asList(reply.getData()));
            //             Message message = new Message(byteList);
            //             String hello = MarshallUtils.unmarshallString(message.getBody());
            //             System.out.println(hello);
            //         } catch(IOException e){System.out.println(e);}
            //         break;
            //     case 1: //Search given src and dest
            //         try{
            //             String[] t = ClientView.getSrcnDest();
            //             String src = t[0];
            //             String dest = t[1];
            //             QueryFlightSrcnDest queryFlight = new QueryFlightSrcnDest(src, dest);
            //             Message requestBody = new Message(messageID, 0, 1, queryFlight.marshall());
            //             System.out.println(requestBody);
            //             DatagramPacket request = new DatagramPacket(requestBody.marshall(), requestBody.marshall().length, aHost, serverPort);
            //             aSocket.send(request);

            //             byte[] replyBuffer = new byte[100];
            //             DatagramPacket reply = new DatagramPacket(replyBuffer, replyBuffer.length);
            //             aSocket.receive(reply);

            //             List<Byte> byteList = new LinkedList<Byte>(Bytes.asList(reply.getData()));
            //             Message message = new Message(byteList);
            //             System.out.println(message);
            //             if (message.isErr()){
            //                 message.printErr();
            //             } else {
            //                 List<FlightInfo> flightInfoList = CustomMarshaller.unmarshallFlightList(message.getBody());
            //                 System.out.println(flightInfoList.toString());
            //             }

            //         } catch(IOException e){System.out.println(e);}
            //         break;
            //     case 2: //
            //         try{
            //             int id = ClientView.getFlightID();

            //             QueryFlightID queryFlight = new QueryFlightID(id);
            //             Message requestBody = new Message(messageID, 0, 2, queryFlight.marshall());
            //             System.out.println(requestBody);
            //             DatagramPacket request = new DatagramPacket(requestBody.marshall(), requestBody.marshall().length, aHost, serverPort);
            //             aSocket.send(request);

            //             byte[] replyBuffer = new byte[100];
            //             DatagramPacket reply = new DatagramPacket(replyBuffer, replyBuffer.length);
            //             aSocket.receive(reply);

            //             List<Byte> byteList = new LinkedList<Byte>(Bytes.asList(reply.getData()));
            //             Message message = new Message(byteList);
            //             if (message.isErr()){
            //                 message.printErr();
            //             } else {
            //                 List<FlightInfo> flightInfoList = CustomMarshaller.unmarshallFlightList(message.getBody());
            //                 System.out.println(flightInfoList.get(0).toString());
            //             }
            //         } catch(IOException e){System.out.println(e);}
            //         break;
                    
            //     default:
            //         System.out.println("***** Invalid Input *****");
            // }
            

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