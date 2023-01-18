import java.io.FileInputStream;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class hash {


    public static String checksumFile(String filepath) throws IOException, NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        try (DigestInputStream dis = new DigestInputStream(new FileInputStream(filepath), md)) {
            while (dis.read() != -1) ; 
            md = dis.getMessageDigest();
        }

        StringBuilder result = new StringBuilder();
        for (byte b : md.digest()) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
    public static String checksumString(String txt) throws IOException, NoSuchAlgorithmException {

        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] bin = md.digest(txt.getBytes());
        StringBuilder result = new StringBuilder();
        for (byte b : bin) {
            result.append(String.format("%02x", b));
        }
        return result.toString();

    }
    
}
