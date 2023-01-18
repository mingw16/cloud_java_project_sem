package com.example.loginwindow;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class print {
    public static void print(String txt){
        final String ANSI_GREEN = "\u001B[32m";
        System.out.println(ANSI_GREEN+txt);
    }

    public static void log(String txt){
        final String ANSI_WHITE = "\u001B[37m";
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        System.out.println(ANSI_WHITE+dtf.format(now)+" <LOG> " + txt);
    }
    public static void error(String txt){
        final String ANSI_RED = "\u001B[31m";
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        System.out.println(ANSI_RED+dtf.format(now)+" <ERROR> " + txt);
    }


}
