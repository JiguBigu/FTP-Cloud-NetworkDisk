package JDBC;



import java.io.UnsupportedEncodingException;
import java.sql.*;
import java.util.Base64;

public class jdbc {

	final static String USERNAME = "root";
	final static String PASSWORD = "";
	final static String DRIVER = "com.mysql.jdbc.Driver";
	final static String URL = "jdbc:mysql://localhost:3306/fog?useUnicode=true&characterEncoding=utf8";
	static Connection connection;
	static final String ERRO_PASSWORD = "&";

	public jdbc(){
		try {
			Class.forName(DRIVER);
			System.out.println("Register driver success");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println("Register driver failure");
		}
		connection=getConnection();
	}

	public static Connection getConnection() {

		try {
			connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
			System.out.println("Connection sucess !");

		} catch (Exception e) {
			System.out.println("Connection exception !");
		}

		return connection;

	}

	public static String getPassword(String sql) throws SQLException, UnsupportedEncodingException {
		String password = "";
		Statement statement=null;
		statement=connection.createStatement();
		ResultSet rs = statement.executeQuery(sql);
        if(rs.next()) {
            password = rs.getString("password");
            byte[] base64decodedBytes = Base64.getDecoder().decode(password);
            return new String(base64decodedBytes, "UTF-8");
        } else{
            password = jdbc.ERRO_PASSWORD;
        }
		return password;
	}


	public boolean verifyUser(User user) throws SQLException, UnsupportedEncodingException {
		String password = user.getPassword();
		String sql = "select * from user where username='" + user.getUsername() + "'";
		if (getPassword(sql).equals(password)){
			return true;
		}else {
			return false;
		}
	}

	public boolean hasUser(User user) throws SQLException {
        System.out.println("jdbc---name:" + user.getUsername());
		String sql = "select * from user where username='" + user.getUsername() + "'";
		String flag = user.getUsername();
		Statement statement=null;
		statement=connection.createStatement();
		ResultSet rs = statement.executeQuery(sql);
		if(rs.next() && rs.getString("username").equals(flag)){
                return true;
		} else {
			return false;
		}
	}

	public void insertUser(User user){
		String sql = "insert into user(username,password) values(?, ?)";
		PreparedStatement pstmt;
		try {
			pstmt = (PreparedStatement) connection.prepareStatement(sql);
			pstmt.setString(1, user.getUsername());
			pstmt.setString(2, user.getPassword());
			pstmt.executeUpdate();
			pstmt.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
