package com.sc4051.marshall;

import java.util.ArrayList;
import java.util.List;

import com.sc4051.entity.FlightInfo;

/**
 * Utility class for marshalling and unmarshalling FlightInfo objects.
 */
public class CustomMarshaller {
    
    /**
     * Converts a list of bytes to a list of FlightInfo objects.
     *
     * @param byteList the list of bytes to unmarshall
     * @return the list of FlightInfo objects
     */
    public static List<FlightInfo> unmarshallFlightList(List<Byte> byteList){
        List<FlightInfo> flightInfoList = new ArrayList<FlightInfo>();
        int listLength = MarshallUtils.unmarshallInt(byteList);
        for(int i=0;i<listLength;i++){
            flightInfoList.add(new FlightInfo(byteList));
        }
        return flightInfoList;
    }

    /**
     * Marshalls a list of FlightInfo objects into a byte list.
     *
     * @param flightInfoList the list of FlightInfo objects to be marshalled
     * @param byteList the byte list to which the marshalled data will be added
     */
    public static void marshallFlightList(List<FlightInfo> flightInfoList, List<Byte> byteList){
        MarshallUtils.marshallInt(flightInfoList.size(), byteList);
        for(FlightInfo flightInfo : flightInfoList){
            flightInfo.marshall(byteList);
        }  
    }
}
