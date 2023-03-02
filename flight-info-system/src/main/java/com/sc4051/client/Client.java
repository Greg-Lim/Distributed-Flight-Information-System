package com.sc4051.client;

import java.net.*;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;
import java.util.Scanner;

import com.google.common.primitives.Bytes;
import com.sc4051.entity.FlightInfo;
import com.sc4051.entity.Message;
import com.sc4051.entity.messageFormats.QueryFlightID;
import com.sc4051.entity.messageFormats.QueryFlightSrcnDest;
import com.sc4051.entity.messageFormats.RequestReserveSeat;
import com.sc4051.marshall.CustomMarshaller;
import com.sc4051.marshall.MarshallUtils;
import com.sc4051.network.UDPCommunicator;
import com.sc4051.network.AtmostOnceNetwork;
import com.sc4051.network.Network;
import com.sc4051.network.NetworkErrorException;
import com.sc4051.network.NoReplyException;
import com.sc4051.network.PoorUDPCommunicator;

public class Client{
    final static double SEND_PROBABILITY=0.8;
    final static int MAX_ATTEMPTS = 5;
    final static int TIMEOUT_TIME = 1000;

    Scanner sc = new Scanner(System.in);
    static Network network;
    static int serverPort;
    static int sendingMessageID;
    static SocketAddress socketAddress;
    public static void main(String args[]){
        sendingMessageID=LocalDateTime.now().getMinute()*23+LocalDateTime.now().getSecond()*101;
        System.out.println("Connecting Client...");
        try{
            InetAddress address = InetAddress.getByName("localhost");
            int port = 6677;
            SocketAddress socketAddress = new InetSocketAddress(address, port);
            UDPCommunicator udpCommunicator = new PoorUDPCommunicator(socketAddress, TIMEOUT_TIME, SEND_PROBABILITY); 
            network = new AtmostOnceNetwork(udpCommunicator, MAX_ATTEMPTS); // need to add both
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
            choiceHandler(choice, serverAddresss);

        }

    }

    static public void choiceHandler(int choice, SocketAddress serverAddresss){
        
        Message messageToSend;
        Message messageRecieve;

        int requestFlightID;

        switch(choice){
            case 0: //ping
                List<Byte> messageBody = Bytes.asList(HexFormat.ofDelimiter(":").parseHex("00:00:00:0c:68:65:6c:6c:6f:20:77:6f:72:6c:64:21"));//5 hello world!
                messageToSend = new Message(sendingMessageID,0,0, messageBody);
                try{
                    messageRecieve = network.sendAndRecieve(messageToSend, serverAddresss);
                } catch (NoReplyException e){
                    System.out.println("No Response");
                    return;
                }
                String pingReplySting = MarshallUtils.unmarshallString(messageRecieve.getBody());
                System.out.print("Ping reply: ");
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
                } catch (NoReplyException e){
                    System.out.println("No Response");
                    return;
                }

                if (messageRecieve.isErr()){
                    messageRecieve.printErr();
                } else {
                    List<FlightInfo> flightInfoList = CustomMarshaller.unmarshallFlightList(messageRecieve.getBody());
                    System.out.println(flightInfoList.toString());
                }
                break;
            case 2:
                requestFlightID = ClientView.getFlightID();
                QueryFlightID queryFlightID = new QueryFlightID(requestFlightID);
                messageToSend = new Message(sendingMessageID, 0, 2, queryFlightID.marshall());
                try{
                    messageRecieve = network.sendAndRecieve(messageToSend, serverAddresss);
                } catch (NoReplyException e){
                    System.out.println("No Response");
                    return;
                }

                if (messageRecieve.isErr()){
                    messageRecieve.printErr();
                } else {
                    List<FlightInfo> flightInfoList = CustomMarshaller.unmarshallFlightList(messageRecieve.getBody());
                    System.out.println(flightInfoList.get(0).toString());
                }
                break;
            case 3:
                requestFlightID = ClientView.getFlightIDOnly();
                int numberOfSeat = ClientView.getNumberOfSeat();
                RequestReserveSeat requestReserveSeat = new RequestReserveSeat(requestFlightID, numberOfSeat);
                messageToSend = new Message(sendingMessageID, 0, 3, requestReserveSeat.marshall());
                try{
                    messageRecieve = network.sendAndRecieve(messageToSend, serverAddresss);
                } catch (NoReplyException e){
                    System.out.println("No Response");
                    return;
                }

                if (messageRecieve.isErr()){
                    messageRecieve.printErr();
                } else {
                    System.out.println(messageRecieve);
                    String replyMessageString = MarshallUtils.unmarshallString(messageRecieve.getBody());
                    System.out.println(replyMessageString);
                }
                break;



        }
    }
}