package com.sc4051.marshall;

import com.google.common.primitives.Bytes;

import java.nio.ByteBuffer;
import java.util.Date;
import java.util.List;


public class MarshallUtils {

    // ===== Primitive Marshaller =====
    public static int unmarshallInt(List<Byte> byteList) {
        return intFromByteList(byteList);
    }

    public static void marshallInt(Object o, List<Byte> res) {
        int i = (int) o;
        res.addAll(intToByteList(i));
    }

    public static short unmarshallShort(List<Byte> byteList) {
        return shortFromByteList(byteList);
    }

    public static void marshallShort(Object o, List<Byte> res) {
        short i = (short) o;
        res.addAll(shortToByteList(i));
    }

    public static float unmarshallFloat(List<Byte> byteList) {
        return floatFromByteList(byteList);
    }

    public static void marshallFloat(Object o, List<Byte> res) {
        float d = (float) o;
        res.addAll(floatToByteList(d));
    }

    public static double unmarshallDouble(List<Byte> byteList) {
        return doubleFromByteList(byteList);
    }

    public static void marshallDouble(Object o, List<Byte> res) {
        double d = (double) o;
        res.addAll(doubleToByteList(d));
    }

    public static boolean unmarshallBoolean(List<Byte> byteList) {
        return booleanFromByteList(byteList);
    }

    public static void marshallBoolean(Object o, List<Byte> res) {
        boolean b = (boolean) o;
        res.addAll(booleanToByteList(b));
    }

    public static String unmarshallString(List<Byte> byteList) {

        int size = intFromByteList(byteList);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < size; i++) {
            builder.append((char) byteList.remove(0).byteValue());
        }
        return builder.toString();
    }

    public static void marshallString(Object o, List<Byte> res) {
        String s = (String) o;

        res.addAll(intToByteList((int) s.length()));
        res.addAll(Bytes.asList(s.getBytes()));
    }

    public static Date unmarshallDate(List<Byte> byteList) {
        int epoch = unmarshallInt(byteList);
        return new Date((long) epoch * 1000);
    }

    public static void marshallDate(Object o, List<Byte> res) {
        Date date = (Date) o;
        int epoch = (int) (date.getTime() / 1000);
        res.addAll(intToByteList(epoch));
    }

    private static int intFromByteList(List<Byte> byteList) {
        byte[] intBytes = new byte[Integer.BYTES];
        for (int i = 0; i < Integer.BYTES; i++) {
            intBytes[i] = byteList.remove(0);
        }
        return ByteBuffer.wrap(intBytes).getInt();
    }

    private static List<Byte> shortToByteList(short v) {
        byte[] array = ByteBuffer.allocate(Short.BYTES).putShort(v).array();
        return Bytes.asList(array);
    }

    private static short shortFromByteList(List<Byte> byteList) {
        byte[] shortBytes = new byte[Short.BYTES];
        for (int i = 0; i < Short.BYTES; i++) {
            shortBytes[i] = byteList.remove(0);
        }
        return ByteBuffer.wrap(shortBytes).getShort();
    }

    private static List<Byte> intToByteList(int v) {
        byte[] array = ByteBuffer.allocate(Integer.BYTES).putInt(v).array();
        return Bytes.asList(array);
    }

    private static double doubleFromByteList(List<Byte> byteList) {
        byte[] doubleBytes = new byte[Double.BYTES];
        for (int i = 0; i < Double.BYTES; i++) {
            doubleBytes[i] = byteList.remove(0);
        }
        return ByteBuffer.wrap(doubleBytes).getDouble();
    }

    private static List<Byte> doubleToByteList(double v) {
        byte[] array = ByteBuffer.allocate(Double.BYTES).putDouble(v).array();
        return Bytes.asList(array);
    }

    private static float floatFromByteList(List<Byte> byteList) {
        byte[] floatBytes = new byte[Float.BYTES];
        for (int i = 0; i < Float.BYTES; i++) {
            floatBytes[i] = byteList.remove(0);
        }
        return ByteBuffer.wrap(floatBytes).getFloat();
    }

    private static List<Byte> floatToByteList(float v) {
        byte[] array = ByteBuffer.allocate(Float.BYTES).putFloat(v).array();
        return Bytes.asList(array);
    }

    private static boolean booleanFromByteList(List<Byte> byteList) {
        Byte bool = byteList.remove(0);
        return bool != (byte) 0;
    }

    private static List<Byte> booleanToByteList(boolean v) {
        byte b = (byte) 0;
        if (v) b = (byte) 1;
        return Bytes.asList(b);
    }
}
