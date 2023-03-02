package com.sc4051.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.sc4051.entity.ClientInfo;
import com.sc4051.entity.FlightInfo;

public class DatabaseTest {
    Database db = new Database();
    

    @Test
    public void testAddNotifyFlightList() {
        
        FlightInfo flightInfo = db.getFlights(1010).get(0);
        System.out.println(flightInfo.toString());
        try{
            db.addNotifyFlightList(1010, new ClientInfo(1111, null, 10000));
        } catch(Exception NoSuchFlightException) {
            fail("did not add flight that exist"); 
        }

        try{
            db.addNotifyFlightList(12341234, new ClientInfo(2020, null, 10000));
            fail("added flight that does not exist"); 
        } catch(Exception NoSuchFlightException) {}

        // System.out.println(db);
        List<ClientInfo> clientInfoList = db.getNotifyFlightList(1010);
        System.out.println(clientInfoList);
        assertEquals(1111,clientInfoList.get(0).getQueryID());

        clientInfoList = db.getNotifyFlightList(123123);
        assertEquals(clientInfoList, Collections.EMPTY_LIST);

        

        
    }

    @Test
    public void testGetFlights() {
        FlightInfo flight1 = db.getFlights(2020).get(0);
        assertEquals(120, flight1.getSeatAvailible());
        List<FlightInfo> flight2 = db.getFlights(12341234);
        assertEquals(Collections.emptyList(), flight2);
    }

    @Test
    public void testGetFlights2() {
        FlightInfo flight2 = db.getFlights("NRT", "SIN").get(0);
        assertEquals(2020,flight2.getFlightID());
    }

    @Test
    public void testMakeReservation() {
        boolean work = false;
        try{
            work = db.makeReservation(1010, 5);
        } catch(Exception notEnoughSeatException){
            fail("have enough seat");
        }

        assertTrue(work);

        try{
            db.makeReservation(1010, 20);
            fail("have not enough seat");
        } catch(Exception notEnoughSeatException){}

    }
}
