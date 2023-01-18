package com.example.loginwindow;

import org.json.simple.JSONObject;

import java.util.HashMap;

public class Packet {


    JSONObject request;
    public Packet(String param){
        String[] parts = param.split(" ");
        HashMap<String, String> map = new HashMap<String,String>();
        for(int i =0; i< parts.length;i++){
            String[] var = parts[i].split(":");
            if(var.length==2) {
                map.put(var[0], var[1]);
            }

        }
        request = JSONHandler.convertHashMapToJson(map);
    }

    public String getStringRequest(){
        return request.toJSONString();
    }
    public JSONObject getJSONRequest(){
        return request;
    }
}


