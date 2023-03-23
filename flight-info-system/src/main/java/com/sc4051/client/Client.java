package com.sc4051.client;

import java.net.*;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import com.google.common.primitives.Bytes;
import com.sc4051.entity.FlightInfo;
import com.sc4051.entity.Message;
import com.sc4051.entity.messageFormats.QueryFlightID;
import com.sc4051.entity.messageFormats.QueryFlightSrcnDest;
import com.sc4051.entity.messageFormats.RequestReserveSeat;
import com.sc4051.entity.messageFormats.RequestSeatUpdate;
import com.sc4051.marshall.CustomMarshaller;
import com.sc4051.marshall.MarshallUtils;
import com.sc4051.network.UDPCommunicator;
import com.sc4051.network.AtleastOnceNetwork;
import com.sc4051.network.AtmostOnceNetwork;
import com.sc4051.network.Network;
import com.sc4051.network.NetworkErrorException;
import com.sc4051.network.NoReplyException;
import com.sc4051.network.PoorUDPCommunicator;

public class Client{
    // final static double SEND_PROBABILITY=0.8;
    // final static int MAX_ATTEMPTS = 5;
    final static int TIMEOUT_TIME = 1000;

    static int mode = 1;
    static double sendProbability=0.8;
    static int maxAttempts = 5;

    Scanner sc = new Scanner(System.in);
    static Network network;
    static int serverPort;
    static int sendingMessageID;
    static SocketAddress socketAddress;
    static int port = ThreadLocalRandom.current().nextInt(1000, 2000 + 1);
    public static void main(String args[]){
        port = Integer.parseInt(args[0]);
        mode = Integer.parseInt(args[1]);
        sendProbability = Double.parseDouble(args[2]);
        maxAttempts = Integer.parseInt(args[3]);

        sendingMessageID=LocalDateTime.now().getMinute()*23+LocalDateTime.now().getSecond()*101;
        System.out.println("Connecting Client...");
        try{
            InetAddress address = InetAddress.getByName("localhost");
            SocketAddress socketAddress = new InetSocketAddress(address, port);
            UDPCommunicator udpCommunicator = new PoorUDPCommunicator(socketAddress, TIMEOUT_TIME, sendProbability); 
            
            if(mode==1) 
                network = new AtleastOnceNetwork(udpCommunicator, maxAttempts);
            else if (mode==2)
                network = new AtmostOnceNetwork(udpCommunicator, maxAttempts);
            else return;

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
                System.out.println(messageRecieve.toString()); //should be log
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
                    System.out.println(messageRecieve.toString()); //should be log
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
                    System.out.println(messageRecieve.toString()); //should be log               
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
            case 4: // Seat call back when number of seat change
                System.out.println("Seat callback...");
                requestFlightID = ClientView.getFlightIDOnly();
                int callBackDuration = ClientView.getCallbackDurationMS();
                RequestSeatUpdate requestSeatUpdate = new RequestSeatUpdate(requestFlightID, callBackDuration);
                messageToSend = new Message(sendingMessageID, 0, 4, requestSeatUpdate.marshall());
                System.out.println(messageToSend);

                // contact server
                try{
                    messageRecieve = network.sendAndRecieve(messageToSend, serverAddresss);
                    System.out.println();
                } catch(NoReplyException _){
                    System.out.println("Server did not ack callback request");
                    return;
                }

                // check server ack
                if (messageRecieve.isErr()){
                    messageRecieve.printErr();
                    return;
                } else {
                    System.out.println(messageRecieve);
                    String replyMessageString = MarshallUtils.unmarshallString(messageRecieve.getBody());
                    System.out.println(replyMessageString);
                }

                // waits for callback
                try{
                    messageRecieve = network.recieve(requestSeatUpdate.getTimeOut());
                    String replyMessageString = MarshallUtils.unmarshallString(messageRecieve.getBody());
                    System.out.println(replyMessageString);
                } catch(SocketTimeoutException _){
                    System.out.println("No reply recieved with callback timeout given...");
                } catch(Exception _){}
                break;
           
        }
    }
}