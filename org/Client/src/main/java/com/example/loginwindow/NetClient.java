package com.example.loginwindow;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
public class NetClient {
    public final int PORT;
    public final InetAddress HOST;
    public Socket socket;
    private volatile DataOutputStream out = null;
    private volatile DataInputStream in = null;
    private String remote_file_tree;

    /// packet type ///
    // 1 - login request
    // 2 - register request
    // 3 - get file tree request
    // 4 - send file request
    // 5 - download file request

    public NetClient(String host, int port) throws IOException {
        PORT = port;
        HOST = InetAddress.getByName(host);

        connectToServer();

        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
    }

    private int connectToServer() throws IOException {
        try {
            socket = new Socket(HOST, PORT);
            return 1;
        }
        catch (IOException e) {
            return -1;
        }
    }

    public int login(String login,String pass){
        try{
            //netHandler.sendTCP_JSON(out, JSONHandler.convertHashMapToJson(new HashMap<String,String>(){{put("type","1");put("login",login);put("pass",pass);}}));
            netHandler.sendTCP_JSON(out,new Packet("type:1 login:"+login+" pass:"+pass));
            JSONObject recv = netHandler.recvJSON(in);
            if(validateRecvPacket(recv) == 1){
                if(Integer.parseInt((String) recv.get("type")) ==  11){
                    print.log("login succesfully");
                    return 1;
                }
                else if(Integer.parseInt((String) recv.get("type")) == 12){
                    print.log("can not log in ");
                    return -1;
                }
            }
            else{
                print.error("communication with server error");
            }

            return 1;
        }
        catch(Exception i){
            return -1 ;
        }
    }

    public int logout(){
        try{
            netHandler.sendTCP_JSON(out,new Packet("type:6"));
            close();
            return 1;
        }
        catch (Exception i){
            print.error("cannot logout");
            return -1;
        }
    }

    public int registerNewUser(String name, String surname, String email, String pass){

        try{
            netHandler.sendTCP_JSON(out, new Packet("type:2 name:"+name+" surname:"+surname+" email:"+email+" pass:"+ pass));
            JSONObject recv = netHandler.recvJSON(in);
            if(validateRecvPacket(recv) == 1){
                if(Integer.parseInt((String) recv.get("type")) ==  21){
                    print.log("new user registered");
                    return 1;
                }
                else if(Integer.parseInt((String) recv.get("type")) == 22){
                    print.log("cannot register new user ");
                    return -1;
                }
                else if(Integer.parseInt((String) recv.get("type")) == 23){
                    print.log("email incorrect ");
                    return -2;
                }
                }

            return 1;
        }
        catch(Exception I){
            return -1;
        }
    }

    private int validateRecvPacket(JSONObject obj){
        try{
            if(obj.containsKey("type")){
                switch(Integer.parseInt((String) obj.get("type"))) {
                    case 31:
                        if (obj.containsKey("content")) {
                            return 1;
                        }
                        else return 0;
                    case 51:
                        if (obj.containsKey("filesize")) {
                            return 1;
                        }
                        else return 0;
                }
                return 1;
            }
            else{
                print.error("receved packet has no type");
                return -3;
            }
        }
        catch(Exception i){
            print.error("connot read JSONObject");
            return -2;
        }
    }

    public int close() throws IOException {
        try {
            in.close();
            out.close();
            socket.close();

            return 1;
        }
        catch(Exception i) {
            return -1;}
    }

    public ArrayList<ArrayList<String>> getFileTree() throws IOException, ParseException {
        return convertRawFileTreeIntoArrayList(getFileTreeFromServer());
    }
    public String getFileTreeFromServer() throws IOException, ParseException {
        netHandler.sendTCP_JSON(out, new Packet("type:3"));
        JSONObject recv = netHandler.recvJSON(in);
        if (validateRecvPacket(recv) == 1) {
            if (Integer.parseInt((String) recv.get("type")) == 31) {
                return(String) recv.get("content");
            } else if (Integer.parseInt((String) recv.get("type")) == 32) {
                print.log("cannot see file tree");
                return null;
            }
        }
        return null;

    }

    private ArrayList<ArrayList<String>> convertRawFileTreeIntoArrayList(String raw){
        //filepath<filename<lastmodified
        ArrayList<ArrayList<String>> list = new ArrayList<>();
        if(raw != null) {
            raw = raw.replace(">"," ");
            String[] parts = raw.split(",");
            int counter = 0;
            for (String a : parts) {
                String[] rows = a.split("<");
                list.add(new ArrayList<String>());
                for (String gowno : rows) {
                    list.get(counter).add(gowno);
                }
                counter++;
            }
        }
        return list;
    }
    public void sendFile(String file_size, String filePath) throws IOException, ParseException, InterruptedException, NoSuchAlgorithmException {
        netHandler.sendTCP_JSON(out,new Packet("type:4 filename:"+new File(filePath).getName()+" filesize:"+file_size ));
        JSONObject recv = netHandler.recvJSON(in);
        int result = validateRecvPacket(recv);
        if(result ==1 && Integer.parseInt((String)recv.get("type"))==41){
//            Thread task =new netHandler.fileSender(socket,filePath);
//            task.start();
//            task.join();
            netHandler.sendFile(out,filePath);
            JSONObject recv1 = netHandler.recvJSON(in);
            int result1 = validateRecvPacket(recv1);
            if(result1==1){
                if(Integer.parseInt((String)recv1.get("type"))==44){
                    String hash = com.example.loginwindow.hash.checksum(filePath);
                    netHandler.sendTCP_JSON(out,new Packet("type:46 hash:"+hash));
                    JSONObject recv2 = netHandler.recvJSON(in);
                    int result2 = validateRecvPacket(recv2);//sdfkglsdjfgklsjdfg
                    if(result2 == 1  ){
                        if(Integer.parseInt((String) recv2.get("type")) ==  461){
                            print.print("file send correctly" + hash);
                        }
                    }
                }
            }
        }


    }

    public int deleteFile(String filename) throws IOException, ParseException {

        netHandler.sendTCP_JSON(out, new Packet("type:9 filename:" + filename));
        JSONObject recv1 = netHandler.recvJSON(in);
        int result1 = validateRecvPacket(recv1);
        if (result1 == 1) {
            if (recv1.get("type").equals("91")) {
                print.log("deleting correctly");
                return 1;
            } else return -1;
        }
        return -1;
    }

    public int downloadFile(String filename) throws IOException, ParseException, NoSuchAlgorithmException {
        try {
            netHandler.sendTCP_JSON(out, new Packet("type:5 filename:" + filename));
            JSONObject recv1 = netHandler.recvJSON(in);
            int result1 = validateRecvPacket(recv1);
            if (result1 == 1) {
                String fileSize = (String) recv1.get("filesize");
                String fullpath = SystemHandler.getDownloadDir() + filename;
                if (netHandler.recvFile(in, fullpath, fileSize) == 1) {
                    String hash = com.example.loginwindow.hash.checksum(fullpath);
                    netHandler.sendTCP_JSON(out, new Packet("type:511 hash:" + hash));
                    JSONObject recv2 = netHandler.recvJSON(in);
                    int result2 = validateRecvPacket(recv2);
                    if (result2 == 1) {
                        if (recv2.get("type").equals("5111")) {
                            print.log("finish downloading a file");
                        } else {
                            new File(fullpath).delete();
                        }
                    }
                }

            }
            return 1;
        }
        catch(Exception i){
            return -1;
        }
    }



}
