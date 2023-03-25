package com.sc4051.entity.messageFormats;

import java.util.LinkedList;
import java.util.List;

import com.sc4051.marshall.MarshallUtils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SetFlightPrice{
    int flightID;
    double flightPrice;
    int sessionKey;

    public SetFlightPrice(List<Byte> byteList) {
        flightID = MarshallUtils.unmarshallInt(byteList);
        flightPrice = MarshallUtils.unmarshallDouble(byteList);
        sessionKey = MarshallUtils.unmarshallInt(byteList);
    }

    public SetFlightPrice(int flightID, double flightPrice, int sessionKey) {
        this.flightID=flightID;
        this.flightPrice=flightPrice;
        this.sessionKey = sessionKey;  
    }

    public List<Byte> marshall(){
        List<Byte> byteList = new LinkedList<Byte>();
        MarshallUtils.marshallInt(flightID, byteList);
        MarshallUtils.marshallDouble(flightPrice, byteList);
        MarshallUtils.marshallInt(sessionKey, byteList);
        return byteList;
    }
}
