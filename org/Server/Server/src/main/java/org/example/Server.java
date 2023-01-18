
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import javax.xml.crypto.Data;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

class Server extends Thread {

    class userThread extends Thread{
        // String name;
        private Socket socket = null;
        public boolean user_is_login = false;
        private int user_status;
        private volatile DataOutputStream out = null;
        private volatile  DataInputStream in = null;

        private HashMap<Integer,userThread> user_map;
        //////////////////////////////////////////////
        public userThread(Socket isocket, HashMap<Integer,userThread> iuser_map, DataInputStream in, DataOutputStream out){
            this.user_map = iuser_map;
            this.socket = isocket;
            user_status = 1;
            this.in = in;
            this.out = out;

        }
        ////////////////////////////////////////////
        public int getStatus(){
            return user_status;
        }
        ///////////////////////////////////////////
        public void close() throws IOException{
            user_status = 0;
            user_map.remove(socket.getPort());
            socket.close();
        }
        /////////////////////////////////////////////////////////////////////////////////

        private int validateRecvPacket(JSONObject obj){
            try{
                if(obj.containsKey("type")){
                    switch(Integer.parseInt((String) obj.get("type"))){
                        case 1:
                            if(obj.containsKey("login") && obj.containsKey("pass")){
                                return 1;
                            }
                        case 2:
                            if(obj.containsKey("name") && obj.containsKey("surname")&& obj.containsKey("email")&& obj.containsKey("pass")){
                                return 1;
                            }
                            break;
                        case 3:
                            return 1;
                        case 4:
                            if(obj.containsKey("filename") && obj.containsKey("filesize")){
                                return 1;
                            }
                            break;
                        case 46:
                            if(obj.containsKey("hash")){
                                return 1;
                            }
                            break;
                        case 6:
                            return 1;

                        case 5:
                            if(obj.containsKey("filename")){
                                return 1;
                        }
                            break;
                        case 511:
                            if(obj.containsKey("hash")){
                                return 1;
                            }
                            break;
                        case 9:
                            if(obj.containsKey("filename")){
                                return 1;
                            }
                            break;
                    }
                    return 0;
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
        public void run(){

            try {
                String line;
                boolean running = true;
                print.print("new user just connect: "+ getName());
                File working_dir = null;
                while (running && !socket.isClosed()) {
                    ///*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/
                    ///*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/
                    print.print(getName()+" start to listen");
                    JSONObject recv = netHandler.recvJSON(in);
                    if (validateRecvPacket(recv) == 1) {
                        if (Integer.parseInt((String) recv.get("type")) == 1) {
                            String login = (String) recv.get("login");
                            String pass = (String) recv.get("pass");
                            if(db_manager.sprawdzLogowanie(login, pass)==1 && !user_is_login){
                                netHandler.sendTCP_JSON(out,new Packet("type:11"));
                                print.log("login succesfully by " + login);
                                String dir_name = hash.checksumString(login);
                                synchronized (this) {
                                    working_dir = new File("./data/" + dir_name);
                                }
                                user_is_login = true;
                            }
                            else {
                                netHandler.sendTCP_JSON(out,new Packet("type:12"));
                            }

                        }

                        else if(Integer.parseInt((String) recv.get("type")) == 2 && !user_is_login){
                            String name = (String) recv.get("name");
                            String surname = (String) recv.get("surname");
                            String email = (String) recv.get("email");
                            String pass = (String) recv.get("pass");

                           boolean result =  db_manager.isEmailUnique(email);
                           if(result){
                               int add_user_result = db_manager.addUser(email, name, surname, pass);
                               if( add_user_result ==1 ){
                                   netHandler.sendTCP_JSON(out, new Packet("type:21"));
                                   String dir_name =hash.checksumString(email);
                                   new File("./data/"+dir_name).mkdirs();
                                   PrintWriter writer = new PrintWriter("./data/"+dir_name+"/welcome.txt", "UTF-8");
                                   writer.println("Welcome to the cloud :)");
                                   writer.close();
                               }
                               else{
                                   netHandler.sendTCP_JSON(out,new Packet("type:22"));
                               }
                           }
                           else{
                               netHandler.sendTCP_JSON(out,new Packet("type:23"));
                           }
                        }

                        else if(Integer.parseInt((String) recv.get("type")) == 3  && user_is_login){
                            List<String> file_list = FileManager.walk(working_dir.getPath());
                            String raw = file_list.toString();
                            raw = raw.replace(" ", ">");
                            print.print(raw);
                            netHandler.sendTCP_JSON(out, new Packet("type:31 content:"+ raw));
                        }

                        else if(Integer.parseInt((String) recv.get("type")) == 4  && user_is_login){
                            String filename = (String) recv.get("filename");
                            String filesize = (String) recv.get("filesize");
                            netHandler.sendTCP_JSON(out,new Packet("type:41"));
                            String fullpath = working_dir+"/"+filename;
                            int result = netHandler.recvFile(in,fullpath,filesize);
                            if(result == 1){
                                netHandler.sendTCP_JSON(out, new Packet("type:44"));
                                JSONObject recv2 = netHandler.recvJSON(in);
                                int result2 = validateRecvPacket(recv2);
                                if(result2 ==1 ){
                                    if(recv2.get("hash").equals(hash.checksumFile(fullpath))){
                                        netHandler.sendTCP_JSON(out, new Packet("type:461"));
                                    }
                                    else {
                                        netHandler.sendTCP_JSON(out, new Packet("type:462"));
                                    }
                                }
                            }
                            else {
                                netHandler.sendTCP_JSON(out,new Packet("type:44"));
                            }

                        }

                        else if(Integer.parseInt((String) recv.get("type")) == 5  && user_is_login){
                            String filename =(String) recv.get("filename");
                            File file = new File(working_dir + "/"+filename);
                            if(file.isFile() ){
                                String fileSize = Long.toString(file.length());
                                netHandler.sendTCP_JSON(out, new Packet("type:51 filesize:"+ fileSize ));
                                netHandler.sendFile(out,working_dir+"//"+filename,fileSize);
                                JSONObject recv1 = netHandler.recvJSON(in);
                                int result1 = validateRecvPacket(recv1);
                                if(result1==1) {
                                    String hash1 =(String) recv1.get("hash");
                                    if(hash1.equals(hash.checksumFile(working_dir+"//"+filename))){
                                        netHandler.sendTCP_JSON(out,new Packet("type:5111"));
                                        print.log("client download file correctly");
                                    }
                                    else {
                                        netHandler.sendTCP_JSON(out,new Packet("type:5112"));
                                    }
                                }
                            }
                        }
                        else if(Integer.parseInt((String) recv.get("type")) == 6 && user_is_login ){
                            try {
                                netHandler.sendTCP_JSON(out, new Packet("type:61"));
                                close();
                                print.log("user just logout");
                            }
                            catch(Exception i){
                                print.error("some error occur when try to logot");
                            }
                        }
                        else if(Integer.parseInt((String) recv.get("type")) == 7  && user_is_login){
                        }

                        else if(Integer.parseInt((String) recv.get("type")) == 8  && user_is_login){
                        }
                        else if(Integer.parseInt((String) recv.get("type")) == 9  && user_is_login){

                            String filename = (String) recv.get("filename");

                            if(new File(working_dir+"/"+filename).delete()){
                                netHandler.sendTCP_JSON(out,new Packet("type:91"));
                            }
                        }

                        ///*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/fe*/*/*/*/*/*/*/*/*/*/*/*/
                        ///*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/
                    }
                }
            }
            catch (IOException e) {
                try {
                    close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

            } catch (ParseException e) {
                throw new RuntimeException(e);
            } catch (SQLException e) {
                try {
                    close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                throw new RuntimeException(e);
            } catch (NoSuchAlgorithmException e) {
                try {
                    close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                throw new RuntimeException(e);
            }  finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                    if (in != null) {
                        in.close();
                    }
                    if(!socket.isClosed()){
                        socket.close();
                    }

                }
                catch (IOException e) {
                    print.print("2 unhandle exception");
                }
            }}



    }

    //initialize socket and input stream
    private ServerSocket    server ;

    protected DBManager db_manager;
    private HashMap<Integer,userThread> user_map = new HashMap<Integer, userThread>();
    public final int PORT;
    public final InetAddress HOST;

    public Server() throws IOException {

        PORT = 8085;
        HOST = InetAddress.getByName("127.0.0.1");
        createSocket(PORT);
        db_manager = new DBManager();
    }

    private void createSocket(int port) throws IOException{
        try{
            server = new ServerSocket(port);
            System.out.println("Server started");
        }
        catch(IOException i)
        {
            print.error("can not create a server socket");
            throw new IOException("<ERROR> can not create a server socket");
        }
    }


    public void listen() throws IOException{
        //user_list.add(null);

        try{
            print.print("server start to listen");
            boolean running = true;
            int counter = 0;
            while(running && !server.isClosed()){
                counter++;
                Socket s = server.accept();
                DataInputStream in = new DataInputStream(s.getInputStream());
                DataOutputStream out = new DataOutputStream(s.getOutputStream());

                user_map.put(s.getPort(),new userThread(s,user_map,in,out)); // add new user to list
                user_map.get(s.getPort()).setName(Integer.toString(counter));
                user_map.get(s.getPort()).start();
                print.log("New connect accepted from " + s.getInetAddress() + ":"+s.getPort());
            }
        }
        catch(IOException i){
            print.error("critical server error");
            System.exit(-1);
        }
    }

    public void run(){
        try {
            listen();
        } catch (IOException e) {
            print.print("3 unhandle exception");
        }
    }
    public void close() throws IOException{
        server.close();
    }
    public static void main(String args[]) throws IOException, InterruptedException {
        Server server = new Server();
        server.start();
        server.join();
    }



}