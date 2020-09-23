package com.java.socket.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.java.socket.modal.ChatUser;

public class SqlServer {
	private static String JDBC_DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";  
	private static String DB_URL = "jdbc:sqlserver://58.250.250.2:1433;databaseName=interview0710;";
	private static String USER = "interview0710";
	private static String PASS = "QJopHVa8";

    public static Connection getConnection() throws Exception {
			Class.forName(JDBC_DRIVER);
			return DriverManager.getConnection(DB_URL, USER, PASS);
    }
    
    public void insert(ChatUser user) throws Exception {
    	String sql = "insert into Temp values(?,?,?,?,?)";
    	Connection conn = SqlServer.getConnection();
    	PreparedStatement stmt = conn.prepareStatement(sql);
    	//获取当前时间
		stmt.setInt(1, user.getFlag());
		stmt.setString(2, user.getChatMsg());
		stmt.setTimestamp(3, user.getChatTime());
		stmt.setString(4, user.getIpAddress());
		stmt.setString(5, user.getUserName());
		int num = stmt.executeUpdate();
		stmt.close();
        conn.close();
    }
    

    public String getDate() {
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	Date new_time=new Date(System.currentTimeMillis());
    	String date = sdf.format(new_time);
    	return date;
    }

}
