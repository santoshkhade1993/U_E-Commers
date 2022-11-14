package com.eshop.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {

	public static Connection getconection() {
		Connection con = null;
		try {

			Class.forName("com.mysql.cj.jdbc.Driver");

			String url = "jdbc:mysql://localhost:3306/shopping";

			con = DriverManager.getConnection(url, "root", "Qwerty@123");
		} catch (ClassNotFoundException | SQLException e) {

			e.printStackTrace();
		}
		return con;

	}
}
