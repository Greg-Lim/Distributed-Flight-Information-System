package com.sc4051.marshall;


// I dk if a interface is good/ dooable
public interface Marshallable {
    public byte[] marshall(Object obj);
    public Object unmarshall(byte[] buf);
}
