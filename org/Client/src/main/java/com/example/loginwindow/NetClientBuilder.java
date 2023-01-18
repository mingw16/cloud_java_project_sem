package com.example.loginwindow;

import java.io.IOException;

public class NetClientBuilder {

    private static NetClient instance = null;
    public static int initNetClient(String host, int port){
        try {
            instance = new NetClient(host, port);
            return 1;
        }
        catch(IOException i){
            return -1;
        }

    }
    public static int Exist(){
        if(instance != null){
            return 1;
        }
        else return -1;
    }

    public static NetClient getInstance(){
        if(instance != null){
            return instance;
        }
        else return null;
    }
}
