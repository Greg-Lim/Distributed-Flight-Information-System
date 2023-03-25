package com.sc4051.entity.messageFormats;

import java.util.LinkedList;
import java.util.List;

import com.sc4051.entity.DateTime;
import com.sc4051.entity.FlightInfo;
import com.sc4051.marshall.MarshallUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor

public class AddFlight {
    String source;
    String dest;
    DateTime departureTime;
    double airfare;
    int seatAvailible; 
    


    public AddFlight(List<Byte> byteList) {        
        source = MarshallUtils.unmarshallString(byteList);
        dest = MarshallUtils.unmarshallString(byteList);
        departureTime = new DateTime(MarshallUtils.unmarshallDate(byteList));
        airfare = MarshallUtils.unmarshallDouble(byteList);
        seatAvailible = MarshallUtils.unmarshallInt(byteList);
    }

    public List<Byte> marshall(){

        List<Byte> byteList = new LinkedList<Byte>();

        MarshallUtils.marshallString(source, byteList);
        MarshallUtils.marshallString(dest, byteList);
        MarshallUtils.marshallDate(departureTime.getDate(), byteList);
        MarshallUtils.marshallDouble(airfare, byteList);
        MarshallUtils.marshallInt(seatAvailible, byteList);

        return byteList;
    }
}
