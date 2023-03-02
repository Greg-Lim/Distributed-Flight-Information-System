package com.sc4051.server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.google.common.primitives.Bytes;
import com.sc4051.entity.FlightInfo;
import com.sc4051.entity.Message;
import com.sc4051.entity.messageFormats.QueryFlightID;
import com.sc4051.entity.messageFormats.QueryFlightSrcnDest;
import com.sc4051.marshall.CustomMarshaller;
import com.sc4051.marshall.MarshallUtils;
import com.sc4051.network.UDPCommunicator;
import com.sc4051.network.AtleastOnceNetwork;
import com.sc4051.network.Network;
import com.sc4051.network.NetworkErrorException;
import com.sc4051.network.PoorUDPCommunicator;

import lombok.Getter;

@Getter
public class Server {
    private static Database db = new Database();
    private static int sendingMessageID;
    private static UDPCommunicator udpCommunicator;
    private static Network network;
    // private int recievedMessageID;

    public static void main(String[] args) {
        start();
    }

    public static void start() {
        sendingMessageID=200;

        System.out.println("Starting Server...");
        try{
            InetAddress addr = InetAddress.getByName("localhost");
            int port = 8899;
            SocketAddress socketAddress = new InetSocketAddress(addr, port);
            
            udpCommunicator = new PoorUDPCommunicator(socketAddress, -1, 0.9);
            network = new AtleastOnceNetwork(udpCommunicator);
        } catch (NetworkErrorException e) {
            System.out.println(e);
            return;
        } catch (UnknownHostException e) {
            System.out.println(e);
            return;
        }
        System.out.println("Server running");

        while(true){
            Message message = null;
            try{
                message = network.recieve();
            } catch (Exception _){}

            System.out.println("here 2");

            requestHandler(message);
        }

        
    }

    public static void requestHandler(Message message){
        if (message==null){
            System.out.println("EROROROEOOROE: if I am priented there is a disaster");
        }
        System.out.println("here 3");
        List<Byte> replyMessageBody = new LinkedList<Byte>();
        Message replyMessage = new Message();
        System.out.println(message.getBody());
        switch(message.getType()){
            case 0: // reply ping
                replyMessage = new Message(sendingMessageID, message.getID()+1, 999, message.getBody());
                break;
            case 1: // find all flight given src and dest
                QueryFlightSrcnDest queryFlightSrcnDest = new QueryFlightSrcnDest(message.getBody());
                String src = queryFlightSrcnDest.getSrc();
                String dest = queryFlightSrcnDest.getDest();
                List<FlightInfo> flightList = db.getFlights(src, dest);
                List<Byte> replyBody = new LinkedList<Byte>();
                if(flightList.isEmpty()){
                    System.out.println("hahdfsahfshf");
                    replyMessage = new Message(sendingMessageID, message.getID()+1, String.format("Error: No Flights From %s To %s Found", src, dest));
                    System.out.println(message);
                } else {
                    CustomMarshaller.marshallFlightList(flightList, replyBody);
                    replyMessage = new Message(sendingMessageID, message.getID()+1, 11, replyBody);
                }
        }

        System.out.println("here 4");


        network.sendReply(replyMessage);
        

    }

    // can change Datagram socket to unreliable socket.
    // public static void handleRequest(DatagramSocket socket, DatagramPacket requestPacket){
    //     Message req = new Message(requestPacket.getData());
    //     System.out.printf("req: %s\n", req.toString());
    //     System.out.println(Arrays.toString(requestPacket.getData()));
    //     DatagramPacket reply;
    //     Message message;
    //     List<Byte> replyBody;

    //     // invocation types such a atleast once should be here
    //     // not the application but more serverside handling

    //     try {
    //         switch(req.getType()){
    //             case 0: // reply ping
    //                 System.out.println(req.getBody());
    //                 message = new Message(123, req.getAck()+1, 999, req.getBody());
    //                 reply = new DatagramPacket(message.marshall(), message.marshall().length, requestPacket.getAddress(),requestPacket.getPort());
    //                 socket.send(reply);
    //                 break;

    //             case 1: // find all flight given src and dest
    //                 QueryFlightSrcnDest queryFlightSrcnDest = new QueryFlightSrcnDest(req.getBody());
    //                 String src = queryFlightSrcnDest.getSrc();
    //                 String dest = queryFlightSrcnDest.getDest();
    //                 List<FlightInfo> flightList = db.getFlights(src, dest);
    //                 replyBody = new LinkedList<Byte>();
    //                 if(flightList.isEmpty()){
    //                     System.out.println("hahdfsahfshf");
    //                     message = new Message(messageID, req.getAck()+1, String.format("Error: No Flights From %s To %s Found", src, dest));
    //                     System.out.println(message);
    //                 } else {
    //                     CustomMarshaller.marshallFlightList(flightList, replyBody);
    //                     message = new Message(messageID, req.getAck()+1, 11, replyBody);
    //                 }
    //                 System.out.println("HERERERERE");
    //                 reply = new DatagramPacket(message.marshall(), message.marshall().length, requestPacket.getAddress(),requestPacket.getPort());
    //                 socket.send(reply);
    //                 break;
    //             case 2:
    //                 QueryFlightID queryFlightID = new QueryFlightID(req.getBody());
    //                 int id = queryFlightID.getId();
    //                 List<FlightInfo> idFlightList = db.getFlights(id);
    //                 replyBody = new LinkedList<Byte>();
    //                 if(idFlightList.isEmpty()){
    //                     message = new Message(messageID, req.getAck()+1, String.format("Error: No Flights With ID %d Found", id));
    //                 } else {
    //                     CustomMarshaller.marshallFlightList(idFlightList, replyBody);
    //                     message = new Message(messageID, req.getAck()+1, 12, replyBody);
    //                 }
    //                 reply = new DatagramPacket(message.marshall(), message.marshall().length, requestPacket.getAddress(),requestPacket.getPort());
    //                 socket.send(reply);
    //                 break;
    //         }
    //     } catch(Exception e){System.out.println(e.toString());}



    


    


    
}