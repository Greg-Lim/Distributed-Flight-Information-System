package com.sc4051.entity;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class FlightInfoTest {
    @Test
    public void testFlightInfo() {
        FlightInfo t = new FlightInfo(123, "abc", "def", new DateTime(2021,1,1,12,30), 123.123, 456);
        byte[] b = t.marshall();
        FlightInfo unmar = new FlightInfo(b);
        // System.out.println(unmar);
        assertEquals("FlightInfo(flightID=123, source=abc, dest=def, departureTime=DateTime(date=Fri Jan 01 12:30:00 SGT 2021), airfare=123.123, seatAvailible=456)", unmar.toString());
    }
}
