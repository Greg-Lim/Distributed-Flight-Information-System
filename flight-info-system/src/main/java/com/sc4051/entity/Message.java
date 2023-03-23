package com.sc4051.entity;

import java.util.HexFormat;
import java.util.LinkedList;
import java.util.List;

import com.google.common.primitives.Bytes;
import com.sc4051.marshall.MarshallUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class Message {
    int ID;
    int ack;
    int type; // 0 Ping, 1-5 queries 999 is error
    List<Byte> body;

    public Message(){
        this.type = 0;
        this.ID = 0;
        this.ack = 0;
        this.body = Bytes.asList(HexFormat.ofDelimiter(":").parseHex("00:00:00:05:68:65:6c:6c:6f")); //5 Hello
    }

    public Message(int ID, int ack, String errorMsg){
        this.type = 999;
        List<Byte> body = new LinkedList<Byte>();
        MarshallUtils.marshallString(errorMsg, body);
        this.body = body;
        this.ID = ID;
        this.ack = ack;
    }

    public Message(byte[] bytes) {
        List<Byte> byteList = new LinkedList<Byte>(Bytes.asList(bytes));
        
        ID = MarshallUtils.unmarshallInt(byteList);
        ack = MarshallUtils.unmarshallInt(byteList);
        type = MarshallUtils.unmarshallInt(byteList);
        body = byteList;
    }

    public Message(List<Byte> byteList) {        
        ID = MarshallUtils.unmarshallInt(byteList);
        ack = MarshallUtils.unmarshallInt(byteList);
        type = MarshallUtils.unmarshallInt(byteList);
        body = byteList;
    }

    public List<Byte> marshall(List<Byte> byteList){
        MarshallUtils.marshallInt(ID, byteList);
        MarshallUtils.marshallInt(ack, byteList);
        MarshallUtils.marshallInt(type, byteList);
        List<Byte> t = new LinkedList<Byte>(body);
        byteList.addAll(t);
        return byteList;
    }

    public boolean isErr(){
        return type == 999;
    }

    public boolean isRequest(){
        return (type>0 && type<10);
    }

    public void printErr(){
        if(type==999){
            System.out.println(MarshallUtils.unmarshallString(body));
        } else {System.out.println("Something wrong");}
    }

    public String toString(){
        return String.format("Message(ID=%d, ack=%d, type=%d, body=\"%s\")", ID, ack, type, bytesToHex(body));
    }





    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(List<Byte> byteList) {
        byte[] bytes = Bytes.toArray(byteList);
        char[] hexChars = new char[bytes.length * 3];
        int fourZero = 0;
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 3] = HEX_ARRAY[v >>> 4];
            hexChars[j * 3 + 1] = HEX_ARRAY[v & 0x0F];
            if (hexChars[j * 3] == '0' & hexChars[j * 3 + 1]=='0') 
                fourZero+=2;
            else fourZero = 0;
            if(fourZero == 8) break;
            hexChars[j * 3 + 2] = ' ';
        }
        return new String(hexChars);
}
}
