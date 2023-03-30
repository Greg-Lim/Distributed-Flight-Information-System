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

    /**
     * Returns a list of FlightInfo objects that have the specified source and destination.
     *
     * @param source the source of the flight
     * @param dest the destination of the flight
     * @return a list of FlightInfo objects that have the specified source and destination
     */
    public List<FlightInfo> getFlights(String source, String dest){
        ArrayList<FlightInfo> flightList = new ArrayList<FlightInfo>();

        for(FlightInfo flight: allFlights){
            if(flight.getSource().equals(source) && flight.getDest().equals(dest)){
                flightList.add(flight);
            }
        }
        return flightList;
    }

    /**
     * Returns a list of FlightInfo objects that match the specified id.
     *
     * @param id the id of the flight to search for
     * @return a list of FlightInfo objects that match the specified id
     */
    public List<FlightInfo> getFlights(int id){
        ArrayList<FlightInfo> flightList = new ArrayList<FlightInfo>();

        for(FlightInfo flight: allFlights){
            if(flight.getFlightID()==id){
                flightList.add(flight);
            }
        }
        return flightList;
    }


    /**
    * 
    * Makes a reservation for a specified number of seats on a flight with the given ID.
    * @param id the ID of the flight to make a reservation on
    * @param noSeats the number of seats to reserve
    * @return a List of ClientInfo objects representing clients who have to be notified of the reservation
    * @throws NotEnoughSeatException if there are not enough seats available on the specified flight
    * @throws NoSuchFlightException if there is no flight with the specified ID
    */
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

    /**
     * Adds a client to the list of clients to notify when the status of the flight with the specified ID changes.
     * 
     * @param id the ID of the flight to register the client for
     * @param client the client to add to the notification list
     * 
     * @throws NoSuchFlightException if no flight with the specified ID exists
     */
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

    /**
     * Returns a list of client information objects for the given ID that have not timed out.
     *
     * @param id the ID of the flight notification to retrieve
     * @return a list of client information objects that have not timed out
     */
    public List<ClientInfo> getNotifyFlightList(int id){
        List<ClientInfo> tempList = callbackList.get(id);
        if(tempList==null) return Collections.<ClientInfo>emptyList();
        List<ClientInfo> tempList2 = new ArrayList<ClientInfo>(tempList);
        for(ClientInfo clientInfo : tempList){
            if(clientInfo.isTimeout()){
                tempList2.remove(clientInfo);
            }
        }
        callbackList.remove(id);
        return tempList2;
    }

    /**
     * Sets the price of the specified flight.
     *
     * @param flightID the ID of the flight to set the price for
     * @param flightPrice the new price for the flight
     * @throws NoSuchFlightException if no flight with the specified ID exists
     */
    public void setFlightPrice(int flightID, double flightPrice) throws NoSuchFlightException{
        List<FlightInfo> flights = getFlights(flightID);
        if(flights.size()==0){
            throw new NoSuchFlightException();
        }

        FlightInfo flight = flights.get(0);

        flight.setAirfare(flightPrice);
        return;
    }

    /**
     * Generates a new, unique flight ID.
     *
     * @return An integer representing a new, unique flight ID.
     */
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

    /**
     * Adds a new flight to the list of all flights.
     * 
     * @param flightInfo the flight information to add to the list of all flights
     */
    public void addFlight(FlightInfo flightInfo){
        allFlights.add(flightInfo);
    }


    /**
     * Prints all FlightInfo objects in the database and the current callback list.
     */
    public void printALL(){
        System.out.println("===== DB Dump ====");
        for(FlightInfo i :allFlights)
            System.out.println(i.toString());
        System.out.print("Callback: ");
        System.out.println(callbackList.entrySet().toString());
        System.out.println("===== END DB Dump ===="); 
    }
}
