package com.sc4051.entity.messageFormats;

import java.util.LinkedList;
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
public class RequestSeatUpdate {
    int id;
    int timeOut;

    public RequestSeatUpdate(List<Byte> byteList) {
        id = MarshallUtils.unmarshallInt(byteList);
        timeOut = MarshallUtils.unmarshallInt(byteList);
    }

    public List<Byte> marshall(){
        List<Byte> byteList = new LinkedList<Byte>();
        MarshallUtils.marshallInt(id, byteList);
        MarshallUtils.marshallInt(timeOut, byteList);
        return byteList;
    }
    
}
