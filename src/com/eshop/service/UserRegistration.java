package com.eshop.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import com.eshop.common.User;
import com.eshop.util.DBUtil;

public class UserRegistration {

	public boolean registerScreen() throws SQLException {
		Scanner sc = new Scanner(System.in);
		System.out.println("----------------User Registration--------------");
		System.out.print("Enter your name : ");
		String nm = sc.nextLine();
		System.out.print("Enter your mobile no.: ");
		String mob = sc.nextLine();
		System.out.print("Enter your address : ");
		String addr = sc.nextLine();
		System.out.print("Enter your Email : ");
		String em = sc.next();
		System.out.print("Enter your password : ");
		String ps = sc.next();
		User user = new User(nm, mob, addr, ps, em);
		return createUser(user);

	}

	private boolean createUser(User user) throws SQLException {
		Connection conn = DBUtil.getconection();
		String query = "insert into UserReg(name, mob, address, pass, email) values(?,?,?,?,?)";

		PreparedStatement pst = conn.prepareStatement(query);
		pst.setString(1, user.getName());
		pst.setString(2, user.getMob());
		pst.setString(3, user.getAddress());
		pst.setString(4, user.getPass());
		pst.setString(5, user.getEmail());
		return pst.execute();
	}

	public User login(String email, String pass) {
		Connection conn = DBUtil.getconection();
		try {
			PreparedStatement pst = conn.prepareStatement("SELECT * FROM UserReg WHERE email = ? and pass = ?");
			pst.setString(1, email);
			pst.setString(2, pass);
			ResultSet rs = pst.executeQuery();
			if (rs != null && rs.next() == true) {
				User user = new User();
				user.setId(rs.getInt(1));
				user.setName(rs.getString(2));
				user.setMob(rs.getString(3));
				user.setAddress(rs.getString(4));
				user.setEmail(rs.getString(6));
				return user;
			} else {
				System.out.println("Invalid email and password. Try again!");
				return null;
			}

		} catch (SQLException e) {
			System.out.println("Server Problem. Try later!");
		}
		return null;
	}
}
