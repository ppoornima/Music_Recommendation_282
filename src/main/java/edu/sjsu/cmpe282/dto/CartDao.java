package edu.sjsu.cmpe282.dto;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.json.JSONArray;
import org.json.JSONObject;

import edu.sjsu.cmpe282.domain.Track;

public class CartDao {
	public static final int STATUS_SUCCESS_CODE = 200;
	public static final String STATUS_SUCCESS_MESSAGE = "success";
	public static final int STATUS_ERROR_CODE =500;
	public static final String STATUS_ERROR_MESSAGE="error";

	Connection conn = null;
	Statement stmt = null;

	public CartDao()
	{
		try{
			try {
				Class.forName("com.mysql.jdbc.Driver");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//conn = DriverManager.getConnection("jdbc:mysql://localhost/cloudservices","root","root");

			conn = DriverManager.getConnection("jdbc:mysql://projectinstance.chtut8njmfxl.us-west-1.rds.amazonaws.com:3306/","root","rootroot"); 
			System.out.println("COnn:"+conn);
		}
		catch (SQLException e) {
			e.printStackTrace();

		}
	}


	@SuppressWarnings("finally")
	public String viewItemsInCart (String userid)
	{
		JSONObject response = new JSONObject();



		JSONArray cartItemArray = new JSONArray();

		ResultSet rs;
		System.out.println("---------------------------------------------");

		try {
			stmt = conn.createStatement();

			String query = "Select * from finalproject.cart where userid = "+userid+";";
			rs = stmt.executeQuery(query);

			while(rs.next())
			{

				JSONObject cartItems = new JSONObject();
				cartItems.put("userid", rs.getString(1));
				cartItems.put("trackid",rs.getString(2));
				cartItems.put("albumid",rs.getString(3));
				cartItems.put("artistid",rs.getString(4));
				cartItems.put("genreid",rs.getString(5));
				cartItemArray.put(cartItems);	
			}



		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		response.put("statusCode",STATUS_SUCCESS_CODE);
		response.put("statusMessage", STATUS_SUCCESS_MESSAGE);
		response.put("cartItems",cartItemArray );
		System.out.println("response cart: " +response.toString());
		return response.toString();
		}

	}
	public String getCurrentDateTime()
	{		  
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		java.util.Date date = new java.util.Date();
		System.out.println("Current Date Time : " + dateFormat.format(date));
		return dateFormat.format(date);
	}



	@SuppressWarnings("finally")
	public String removeFromCart(String cartItem)
	{

		JSONObject cItem = new JSONObject(cartItem);
		String userid= cItem.get("userid").toString();
		String trackid= cItem.get("trackid").toString();
		String tableName = "cart";
		JSONObject response = new JSONObject();
		int i = 0;
		try {
			stmt = conn.createStatement();

			String query = "delete from finalproject.cart where userid = "+userid+" and trackid='"+ trackid+ "';";
			i = stmt.executeUpdate(query);
			System.out.println("Number of rows deleted:"+ i);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
finally{
			
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		if(i<=0)
		{
			response.put("statusCode",STATUS_ERROR_CODE);
			response.put("statusMessage", STATUS_ERROR_MESSAGE);
			return response.toString();
		}	


		response.put("statusCode",STATUS_SUCCESS_CODE);
		response.put("statusMessage", STATUS_SUCCESS_MESSAGE);

		return response.toString();
}
	}



	public static void main(String[] args)
	{
		CartDao dao = new CartDao();

		dao.viewItemsInCart("user1");

	}


	public String addToCart(Track track) {

		JSONObject response = new JSONObject();
		try 
		{
			stmt = conn.createStatement();


			String query = "insert into finalproject.cart values("+track.getUserid()+",'"+track.getTrackid()+
					"','"+track.getAlbumid()+"','"+track.getArtistid()+"','"+track.getGenreid()+"');";
			stmt.executeUpdate(query);

		} catch (SQLException e) {

			e.printStackTrace();
			response.put("statusCode",STATUS_ERROR_CODE);
			response.put("statusMessage", STATUS_ERROR_MESSAGE+"\t Error in adding to cart");
			return response.toString();

		}finally{
			
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		response.put("statusCode",STATUS_SUCCESS_CODE);
		response.put("statusMessage", STATUS_SUCCESS_MESSAGE+ "Added to Cart");
		return response.toString();

	}
}
