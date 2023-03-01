package com.sc4051.entity.messageFormats;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.processing.Generated;

import com.sc4051.marshall.MarshallUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class QueryFlightID {
    int id;

    public QueryFlightID(List<Byte> byteList) {
        id = MarshallUtils.unmarshallInt(byteList);
    }

    public void marshall(List<Byte> byteList){
        MarshallUtils.marshallInt(id, byteList);
    }

    public List<Byte> marshall(){
        List<Byte> byteList = new LinkedList<Byte>();
        MarshallUtils.marshallInt(id, byteList);
        return byteList;
    }
}
