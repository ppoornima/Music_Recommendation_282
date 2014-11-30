package edu.sjsu.cmpe282.dto;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.JSONArray;
import org.json.JSONObject;


public class ProductsDao {



	public static final int STATUS_SUCCESS_CODE = 200;
	public static final String STATUS_SUCCESS_MESSAGE = "success";
	public static final int STATUS_ERROR_CODE =500;
	public static final String STATUS_ERROR_MESSAGE="error";


	Connection conn = null;
	Statement stmt = null;

	
	public ProductsDao()
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

	public String getTracks(String artistID)
	{
		ResultSet rs;
		JSONObject response = new JSONObject();
		try 
		{
			stmt = conn.createStatement();

			String query = "select * from finalproject.tracks where artistid = '"+artistID+"';";
			rs = stmt.executeQuery(query);
			JSONArray array = new JSONArray();

			while(rs.next())
			{
				JSONObject obj = new JSONObject();
				obj.put("trackid", rs.getString("trackid"));
				obj.put("albumid", rs.getString("albumid"));
				obj.put("genreid", rs.getString("genreid"));

				array.put(obj);

			}
			System.out.println("TRACKS ARRAY:"+ array.toString());

			response.put("tracks", array);
			response.put("statusCode",STATUS_SUCCESS_CODE);
			response.put("statusMessage", STATUS_SUCCESS_MESSAGE);


		} catch (SQLException e) {

			e.printStackTrace();
			response.put("statusCode",STATUS_ERROR_CODE);
			response.put("statusMessage", STATUS_ERROR_MESSAGE+"\t Error updating last logged in time!");
			return response.toString();

		}



		return response.toString();




	}

	public static void main(String[] args)
	{

		//new ProductsDao().getProducts("100");

	}
}
