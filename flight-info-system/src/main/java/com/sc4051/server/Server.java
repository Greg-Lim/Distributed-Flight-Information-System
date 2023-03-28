package com.sc4051.server;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

import com.sc4051.entity.ClientInfo;
import com.sc4051.entity.FlightInfo;
import com.sc4051.entity.Message;
import com.sc4051.entity.messageFormats.AddFlight;
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
    final static int TIMEOUT_TIME = -1; // -1 = never timeout
    static int port = 2222;
    static double sendProbability = 0.8;
    static int mode = 1; //1 ALO, 2 AMO
    static int maxAttempts = 5;

    private static Database db = new Database();
    private static int sendingMessageID;
    private static PoorUDPCommunicator udpCommunicator;
    private static Network network;
    private static int oldID;

    public static void main(String[] args) {
        port = Integer.parseInt(args[0]);
        mode = Integer.parseInt(args[1]);
        sendProbability = Double.parseDouble(args[2]);
        maxAttempts = Integer.parseInt(args[3]);

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
            System.out.println("");
            Message message = null;
            try{
                message = network.recieve();
            } catch (CacheHandledReply discard){
                continue;
            }
            catch (SocketTimeoutException discard){
                continue;
            }

            requestHandler(message);
        }
    }

    public static void requestHandler(Message message){
        if (message==null){
            System.out.println("ERROR: NULL MESSAGE"); // this should not run
            System.exit(-1);
        }
        System.out.println("recieved: "+message.toString());
        List<Byte> replyMessageBody = new LinkedList<Byte>();
        Message replyMessage = new Message();
        switch(message.getType()){
            case 0: // reply ping
                replyMessage = new Message(sendingMessageID, message.getID()+1, 10, message.getBody());
                if(message.getID()!=oldID){
                    db.printALL();
                }
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
            case 2: // find flight given an ID
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
                break;
            case 3: // seat reservation flight id + seat number -> update server+ack or error
                RequestReserveSeat requestReserveSeat = new RequestReserveSeat(message.getBody());
                List<ClientInfo> callbackList = null;
                try{
                    //reply callbacks
                    callbackList = db.makeReservation(requestReserveSeat.getFlightID(), requestReserveSeat.getNumberOfSeat());
                    FlightInfo reservFlightInfo = db.getFlights(requestReserveSeat.getFlightID()).get(0);
                    String callBackMessageString = String.format("Flight %d has %d seats now", requestReserveSeat.getFlightID(), reservFlightInfo.getSeatAvailible());
                    replyMessageBody = new LinkedList<Byte>();
                    MarshallUtils.marshallString(callBackMessageString, replyMessageBody);
                    if(callbackList != null){
                        for(ClientInfo clientInfo: callbackList){
                            replyMessage = new Message(sendingMessageID, clientInfo.getQueryID()+1, 142, replyMessageBody);
                            System.out.println(String.format("Sending Callback: %s", replyMessage.toString()));
                            network.send(replyMessage, clientInfo.getSocketAddress());
                        }
                    }

                    // reply client booking the seat
                    String messageString = String.format("%d seats reserved on flight %d", requestReserveSeat.getNumberOfSeat(), requestReserveSeat.getFlightID());
                    replyMessageBody = new LinkedList<Byte>();
                    MarshallUtils.marshallString(messageString, replyMessageBody);
                    replyMessage = new Message(sendingMessageID, message.getID()+1, 13, replyMessageBody);

                } catch (NotEnoughSeatException discard) {
                    replyMessage = new Message(sendingMessageID, message.getID()+1, String.format("Error: Not enought seats on flight %d.", requestReserveSeat.getFlightID()));
                } catch (NoSuchFlightException discard){
                    replyMessage = new Message(sendingMessageID, message.getID()+1, String.format("Error: Error: No Flight ID: %d", requestReserveSeat.getFlightID()));
                }
                
                break;
            case 4: // Seat call back when number of seat change
                RequestSeatUpdate requestSeatUpdate = new RequestSeatUpdate(message.getBody());
                ClientInfo clientInfo = new ClientInfo(message.getID(), network.getReplyAddress(), requestSeatUpdate.getTimeOut());
                try{
                    db.addNotifyFlightList(requestSeatUpdate.getId(), clientInfo);
                    String messageString = String.format("Seat notification set for flight %d", requestSeatUpdate.getId());
                    replyMessageBody = new LinkedList<Byte>();
                    MarshallUtils.marshallString(messageString, replyMessageBody);
                    replyMessage = new Message(sendingMessageID, message.getID()+1, 141,replyMessageBody);
                } catch(NoSuchFlightException discard){
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

            case 6: 
                //non-idenpotent create flight
                // NOTE: Create flight is non-idempotent as ID is generated by server
                // 2 request will make 2 flights with different ID.

                AddFlight addFlight = new AddFlight(message.getBody());
                int flightID = db.makeNewFlightID();
                FlightInfo newFlightInfo = new FlightInfo(flightID, addFlight.getSource(), addFlight.getDest(), addFlight.getDepartureTime(), addFlight.getAirfare(), addFlight.getSeatAvailible());
                db.addFlight(newFlightInfo);

                replyMessage = new Message(sendingMessageID, message.getID()+1, 16, String.format("New fligh added with ID: %d", flightID));

                break;              
        }
        sendingMessageID+=1;
        oldID = message.getID();
        System.out.println("reply: "+replyMessage.toString());
        network.sendReply(replyMessage);
    }
}