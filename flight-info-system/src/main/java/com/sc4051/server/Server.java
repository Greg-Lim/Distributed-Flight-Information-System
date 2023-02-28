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
import com.sc4051.entity.messageFormats.QueryFlight;
import com.sc4051.marshall.CustomMarshaller;
import com.sc4051.marshall.MarshallUtils;

import lombok.Getter;

@Getter
public class Server {
    private static Database db = new Database();
    private Network network;

    public static void main(String[] args) {
        start();
    }

    public static void start() {
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
            }
        } catch(Exception IOException){
            System.out.println(IOException.toString());
        }
        
    }

    public static void handleRequest(DatagramSocket socket, DatagramPacket requestPacket){
        Message req = new Message(requestPacket.getData());
        System.out.printf("req: %s\n", req.toString());
        System.out.println(Arrays.toString(requestPacket.getData()));
        DatagramPacket reply = null;
        Message message = null;

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
                    QueryFlight query = new QueryFlight(req.getBody());
                    String src = query.getSrc();
                    String dest = query.getDest();
                    List<FlightInfo> flightList = db.getFlights(src, dest);
                    List<Byte> replyBody = new LinkedList<Byte>();
                    CustomMarshaller.marshallFlightList(flightList, replyBody);
                    message = new Message(123, req.getAck()+1, 999, replyBody);
                    System.out.println(message);

                    reply = new DatagramPacket(message.marshall(), message.marshall().length, requestPacket.getAddress(),requestPacket.getPort());
                    socket.send(reply);

            }
        } catch(Exception IOException){System.out.println("IOexception");}



        


    }


    
}