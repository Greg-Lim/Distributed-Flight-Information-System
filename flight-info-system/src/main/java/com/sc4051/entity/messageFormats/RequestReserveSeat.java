package com.sc4051.entity.messageFormats;

import java.util.LinkedList;
import java.util.List;

import com.sc4051.marshall.MarshallUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RequestReserveSeat {
    int flightID;
    int numberOfSeat;

    public RequestReserveSeat(List<Byte> byteList) {
        flightID = MarshallUtils.unmarshallInt(byteList);
        numberOfSeat = MarshallUtils.unmarshallInt(byteList);
    }

    // public void marshall(List<Byte> byteList){
    //     MarshallUtils.marshallInt(flightID, byteList);
    //     MarshallUtils.marshallInt(numberOfSeat, byteList);
    // }

    public List<Byte> marshall(){
        List<Byte> byteList = new LinkedList<Byte>();

        MarshallUtils.marshallInt(flightID, byteList);
        MarshallUtils.marshallInt(numberOfSeat, byteList);
        return byteList;
    }



}
