package com.sc4051.entity.messageFormats;

import java.util.LinkedList;
import java.util.List;

import com.sc4051.marshall.MarshallUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class QueryFlightSrcnDest {
    String src;
    String dest;

    public QueryFlightSrcnDest(List<Byte> byteList) {
        src = MarshallUtils.unmarshallString(byteList);
        dest = MarshallUtils.unmarshallString(byteList);
    }

    // public void marshall(List<Byte> byteList){
    //     MarshallUtils.marshallString(src, byteList);
    //     MarshallUtils.marshallString(dest, byteList);
    // }

    public List<Byte> marshall(){
        List<Byte> byteList = new LinkedList<Byte>();

        MarshallUtils.marshallString(src, byteList);
        MarshallUtils.marshallString(dest, byteList);
        return byteList;
    }
}
