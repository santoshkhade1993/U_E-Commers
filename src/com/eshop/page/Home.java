package com.eshop.page;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.eshop.common.Product;
import com.eshop.common.User;
import com.eshop.service.UserRegistration;
import com.eshop.util.DBUtil;

public class Home {

	static List<Product> productList = new ArrayList<>();
	static Map<Integer, Integer> productById = new HashMap<>();
	static User user = null;
	static List<Map<String, Integer>> cartList = new ArrayList<>();; // cart table values;
	static List<Map<String, Integer>> OrderList = new ArrayList<>();
	public void showMenu() {
		System.out.println("1)Registration \n2)Login \n3)Exit");
		System.out.print("Please select option (1/2/3) : ");
		Scanner sc = new Scanner(System.in);
		int choose = sc.nextInt();
		switch (choose) {
		case 1: {
			UserRegistration user = new UserRegistration();
			try {
				boolean isCreated = user.registerScreen();
				if (isCreated == true) {
					System.out.println("Congratulation registration done successfully");
					doLogin();
				} else
					throw new Exception("fail");
			} catch (Exception e) {
				System.out.println("Registration failed. Try again");
			}
			showMenu();
			break;
		}
		case 2:
			doLogin();
			break;
		case 3:
			System.exit(0);
			break;
		default:
			dashboard();
			System.out.println("wrong choose: " + choose);
			showMenu();
		}
	}

	private void doLogin() {
		System.out.println("----------------User Login------------------");
		Scanner sc = new Scanner(System.in);
		System.out.print("Enter Email : ");
		String email = sc.next();
		System.out.print("Enter Password : ");
		String pass = sc.next();
		UserRegistration users = new UserRegistration();
		user = users.login(email, pass);
		if (user != null) {
			updateProductList();
			dashboard();
		} else
			showMenu();
	}

	private void dashboard() {
		System.out.println("----------------Dashboard------------------");
		System.out.println("1)Product List \n2)Order History \n3)cart\n4)Logout");
		System.out.print("Please select option (1/2/3/4) : ");
		Scanner sc = new Scanner(System.in);
		int choose = sc.nextInt();
		switch (choose) {
		case 1: {
			showProductList();
			addProductToCart();
			break;
		}
		case 2:
			showOrderHistory();
			break;
		case 3:
			showCart();
		case 4:
			user = null;
			// after logout by current user cart list empty 
			cartList.clear();
			OrderList.clear();
			showMenu();
			break;
		default:
			System.out.println("wrong choose: " + choose);
			showMenu();
		}
	}

