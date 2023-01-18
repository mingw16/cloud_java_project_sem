package com.example.loginwindow;

import java.io.File;

public class SystemHandler {

    public static String getDownloadDir(){

        File temp = new File(System.getenv("temp"));
        String pobrane =  temp.getParentFile().getParentFile().getParentFile().getAbsolutePath() + "\\Downloads";
        String pobrane2 =  temp.getParentFile().getParentFile().getParentFile().getAbsolutePath() + "\\Pobrane";
        if(new File(pobrane).isDirectory()){
            return pobrane+"\\";
        }
        else if(new File(pobrane2).isDirectory()){
            return pobrane2+"\\";
        }

        else{
            return null;
        }
    }
}
