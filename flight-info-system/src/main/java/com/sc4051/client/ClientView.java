package com.sc4051.client;

import java.util.Scanner;

public class ClientView {

    public static Scanner sc = new Scanner(System.in);

    public static void printMenu(){
        System.out.println("\n========== S4051 Flight Information System ==========");
        System.out.println("Choose request:");
        System.out.println("[0] Ping Server");
        System.out.println("[1] Find Flight with source and dest");
        System.out.println("[2] Query Flight with ID");
        System.out.println("[3] Make Flight Reservation");
        System.out.println("[4] Monitor Flight with ID");
        System.out.println("[5] Set Airfare (Need autenticate)");
        System.out.println("[6] ???");
        System.out.println("[7] Exit Application");
    }

    public static int getUserChoice(){
        int i = getInt();
        sc.nextLine();
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
        System.out.println("Enter Source Location: ");
        SrcnDest[0] = getStringUpper();
        System.out.println("Enter Destination Location: ");
        SrcnDest[1] = getStringUpper();
        return SrcnDest;
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

    
    public static int getSessionID() {
        System.out.println("Enter Session ID (hint: hardcoded as 31337): ");
        return getInt();
    }




    public static int getInt(){
        try{
            int i = sc.nextInt();
            return i;
        } catch(Exception e) {
            System.out.println("Invalid int");
            return getInt();
        }
    }

    public static double getDouble(){
        try{
            double i = sc.nextDouble();
            return i;
        } catch(Exception e) {
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


    
}
