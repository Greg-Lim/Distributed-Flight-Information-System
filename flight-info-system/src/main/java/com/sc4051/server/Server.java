package com.sc4051.server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.google.common.graph.Network;
import com.google.common.primitives.Bytes;
import com.sc4051.entity.FlightInfo;
import com.sc4051.entity.Message;
import com.sc4051.entity.messageFormats.QueryFlightID;
import com.sc4051.entity.messageFormats.QueryFlightSrcnDest;
import com.sc4051.marshall.CustomMarshaller;
import com.sc4051.marshall.MarshallUtils;

import lombok.Getter;

@Getter
public class Server {
    private static Database db = new Database();
    private static int messageID;

    public static void main(String[] args) {
        start();
    }

    public static void start() {
        messageID=200;
        DatagramSocket aSocket = null;

        try{
            aSocket = new DatagramSocket(6789);
            byte[] buffer =new byte[50];
            while(true){
                DatagramPacket request = new DatagramPacket(buffer, 50);
                System.out.println("here 1");
                aSocket.receive(request); // this is blocking I think
                System.out.println("here 2");
                handleRequest(aSocket, request);
                messageID+=1;
            }
        } catch(Exception IOException){
            System.out.println(IOException.toString());
        }
        
    }

    // can change Datagram socket to unreliable socket.
    public static void handleRequest(DatagramSocket socket, DatagramPacket requestPacket){
        Message req = new Message(requestPacket.getData());
        System.out.printf("req: %s\n", req.toString());
        System.out.println(Arrays.toString(requestPacket.getData()));
        DatagramPacket reply;
        Message message;
        List<Byte> replyBody;

        // invocation types such a atleast once should be here
        // not the application but more serverside handling

        try {
            switch(req.getType()){
                case 0: // reply ping
                    System.out.println(req.getBody());
                    message = new Message(123, req.getAck()+1, 999, req.getBody());
                    reply = new DatagramPacket(message.marshall(), message.marshall().length, requestPacket.getAddress(),requestPacket.getPort());
                    socket.send(reply);
                    break;

                case 1: // find all flight given src and dest
                    QueryFlightSrcnDest queryFlightSrcnDest = new QueryFlightSrcnDest(req.getBody());
                    String src = queryFlightSrcnDest.getSrc();
                    String dest = queryFlightSrcnDest.getDest();
                    List<FlightInfo> flightList = db.getFlights(src, dest);
                    replyBody = new LinkedList<Byte>();
                    if(flightList.isEmpty()){
                        System.out.println("hahdfsahfshf");
                        message = new Message(messageID, req.getAck()+1, String.format("Error: No Flights From %s To %s Found", src, dest));
                        System.out.println(message);
                    } else {
                        CustomMarshaller.marshallFlightList(flightList, replyBody);
                        message = new Message(messageID, req.getAck()+1, 11, replyBody);
                    }
                    System.out.println("HERERERERE");
                    reply = new DatagramPacket(message.marshall(), message.marshall().length, requestPacket.getAddress(),requestPacket.getPort());
                    socket.send(reply);
                    break;
                case 2:
                    QueryFlightID queryFlightID = new QueryFlightID(req.getBody());
                    int id = queryFlightID.getId();
                    List<FlightInfo> idFlightList = db.getFlights(id);
                    replyBody = new LinkedList<Byte>();
                    if(idFlightList.isEmpty()){
                        message = new Message(messageID, req.getAck()+1, String.format("Error: No Flights With ID %d Found", id));
                    } else {
                        CustomMarshaller.marshallFlightList(idFlightList, replyBody);
                        message = new Message(messageID, req.getAck()+1, 12, replyBody);
                    }
                    reply = new DatagramPacket(message.marshall(), message.marshall().length, requestPacket.getAddress(),requestPacket.getPort());
                    socket.send(reply);
                    break;
            }
        } catch(Exception e){System.out.println(e.toString());}



        


    }


    
}