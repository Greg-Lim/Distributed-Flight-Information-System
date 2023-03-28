package com.sc4051.entity;

import java.util.HexFormat;
import java.util.LinkedList;
import java.util.List;

import com.google.common.primitives.Bytes;
import com.sc4051.marshall.MarshallUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


/**
 * The Message class represents a message that is send over the netowrk. 
 * It contains an ID, an ack, a type (type of query/response), and a list of bytes.
 */
@AllArgsConstructor
@Getter
@Setter
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

    /**
     * This constructor creates a new message with the given ID, ack, type, and message.
     * @param ID The ID of the message.
     * @param ack The ack of the message.
     * @param type The type of the message.
     * @param msg The message to be sent.
     */
    public Message(int ID, int ack, int type, String msg) {
        this.type = type;
        List<Byte> body = new LinkedList<Byte>();
        MarshallUtils.marshallString(msg, body);
        this.body = body;
        this.ID = ID;
        this.ack = ack;
    }

    /**
     * This constructor creates a new error message with the given ID, ack, and the error.
     * @param ID The ID of the message.
     * @param ack The ack of the message.
     * @param errorMsg The error message to be sent.
     */
    public Message(int ID, int ack, String errorMsg){
        this.type = 999;
        List<Byte> body = new LinkedList<Byte>();
        MarshallUtils.marshallString(errorMsg, body);
        this.body = body;
        this.ID = ID;
        this.ack = ack;
    }

    /**
     * This constructor Umarshals a array of bytes to a message
     * @param bytes The bytes representing the message.
     */
    public Message(byte[] bytes) {
        List<Byte> byteList = new LinkedList<Byte>(Bytes.asList(bytes));
        
        ID = MarshallUtils.unmarshallInt(byteList);
        ack = MarshallUtils.unmarshallInt(byteList);
        type = MarshallUtils.unmarshallInt(byteList);
        body = byteList;
    }

    /**
     * This constructor Umarshals a byteList to a message
     * @param byteList The bytes representing the message.
     */
    public Message(List<Byte> byteList) {        
        ID = MarshallUtils.unmarshallInt(byteList);
        ack = MarshallUtils.unmarshallInt(byteList);
        type = MarshallUtils.unmarshallInt(byteList);
        body = byteList;
    }

    /**
     * This method marshalls the message into a list of bytes.
     * @param byteList The list of bytes to be marshalled.
     * @return The marshalled list of bytes.
     */
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

    // helper function to print hex
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
