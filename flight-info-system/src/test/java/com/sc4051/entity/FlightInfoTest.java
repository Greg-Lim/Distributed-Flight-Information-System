package com.sc4051.entity;

import static org.junit.Assert.assertEquals;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

public class FlightInfoTest {
    @Test
    public void testFlightInfo() {
        FlightInfo t = new FlightInfo(123, "abc", "def", new DateTime(2021,1,1,12,30), 123.123, 456);
        List<Byte> byteList = new LinkedList<Byte>();
        t.marshall(byteList);
        FlightInfo unmar = new FlightInfo(byteList);
        // System.out.println(unmar);
        assertEquals(t.toString(),unmar.toString());
    }
}
