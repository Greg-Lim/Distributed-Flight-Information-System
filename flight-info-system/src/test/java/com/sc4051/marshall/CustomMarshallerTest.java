package com.sc4051.marshall;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import com.sc4051.entity.DateTime;
import com.sc4051.entity.FlightInfo;

public class CustomMarshallerTest {
    @Test
    public void testMarshallFlightList() {
        List<FlightInfo> flightList = new ArrayList<FlightInfo>();
        List<Byte> byteList = new LinkedList<Byte>();
        flightList.add(new FlightInfo(1010, "SIN", "NRT", new DateTime(2023,1,1,1,15), 600, 10));
        // flightList.add(new FlightInfo(2020, "NRT", "SIN", new DateTime(2023,2,2,2,25), 700, 120));
        // flightList.add(new FlightInfo(3030, "SIN", "PEK", new DateTime(2023,3,3,3,35), 800, 130));

        CustomMarshaller.marshallFlightList(flightList, byteList);
        assertEquals(byteList.toString(), "[0, 0, 0, 1, 0, 0, 3, -14, 0, 0, 0, 3, 83, 73, 78, 0, 0, 0, 3, 78, 82, 84, 99, -80, 110, 20, 64, -126, -64, 0, 0, 0, 0, 0, 0, 0, 0, 10]");
        List<FlightInfo> newList = CustomMarshaller.unmarshallFlightList(byteList);
        assertEquals(flightList.toString(), newList.toString());
    }

    @Test
    public void testUnmarshallFlightList() {

    }
}
