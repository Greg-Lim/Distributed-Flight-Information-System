package com.sc4051.client;

import java.util.Random;

import com.sc4051.entity.DateTime;

public class ClientRandomData {






    static String get3Char() {
        String allChr = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder strBuilder = new StringBuilder();
        Random rnd = new Random();
        while (strBuilder.length() < 3) { // length of the random string.
            int index = (int) (rnd.nextFloat() * allChr.length());
            strBuilder.append(allChr.charAt(index));
        }
        String saltStr = strBuilder.toString();
        return saltStr;
    }


    static DateTime getRandDateTime(){
        Random rand = new Random();
        return new DateTime(2023,rand.nextInt(11)+1,rand.nextInt(28)+1, rand.nextInt(24), rand.nextInt(59)+1);
    }


    static double getFlightPrice(){
        Random rand = new Random();
        return (((double)rand.nextInt(90000))/100) +100;
    }

    static int getSeatAvailible(){
        Random rand = new Random();
        return rand.nextInt(600)+5;
    }


    
}
