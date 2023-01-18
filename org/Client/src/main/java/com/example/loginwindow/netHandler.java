package com.example.loginwindow;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;

public  class netHandler {




    public static JSONObject recvJSON(DataInputStream in) throws IOException, ParseException {
        synchronized (in) {
            JSONObject obj = new JSONObject();
            print.log("recving");
            String txt = in.readUTF(in);
            print.log("recv: " + txt);
            print.log(txt);
            JSONParser parser = new JSONParser();
            obj = (JSONObject) parser.parse(txt);
            return obj;
        }
    }

    public static void sendFile(DataOutputStream out, String file_path){
        try {
            int bytes = 0;
            File file = new File(file_path);
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] buffer = new byte[4 * 1024];
            while ((bytes = fileInputStream.read(buffer))
                    != -1) {
                out.write(buffer, 0, bytes);
                out.flush();
            }
            fileInputStream.close();
        }
        catch(Exception i){
        }
    }

    public static int recvFile(DataInputStream in, String  filename, String fileSize){
        try {
            int bytes = 0;
            FileOutputStream fileOutputStream
                    = new FileOutputStream(filename);

            long size = Long.parseLong(fileSize);
            print.log(Long.toString(size));
            byte[] buffer = new byte[4 * 1024];
            while (size > 0
                    && (bytes = in.read(
                    buffer, 0,
                    (int) Math.min(buffer.length, size)))
                    != -1) {
                fileOutputStream.write(buffer, 0, bytes);
                size -= bytes;
            }
            fileOutputStream.close();
            return 1;
        }
        catch(Exception i){
           return  -1;
        }
    }
    public static int sendTCP_JSON(DataOutputStream out, Packet packet){
        synchronized (out) {
            JSONObject obj = packet.getJSONRequest();
            try {
                print.log("send: " + obj.toJSONString());
                out.writeUTF(obj.toJSONString());
                out.flush();
                print.log("sending" + "finish");
                return 1;
            } catch (Exception e) {
                return -1;
            }
        }
    }
}
