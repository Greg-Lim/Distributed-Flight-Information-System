package com.sc4051.marshall;

import java.util.ArrayList;
import java.util.List;

import com.sc4051.entity.FlightInfo;

public class CustomMarshaller {
    
    public static List<FlightInfo> unmarshallFlightList(List<Byte> byteList){
        List<FlightInfo> flightInfoList = new ArrayList<FlightInfo>();
        int listLength = MarshallUtils.unmarshallInt(byteList);
        for(int i=0;i<listLength;i++){
            flightInfoList.add(new FlightInfo(byteList));
        }
        return flightInfoList;
    }

    public static void marshallFlightList(List<FlightInfo> flightInfoList, List<Byte> byteList){
        MarshallUtils.marshallInt(flightInfoList.size(), byteList);
        for(FlightInfo flightInfo : flightInfoList){
            flightInfo.marshall(byteList);
        }  
    }
}
