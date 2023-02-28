package com.sc4051.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.sc4051.marshall.Marshallable;
import com.google.common.primitives.Bytes;
import com.sc4051.marshall.MarshallUtils;
import com.sc4051.entity.DateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class FlightInfo implements Marshallable<FlightInfo>{
    int flightID;
    String source;
    String dest;
    DateTime departureTime;
    double airfare;
    int seatAvailible; 
    
    public FlightInfo unmarshall(byte[] bytes) {
        return null;
    }

    // public FlightInfo(byte[] bytes) {
    //     List<Byte> byteList = new LinkedList<Byte>(Bytes.asList(bytes));
        
    //     flightID = MarshallUtils.unmarshallInt(byteList);
    //     source = MarshallUtils.unmarshallString(byteList);
    //     dest = MarshallUtils.unmarshallString(byteList);
    //     departureTime = new DateTime(MarshallUtils.unmarshallDate(byteList));
    //     airfare = MarshallUtils.unmarshallDouble(byteList);
    //     seatAvailible = MarshallUtils.unmarshallInt(byteList);
    // }

    public FlightInfo(List<Byte> byteList) {        
        flightID = MarshallUtils.unmarshallInt(byteList);
        source = MarshallUtils.unmarshallString(byteList);
        dest = MarshallUtils.unmarshallString(byteList);
        departureTime = new DateTime(MarshallUtils.unmarshallDate(byteList));
        airfare = MarshallUtils.unmarshallDouble(byteList);
        seatAvailible = MarshallUtils.unmarshallInt(byteList);
    }

    // public byte[] marshall(){
    //     List<Byte> byteList = new ArrayList<>();

    //     MarshallUtils.marshallInt(flightID, byteList);
    //     MarshallUtils.marshallString(source, byteList);
    //     MarshallUtils.marshallString(dest, byteList);
    //     MarshallUtils.marshallDate(departureTime.date, byteList);
    //     MarshallUtils.marshallDouble(airfare, byteList);
    //     MarshallUtils.marshallInt(seatAvailible, byteList);

    //     return Bytes.toArray(byteList);
    // }

    public void marshall(List<Byte> byteList){
        MarshallUtils.marshallInt(flightID, byteList);
        MarshallUtils.marshallString(source, byteList);
        MarshallUtils.marshallString(dest, byteList);
        MarshallUtils.marshallDate(departureTime.date, byteList);
        MarshallUtils.marshallDouble(airfare, byteList);
        MarshallUtils.marshallInt(seatAvailible, byteList);
    }
    
}
