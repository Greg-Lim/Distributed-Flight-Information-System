package com.sc4051.client;

import java.util.Scanner;
import java.util.stream.Stream;

import com.sc4051.entity.DateTime;
import com.sc4051.entity.FlightInfo;

public class ClientView {

    public static Scanner sc = new Scanner(System.in);

    public static void printMenu(){
        System.out.println("\n========== S4051 Flight Information System ==========");
        System.out.println("Choose request:");
        System.out.println("[0] Ping Debug");
        System.out.println("[1] Find Flight with source and dest");
        System.out.println("[2] Query Flight with ID");
        System.out.println("[3] Make Flight Reservation");
        System.out.println("[4] Monitor Flight with ID");
        System.out.println("[5] Set Airfare (need autenticate)");
        System.out.println("[6] Add Flight (non-idempotent)");
        System.out.println("[7] Exit Application");
    }

    public static int getUserChoice(){
        int i = getInt();
        // sc.nextLine();
        return i;
    }

    public static int getInvocation(){
        System.out.println("Selct Network invocation\n 1. Atleast once\n 2. Atmost once");
        int i = getInt();
        while(i!=1 & i!=2){
            System.out.println("Invalid");
            i = getInvocation();
        }
        return i;
    }

    public static String[] getSrcnDest(){
        String[] SrcnDest = new String[2];
        System.out.println("\n========== [1] Find Flight with source and dest ==========");
        SrcnDest[0] = getSrc();
        SrcnDest[1] = getDest();
        return SrcnDest;
    }

    public static String getSrc(){
        System.out.println("Enter Source Location: ");
        return getStringUpper();
    }

    public static String getDest(){
        System.out.println("Enter Destination Location: ");
        return getStringUpper();
    }

    public static int getFlightID(){
        int id;
        System.out.println("\n========== [2] Query Flight with ID ==========");
        System.out.println("Enter Flight ID: ");
        id = getInt();
        return id;
    }


    public static int getFlightIDOnly(){
        int id;
        System.out.println("Enter Flight ID: ");
        id = getInt();
        return id;
    }

    public static int getNumberOfSeat(){
        int id;
        System.out.println("Enter Number of seats to reserve: ");
        id = getInt();
        return id;
    }

    public static int getCallbackDurationMS(){
        int callBackDuration;
        System.out.println("Enter Callback wait time (seconds): ");
        callBackDuration = getInt()*1000;
        return callBackDuration;   
    }

    public static double getFlightPrice() {
        double price;
        System.out.println("Enter Price of Flight");
        price = getDouble();
        return price;  
    }

    public static int getFlightSeats() {
        System.out.println("Enter number of flight seats");
        return getInt();
    }
    
    public static int getSessionID() {
        System.out.println("Enter Session ID (hint: hardcoded as 31337): ");
        return getInt();
    }



    public static int getInt(){
        try{
            int i = sc.nextInt();
            sc.nextLine();
            return i;
        } catch(Exception e) {
            sc.nextLine();
            System.out.println("Invalid int");
            return getInt();
        }
    }

    public static double getDouble(){
        try{
            double i = sc.nextDouble();
            sc.nextLine();
            return i;
        } catch(Exception e) {
            sc.nextLine();
            System.out.println("Invalid double");
            return getDouble();
        }
    }

    public static String getStringUpper(){
        try{
            return sc.nextLine().toUpperCase().trim();
        } catch(Exception e) {
            System.out.println("Invalid String");
            return getStringUpper();
        }
    }

    public static DateTime getDepartureTime() {
        System.out.println("Enter year, month, day, hrs, min");
        String[] tokens = sc.nextLine().split(" ?, ?");
        if(tokens.length <5){
            System.out.println("Insufficient Elements");
            return getDepartureTime();
        }
        try{
            return new DateTime(Integer.parseInt(tokens[0]),Integer.parseInt(tokens[1]),Integer.parseInt(tokens[2]),Integer.parseInt(tokens[3]),Integer.parseInt(tokens[4]));
        } catch (NumberFormatException e){
            System.out.println("Invalid number format");
            return getDepartureTime();
        }
    }

    public static boolean isY() {

        // System.out.println("Enter another contestant (Y)?");
        String str = sc.nextLine();
        if (str.equalsIgnoreCase("Y"))
            return true;

        return false;
        
    }






    
}
