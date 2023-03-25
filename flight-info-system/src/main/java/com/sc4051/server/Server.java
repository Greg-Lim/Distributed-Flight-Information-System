package com.sc4051.server;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale.Category;

import com.sc4051.entity.ClientInfo;
import com.sc4051.entity.FlightInfo;
import com.sc4051.entity.Message;
import com.sc4051.entity.messageFormats.QueryFlightID;
import com.sc4051.entity.messageFormats.QueryFlightSrcnDest;
import com.sc4051.entity.messageFormats.RequestReserveSeat;
import com.sc4051.entity.messageFormats.RequestSeatUpdate;
import com.sc4051.entity.messageFormats.SetFlightPrice;
import com.sc4051.marshall.CustomMarshaller;
import com.sc4051.marshall.MarshallUtils;
import com.sc4051.network.AtleastOnceNetwork;
import com.sc4051.network.AtmostOnceNetwork;
import com.sc4051.network.CacheHandledReply;
import com.sc4051.network.Network;
import com.sc4051.network.NetworkErrorException;
import com.sc4051.network.PoorUDPCommunicator;

import lombok.Getter;

@Getter
public class Server {
    // final static double SEND_PROBABILITY=0.6;
    // final static int MAX_ATTEMPTS = 5;
    final static int TIMEOUT_TIME = -1; // -1 = never timeout
    static int port = 2222;
    static double sendProbability = 0.8;
    static int mode = 1; //1 ALO, 2 AMO
    static int maxAttempts = 5;

    private static Database db = new Database();
    private static int sendingMessageID;
    private static PoorUDPCommunicator udpCommunicator;
    private static Network network;
    // private int recievedMessageID;

    public static void main(String[] args) {
        port = Integer.parseInt(args[0]);
        mode = Integer.parseInt(args[1]);
        sendProbability = Double.parseDouble(args[2]);
        maxAttempts = Integer.parseInt(args[3]);
        // System.out.printf("%d %d %f %d",port, mode, sendProbability, maxAttempts);

        start();
    }

