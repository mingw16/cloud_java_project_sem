import java.sql.*;
import java.util.ArrayList;

public class DBManager {

    //private final static String DBURL = "jdbc:mysql://172.17.0.2:3306/Blackcloud";
   // private final static String DBURL = "jdbc:mysql://172.17.0.2:3306/Blackcloud";
    private final static String DBURL = "jdbc:mysql://160.81.194.243:3366/Blackcloud";
    private final static String DBUSER = "haslo";
    private final static String DBPASS = "haslo";
    private final static String DBDRIVER = "com.mysql.jdbc.Driver";


    Statement statement ;
    Connection connection;
    static String Email;
    //zapytanie SQL
    private String query;
    public DBManager(){
        try {
            connection = DriverManager.getConnection(DBURL, DBUSER, DBPASS);
            if(connection == null) throw new SQLException();
            statement = connection.createStatement();
        } catch ( SQLException e) {
            print.error("connot connect to db" + e);
        }
    }
    public  void setEmail(String email){
        Email = email;
    }

    public  int addUser(String  email, String name, String surname, String pass) throws SQLException {
        try {
            String query = " insert into user (email, imie, nazwisko, haslo)"
                    + " values (?, ?, ?, ?)";
            PreparedStatement preparedStmt = connection.prepareStatement(query);
            preparedStmt.setString(1, email);
            preparedStmt.setString(2, name);
            preparedStmt.setString(3, surname);
            preparedStmt.setString(4, pass);
            preparedStmt.execute();
            return 1;
        }
        catch(Exception i){
            return -1;
        }
    }

    public  boolean isEmailUnique(String email) throws SQLException {
        CallableStatement cStmt = connection.prepareCall("{? = call checkEmail(?)}");
        cStmt.registerOutParameter(1, Types.INTEGER);
        cStmt.setString(2, email);
        cStmt.execute();
        Integer outputValue = cStmt.getInt(1);

        if (outputValue == 0){
            return false;
        }else {
            return true;
        }
    }

    public  ArrayList<ArrayList<String>> pobierzDaneZBazy(String Email) throws SQLException {
        ArrayList<ArrayList<String>> listOfLists = new ArrayList<ArrayList<String>>();

        PreparedStatement pst=null;
        ResultSet rs=null;
        pst=connection.prepareStatement("select * from usersFiles where email=? ");
        pst.setString(1,Email);
        rs=pst.executeQuery();
        while (rs.next())
        {
            listOfLists.add(new ArrayList<String>());
            listOfLists.get(listOfLists.size()-1).add(rs.getString(2));
            listOfLists.get(listOfLists.size()-1).add(rs.getString(3));
            listOfLists.get(listOfLists.size()-1).add(rs.getString(5));
        }
        return listOfLists;
    }

    public  int sprawdzLogowanie(String login, String haslo) throws SQLException {
        CallableStatement cStmt = connection.prepareCall("{? = call checkLogin(?,?)}");
        cStmt.registerOutParameter(1, Types.INTEGER);
        cStmt.setString(2, login);
        cStmt.setString(3, haslo);
        cStmt.execute();
        Integer outputValue = cStmt.getInt(1);

        if (outputValue == 0){
            return -1;
        }else {
            setEmail(login);
            return 1;
        }
    }
}
