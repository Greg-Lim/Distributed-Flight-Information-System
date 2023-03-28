package com.sc4051.entity;

import java.util.List;

import com.sc4051.marshall.MarshallUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class FlightInfo{
    int flightID;
    String source;
    String dest;
    DateTime departureTime;
    double airfare;
    int seatAvailible; 

    
    /**
     * Constructs a new FlightInfo object by unmarshalling data from a list of bytes
     *
     * @param byteList the list of bytes to unmarshall
     */
    public FlightInfo(List<Byte> byteList) {        
        flightID = MarshallUtils.unmarshallInt(byteList);
        source = MarshallUtils.unmarshallString(byteList);
        dest = MarshallUtils.unmarshallString(byteList);
        departureTime = new DateTime(MarshallUtils.unmarshallDate(byteList));
        airfare = MarshallUtils.unmarshallDouble(byteList);
        seatAvailible = MarshallUtils.unmarshallInt(byteList);
    }

    /**
     * Marshalls this FlightInfo object by adding its data to a list of bytes.
     *
     * @param byteList the list of bytes to add data to
     */
    public void marshall(List<Byte> byteList){
        MarshallUtils.marshallInt(flightID, byteList);
        MarshallUtils.marshallString(source, byteList);
        MarshallUtils.marshallString(dest, byteList);
        MarshallUtils.marshallDate(departureTime.date, byteList);
        MarshallUtils.marshallDouble(airfare, byteList);
        MarshallUtils.marshallInt(seatAvailible, byteList);
    }
    
}
