import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.Socket;
import java.util.List;

public  class netHandler {


//    public class fileSender extends Thread{
//
//        private String file_path;
//        private Socket socket;
//        private DataOutputStream out;
//        private int status;
//        public fileSender(Socket isocket, String file) throws IOException {
//            file_path = file;
//            socket = isocket;
//            out = new DataOutputStream(socket.getOutputStream());
//        }public void run() {
//                try {
//                        int bytes = 0;
//                        // Open the File where he located in your pc
//                        File file = new File(file_path);
//                        FileInputStream fileInputStream
//                                = new FileInputStream(file);
//
//                        // Here we send the File to Server
//                        out.writeLong(file.length());
//                        // Here we  break file into chunks
//                        byte[] buffer = new byte[4 * 1024];
//                        while ((bytes = fileInputStream.read(buffer))
//                                != -1) {
//                            // Send the file to Server Socket
//                            out.write(buffer, 0, bytes);
//                            out.flush();
//                        }
//                        // close the file here
//                        fileInputStream.close();
//                        status = 1;
//                    }
//                catch(Exception i){
//                    status = -1;
//                }
//            }
//            public int status(){
//                return status;
//            }
//
//    }
//
//    public static class fileRecever extends Thread{
//
//        private final Socket socket;
//        private String file_name;
//        private String file_size;
//        private DataInputStream in;
//        private int status;
//        public fileRecever(Socket isocket, String file_name,String ifile_size) throws IOException {
//            file_name = file_name;
//            file_size = ifile_size;
//            socket = isocket;
//            in = new DataInputStream(socket.getInputStream());
//        }
//        public void run() {
//            try {
//                    int bytes = 0;
//                    FileOutputStream fileOutputStream
//                            = new FileOutputStream(file_name);
//
//                    long size = Long.parseLong(file_size);
//                    byte[] buffer = new byte[4 * 1024];
//                    while (size > 0
//                            && (bytes = in.read(
//                            buffer, 0,
//                            (int) Math.min(buffer.length, size)))
//                            != -1) {
//                        // Here we write the file using write method
//                        fileOutputStream.write(buffer, 0, bytes);
//                        size -= bytes; // read upto file size
//                    }
//                    // Here we received file
//                    print.log("File is Received");
//                    fileOutputStream.close();
//                    status = 1;
//
//            }
//            catch(Exception i){
//                status = -1;
//            }
//        }
//        }



    public static JSONObject recvJSON(DataInputStream in) throws IOException, ParseException {
        synchronized (in) {
            JSONObject obj = new JSONObject();
            print.log("recv: start");
            String txt = in.readUTF(in);
            print.log("recv: " + txt);
            try {
                JSONParser parser = new JSONParser();
                obj = (JSONObject) parser.parse(txt);
                return obj;
            }
            catch(ParseException e){
                return new JSONObject(){};
            }
        }
    }
    public static int recvFile(DataInputStream in,String file_name,String file_size){
        try {
            int bytes = 0;
            FileOutputStream fileOutputStream
                    = new FileOutputStream(file_name);

            long size = Long.parseLong(file_size);
            byte[] buffer = new byte[4 * 1024];
            while (size > 0
                    && (bytes = in.read(
                    buffer, 0,
                    (int) Math.min(buffer.length, size)))
                    != -1) {
                // Here we write the file using write method
                fileOutputStream.write(buffer, 0, bytes);
                size -= bytes; // read upto file size
            }
            // Here we received file
            print.log("File is Received");
            fileOutputStream.close();
            return 1;
        }
        catch(Exception i){
            return -1;
        }
    }
    public static int sendFile(DataOutputStream out,String file_path,String file_size) {
        try {
            int bytes = 0;
            // Open the File where he located in your pc
            File file = new File(file_path);
            FileInputStream fileInputStream
                    = new FileInputStream(file);

            // Here we send the File to Server
            // Here we  break file into chunks
            byte[] buffer = new byte[4 * 1024];
            while ((bytes = fileInputStream.read(buffer))
                    != -1) {
                // Send the file to Server Socket
                out.write(buffer, 0, bytes);
                out.flush();
            }
            // close the file here
            fileInputStream.close();
            return 1;
        }
        catch(Exception i){
            return 0;
        }
    }

    public static int sendTCP_JSON(DataOutputStream out, Packet packet) {
        JSONObject obj = packet.getJSONRequest();
        try {
            synchronized (out){
                    print.log("send: " + obj.toJSONString());
                    out.writeUTF(obj.toJSONString());
                    out.flush();
                    print.log("send: " + "finish");
                    return 1;
                }
        } catch (Exception e) {
            return -1;
        }
    }

}