	private void showOrderHistory() {
		System.out.println("--------------------Order List-------------------");
		Connection con = DBUtil.getconection();
	try {String query="select * from orders where user_id=? ";
		PreparedStatement pst=	con.prepareStatement(query);
		pst.setInt(1,user.getId());
		ResultSet rs=pst.executeQuery();
		if (rs != null && rs.next()) {
			Map<String, Integer> map = new HashMap<>();
			map.put("productId", rs.getInt(1));
			map.put("qty", rs.getInt(2));
			map.put("User_Id",rs.getInt(3) );
			map.put("amount", rs.getInt(4));
			OrderList.add(map);
		} else {
			System.out.println("Order List is Empty. Please order the product");
		}
		
		System.out.println("|Product Name\t|Price\t|Qty\t|Amount");
		for (Map<String, Integer> order : OrderList) {
			Product prod = productList.get(productById.get(order.get("productId"))); // ....
			System.out.println("|" + prod.getProductName() + "\t|" + prod.getProductPrice() + "\t|"
					+ order.get("qty") + "\t|" + order.get("amount"));
		}
		
		
		
		
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
		
	}

	private void showCart() {
		System.out.println("----------------Cart List---------------------");
		Connection conn = DBUtil.getconection();
		try {
			PreparedStatement pst = conn.prepareStatement("SELECT * FROM CART WHERE user_id = ?");
			pst.setInt(1, user.getId());
			ResultSet rs = pst.executeQuery();
			if (rs != null && rs.next()) {
				Map<String, Integer> map = new HashMap<>();
				map.put("productId", rs.getInt(1));
				map.put("qty", rs.getInt(2));
				map.put("amount", rs.getInt(4));
				cartList.add(map);
			} else {
				System.out.println("Cart Empty. Please product in cart");
			}

			System.out.println("|Product Name\t|Price\t|Qty\t|Amount");
			for (Map<String, Integer> cart : cartList) {
				Product prod = productList.get(productById.get(cart.get("productId"))); // ....
				System.out.println("|" + prod.getProductName() + "\t|" + prod.getProductPrice() + "\t|"
						+ cart.get("qty") + "\t|" + cart.get("amount"));
			}
			buyProduct();

		} catch (SQLException e) {
			System.out.println("Server Problem. Try again!");
		}
	}

	private void buyProduct() {
		Scanner sc = new Scanner(System.in);
		System.out.println("\n1)Buy Product\n2)Remove Product\n3)dashboard");
		System.out.println("Select options: ");
		int ch = sc.nextInt();
		switch (ch) {
		case 1:
			placeOrder();
			dashboard();
			break;
		case 2:
			System.out.print("Enter Product Id : ");
			int prodId = sc.nextInt();
			removeProductFromCart(prodId);
			buyProduct();
			break;
		case 3:
			dashboard();
			break;
		default:
			buyProduct();
		}
	}

	private void placeOrder() {
		System.out.println("----------------Place Order---------------------");
		Connection conn = DBUtil.getconection();
		try {
			int amount = 0;
			for (Map<String, Integer> cart : cartList) {
				PreparedStatement pst = conn.prepareStatement("Insert into orders values(?,?,?,?)");
				pst.setInt(1, cart.get("productId"));
				pst.setInt(2, cart.get("qty"));
				pst.setInt(3, user.getId());
				pst.setInt(4, cart.get("amount"));
				amount = amount + cart.get("amount");
				// amount+=cart.get("amount");
				pst.execute();
				updateProductQty(cart.get("productId"),cart.get("qty"));
			}
			updateProductList();
			
			clearcart(user.getId());
			
			// payment
			System.out.println("Your order is placed with total amount " + amount);
		} catch (SQLException e) {

		}

	}

	private void clearcart(int id) {
		Connection con=DBUtil.getconection();
		
		try {String query="delete from cart where user_id=?";
			PreparedStatement pst= con.prepareStatement(query);
			pst.setInt(1, id);
			pst.execute();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void updateProductQty(Integer prodId, Integer qty) {
		Connection conn = DBUtil.getconection();
		try {
			Product prod = Home.productList.get(Home.productById.get(prodId));
			int currentQty = prod.getProductQuantity() - qty;
			PreparedStatement pst = conn.prepareStatement("update product set product_quantity = ? where product_id = ?");
			pst.setInt(1, currentQty);
			pst.setInt(2, prodId);
			pst.execute();
		} catch (SQLException e) {
		}
	}

	private void removeProductFromCart(int prodId) {
		Connection conn = DBUtil.getconection();
		try {
			PreparedStatement pst = conn.prepareStatement("delete from cart where product_id = ?");
			pst.setInt(1, prodId);
			pst.execute();
		} catch (SQLException e) {
		}
	}

	private void addProductToCart() {
		Scanner sc = new Scanner(System.in);
		System.out.println("----------------Add Prodct To Cart---------------------");
		System.out.print("Enter Product Id: ");
		int prodId = sc.nextInt();
		System.err.println("Enter quantity: ");
		int qty = sc.nextInt();
		if (!productById.containsKey(prodId)) {
			System.out.println("Product id not found ");
			addProductToCart();
		} else {

			Product pro = productList.get(productById.get(prodId));
			if (pro.getProductQuantity() < qty) {
				System.out.println("Quantity should not more than " + pro.getProductQuantity());
				addProductToCart();
			} else {
				int amount = pro.getProductPrice() * qty;
				Connection conn = DBUtil.getconection();
				try {
					PreparedStatement pst = conn.prepareStatement("Insert Into cart values(?,?,?,?)");
					pst.setInt(1, prodId);
					pst.setInt(2, qty);
					pst.setInt(3, user.getId());
					pst.setInt(4, amount);
					pst.execute();
					System.out.println("Product added to cart successfully.");
				} catch (SQLException e) {
					System.out.println("Product Not Added. Try again!");
				}
				dashboard();
			}
		}
	}

	private void showProductList() {
		updateProductList();
		System.out.println("-------------------Product List---------------------");
		System.out.println("|Ids\t|Product Name\t|Price\t|Qty\t|Product Description");
		for (Product prod : productList) {
			System.out
					.println("|" + prod.getProductId() + "\t|" + prod.getProductName() + "\t|" + prod.getProductPrice()
							+ "\t|" + prod.getProductQuantity() + "\t|" + prod.getProductDescription());
		}
	}

	private void updateProductList() {
		Connection conn = DBUtil.getconection();
		try {
			PreparedStatement pst = conn.prepareStatement("SELECT * FROM product WHERE product_quantity > 0");
			ResultSet rs = pst.executeQuery();
			int count = 0;
			productList.clear();
			productById.clear();
			while (rs != null && rs.next()) {
				Product product = new Product();
				product.setProductId(rs.getInt(1));
				product.setProductName(rs.getString(2));
				product.setProductDescription(rs.getString(3));
				product.setProductPrice(rs.getInt(4));
				product.setProductQuantity(rs.getInt(5));
				productList.add(product);
				productById.put(product.getProductId(), count++);
			}
		} catch (SQLException e) {

		}

	}

}