    public static void start() {
        sendingMessageID=200;

        System.out.println("Starting Server...");
        try{
            InetAddress addr = InetAddress.getByName("localhost");
            int port = 8899;
            SocketAddress socketAddress = new InetSocketAddress(addr, port);
            
            udpCommunicator = new PoorUDPCommunicator(socketAddress, TIMEOUT_TIME, sendProbability);
            
            if(mode==1)
                network = new AtleastOnceNetwork(udpCommunicator, maxAttempts);
            else if(mode==2)
                network = new AtmostOnceNetwork(udpCommunicator, maxAttempts);
            else return;
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
            } catch (CacheHandledReply _){
                continue;
            }
            catch (SocketTimeoutException _){
                continue;
            }

            requestHandler(message);
        }

        
    }

    public static void requestHandler(Message message){
        if (message==null){
            System.out.println("EROROROEOOROE: if I am printed there is a disaster");
        }
        System.out.println("recieved: "+message.toString());
        List<Byte> replyMessageBody = new LinkedList<Byte>();
        Message replyMessage = new Message();
        switch(message.getType()){
            case 0: // reply ping
                replyMessage = new Message(sendingMessageID, message.getID()+1, 10, message.getBody());
                break;
            case 1: // find all flight given src and dest
                QueryFlightSrcnDest queryFlightSrcnDest = new QueryFlightSrcnDest(message.getBody());
                String src = queryFlightSrcnDest.getSrc();
                String dest = queryFlightSrcnDest.getDest();
                List<FlightInfo> flightList = db.getFlights(src, dest);
                replyMessageBody = new LinkedList<Byte>();
                if(flightList.isEmpty()){
                    System.out.println("Empty");
                    replyMessage = new Message(sendingMessageID, message.getID()+1, String.format("Error: No Flights From %s To %s Found", src, dest));
                    System.out.println(message);
                } else {
                    CustomMarshaller.marshallFlightList(flightList, replyMessageBody);
                    replyMessage = new Message(sendingMessageID, message.getID()+1, 11, replyMessageBody);
                }
                break;
            case 2:
                QueryFlightID queryFlightID = new QueryFlightID(message.getBody());
                int id = queryFlightID.getId();
                List<FlightInfo> flightListFromID = db.getFlights(id);
                replyMessageBody = new LinkedList<Byte>();
                if(flightListFromID.isEmpty()){
                    System.out.println("Empty");
                    replyMessage = new Message(sendingMessageID, message.getID()+1, String.format("Error: No Flight ID: %d", id));
                    System.out.println(message);
                } else {
                    CustomMarshaller.marshallFlightList(flightListFromID, replyMessageBody);
                    replyMessage = new Message(sendingMessageID, message.getID()+1, 12, replyMessageBody);
                }
                // System.out.println(replyMessage);

                break;
            case 3: // seat reservation flight id + seat number -> update server+ack or error
                RequestReserveSeat requestReserveSeat = new RequestReserveSeat(message.getBody());
                List<ClientInfo> callbackList = null;
                try{
                    System.out.printf("%d %d", requestReserveSeat.getNumberOfSeat(), requestReserveSeat.getFlightID());
                    //reply callbacks
                    callbackList = db.makeReservation(requestReserveSeat.getFlightID(), requestReserveSeat.getNumberOfSeat());
                    FlightInfo reservFlightInfo = db.getFlights(requestReserveSeat.getFlightID()).get(0);
                    String callBackMessageString = String.format("Flight %d has %d seats now", requestReserveSeat.getFlightID(), reservFlightInfo.getSeatAvailible());
                    replyMessageBody = new LinkedList<Byte>();
                    MarshallUtils.marshallString(callBackMessageString, replyMessageBody);
                    if(callbackList != null){
                        for(ClientInfo clientInfo: callbackList){
                            replyMessage = new Message(sendingMessageID, clientInfo.getQueryID()+1, 14, replyMessageBody);
                            network.send(replyMessage, clientInfo.getSocketAddress());
                        }
                    }

                    String messageString = String.format("%d seats reserved on flight %d", requestReserveSeat.getNumberOfSeat(), requestReserveSeat.getFlightID());
                    replyMessageBody = new LinkedList<Byte>();
                    MarshallUtils.marshallString(messageString, replyMessageBody);
                    replyMessage = new Message(sendingMessageID, message.getID()+1, 13, replyMessageBody);

                } catch (NotEnoughSeatException _) {
                    // System.out.println("Empty");
                    replyMessage = new Message(sendingMessageID, message.getID()+1, String.format("Error: Not enought seats on flight %d.", requestReserveSeat.getFlightID()));
                    // System.out.println(message);
                } catch (NoSuchFlightException _){
                    replyMessage = new Message(sendingMessageID, message.getID()+1, String.format("Error: Error: No Flight ID: %d", requestReserveSeat.getFlightID()));
                }
                
                break;
            case 4: // Seat call back when number of seat change
                System.out.println(message);
                RequestSeatUpdate requestSeatUpdate = new RequestSeatUpdate(message.getBody());
                ClientInfo clientInfo = new ClientInfo(message.getID(), network.getReplyAddress(), requestSeatUpdate.getTimeOut());
                try{
                    db.addNotifyFlightList(requestSeatUpdate.getId(), clientInfo);
                    String messageString = String.format("Seat notification set for flight %d", requestSeatUpdate.getId());
                    replyMessageBody = new LinkedList<Byte>();
                    MarshallUtils.marshallString(messageString, replyMessageBody);
                    replyMessage = new Message(sendingMessageID, message.getID()+1, 14,replyMessageBody);
                } catch(NoSuchFlightException _){
                    replyMessage = new Message(sendingMessageID, message.getID()+1, String.format("Error: Error: No Flight ID: %d", requestSeatUpdate.getId()));
                }
                break;
            
            case 5: //set flight price
                SetFlightPrice setFlightPrice = new SetFlightPrice(message.getBody());
                if(setFlightPrice.getSessionKey()!=31337){ //TODO: hardcoded session autentication
                    replyMessage = new Message(sendingMessageID, message.getID()+1, String.format("Error: Invalid session ID"));
                    break;
                }
                try{
                    db.setFlightPrice(setFlightPrice.getFlightID(), setFlightPrice.getFlightPrice());
                    String messageString = String.format("Price of flight %d changed to %.2f", setFlightPrice.getFlightID(), setFlightPrice.getFlightPrice());
                    replyMessageBody = new LinkedList<Byte>();
                    MarshallUtils.marshallString(messageString, replyMessageBody);
                    replyMessage = new Message(sendingMessageID, message.getID()+1, 15,replyMessageBody);
                } catch (NoSuchFlightException e){
                    replyMessage = new Message(sendingMessageID, message.getID()+1, String.format("Error: No Flight ID: %d", setFlightPrice.getFlightID()));
                }
                break;

            case 6: //no-idenpotent maybe increase price of flights?

                break;
                
        }
        sendingMessageID+=1;
        System.out.println("reply: "+replyMessage.toString());
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