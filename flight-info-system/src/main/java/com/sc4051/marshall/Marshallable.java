package com.sc4051.marshall;

import java.util.List;


// Idk if a interface is good/ dooable
public interface Marshallable<T>{
    public void marshall(List<Byte> byteList);
    public T unmarshall(byte[] bytes);
}
