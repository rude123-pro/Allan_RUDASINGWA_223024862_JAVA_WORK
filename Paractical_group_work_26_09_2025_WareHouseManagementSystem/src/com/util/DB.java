package com.util;


 import java.sql.*;

	public class DB {
		
	    public static Connection getConnection() throws SQLException {
	        return DriverManager.getConnection(
	            "jdbc:mysql://localhost:3306/wms",
	            "root",
	            ""
	        );
	    }
	}
