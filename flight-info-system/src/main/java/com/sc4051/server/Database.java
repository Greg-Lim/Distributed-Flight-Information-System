package com.sc4051.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import com.sc4051.entity.ClientInfo;
import com.sc4051.entity.DateTime;
import com.sc4051.entity.FlightInfo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class Database {
    private static List<FlightInfo> allFlights = new ArrayList<FlightInfo>();
    private static HashMap<Integer, List<ClientInfo>> callbackList = new HashMap<>();

    public Database(boolean test){
        setupTest();
    }

    public Database(){
        setup();
    }

    private static void setup(){
        allFlights.add(new FlightInfo(1010, "SIN", "NRT", new DateTime(2023,1,1,1,15), 600, 10));
        allFlights.add(new FlightInfo(2020, "NRT", "SIN", new DateTime(2023,2,2,2,25), 700, 120));
        allFlights.add(new FlightInfo(3030, "SIN", "PEK", new DateTime(2023,3,3,3,35), 800, 130));
        allFlights.add(new FlightInfo(4040, "PEK", "NRT", new DateTime(2023,4,4,4,45), 850, 140));
        allFlights.add(new FlightInfo(5050, "PEK", "SIN", new DateTime(2023,5,5,5,55), 900, 150));
        allFlights.add(new FlightInfo(6060, "NRT", "PEK", new DateTime(2023,6,6,6,05), 950, 160));
    }

    private static void setupTest(){
        allFlights.add(new FlightInfo(1010, "SIN", "NRT", new DateTime(2023,1,1,1,15), 600, 10));
        allFlights.add(new FlightInfo(2020, "NRT", "SIN", new DateTime(2023,2,2,2,25), 700, 120));
        allFlights.add(new FlightInfo(3030, "SIN", "PEK", new DateTime(2023,3,3,3,35), 800, 130));
        allFlights.add(new FlightInfo(4040, "PEK", "NRT", new DateTime(2023,4,4,4,45), 850, 140));
        allFlights.add(new FlightInfo(5050, "PEK", "SIN", new DateTime(2023,5,5,5,55), 900, 150));
        allFlights.add(new FlightInfo(6060, "NRT", "PEK", new DateTime(2023,6,6,6,05), 950, 160));
    }

    public List<FlightInfo> getFlights(String source, String dest){
        ArrayList<FlightInfo> flightList = new ArrayList<FlightInfo>();

        for(FlightInfo flight: allFlights){
            if(flight.getSource().equals(source) && flight.getDest().equals(dest)){
                flightList.add(flight);
            }
        }
        return flightList;
    }

    public List<FlightInfo> getFlights(int id){
        ArrayList<FlightInfo> flightList = new ArrayList<FlightInfo>();

        for(FlightInfo flight: allFlights){
            if(flight.getFlightID()==id){
                flightList.add(flight);
            }
        }
        return flightList;
    }

    public List<ClientInfo> makeReservation(int id, int noSeats) throws NotEnoughSeatException, NoSuchFlightException{
        List<FlightInfo> flights = getFlights(id);
        if(flights.size()==0){
            throw new NoSuchFlightException();
        }
        FlightInfo flight = flights.get(0);
        if(flight.getSeatAvailible() < noSeats){
            throw new NotEnoughSeatException();
        }
        else {
            flight.setSeatAvailible(flight.getSeatAvailible() - noSeats);
            return getNotifyFlightList(flight.getFlightID());
        }
    }

    public void addNotifyFlightList(int id, ClientInfo client) throws NoSuchFlightException{
        finding: { 
            for(FlightInfo flight: allFlights){
                if (flight.getFlightID()==id){
                    break finding;
                }
            }
            {throw new NoSuchFlightException();}
        }

        if(!callbackList.containsKey(id)){
            callbackList.put(id, new ArrayList<ClientInfo>());
        }

        List<ClientInfo> tempList = callbackList.get(id);
        tempList.add(client);
        callbackList.put(id, tempList);
    }

    public List<ClientInfo> getNotifyFlightList(int id){
        List<ClientInfo> tempList = callbackList.get(id);
        if(tempList==null) return Collections.<ClientInfo>emptyList();
        for(ClientInfo clientInfo : tempList){
            if(clientInfo.isTimeout()){
                tempList.remove(clientInfo);
            }
        }
        callbackList.remove(id);
        return tempList;
    }

    public void setFlightPrice(int flightID, double flightPrice) throws NoSuchFlightException{
        List<FlightInfo> flights = getFlights(flightID);
        if(flights.size()==0){
            throw new NoSuchFlightException();
        }

        FlightInfo flight = flights.get(0);

        flight.setAirfare(flightPrice);
        return;
    }

    public int makeNewFlightID() {
        ArrayList<Integer> allID = new ArrayList<Integer>();
        for(FlightInfo i: allFlights) 
            allID.add(i.getFlightID());
        while(true){
            Random rand = new Random();
            int r = rand.nextInt(9999);
            if (! allID.contains(r)) return r;
        }
        
    }

    public void addFlight(FlightInfo flightInfo){
        allFlights.add(flightInfo);
    }

    public void printALL(){
        for(FlightInfo i :allFlights)
            System.out.println(i.toString());
        System.out.println(callbackList.entrySet().toString());
           
    }
}
