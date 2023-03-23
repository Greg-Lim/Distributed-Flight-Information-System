package com.sc4051.server;

import java.util.Scanner;

public class ServerView {

    public static Scanner sc = new Scanner(System.in);

    public static int getInvocation(){
        System.out.println("Selct Network invocation\n 1. Atleast once\n 2. Atmost once");
        int i = getInt();
        while(i!=1 & i!=2){
            System.out.println("Invalid");
            i = getInvocation();
        }
        return i;
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
}
