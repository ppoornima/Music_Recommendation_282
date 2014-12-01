package edu.sjsu.cmpe282.dto;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class RecommendDao
{

	public static final int STATUS_SUCCESS_CODE = 200;
	public static final String STATUS_SUCCESS_MESSAGE = "success";
	public static final int STATUS_ERROR_CODE =500;
	public static final String STATUS_ERROR_MESSAGE="error";

	Connection conn = null, conn_item=null,conn_item1=null;
	Statement stmt = null;

	// Constructor with JDBC connection
	public  RecommendDao() {
		// TODO Auto-generated constructor stub

		try{
			try {
				Class.forName("com.mysql.jdbc.Driver");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			conn = DriverManager.getConnection("jdbc:mysql://recommendation.chtut8njmfxl.us-west-1.rds.amazonaws.com:3306/","root","rootroot"); 
			conn_item = DriverManager.getConnection("jdbc:mysql://projectinstance.chtut8njmfxl.us-west-1.rds.amazonaws.com:3306/","root","rootroot"); 
		//	conn_item1 = DriverManager.getConnection("jdbc:mysql://projectinstance.chtut8njmfxl.us-west-1.rds.amazonaws.com:3306/","root","rootroot"); 

			System.out.println("COnn:"+conn);
		}
		catch (SQLException e) {
			e.printStackTrace();

		}


	}

	public String getUserRecommendation(String userid)  
	{
		// TODO Auto-generated method stub

		if(userid.matches("[A-Za-z]+"))
		{
			// new user


		}

		ResultSet rs;

		JSONObject response = new JSONObject();

		try {
			stmt = conn.createStatement();
			String query = "Select * from  `mydatabase`.`User_Reco` where userid = "+userid+";";
			rs = stmt.executeQuery(query);
			JSONArray array = new JSONArray();

			while(rs.next()){
				JSONObject obj = new JSONObject();
				//System.out.println(rs.getString("orderID"));

				obj.put("userid", rs.getString("userid"));
				obj.put("recoitemid", rs.getString("recoitemid"));
				obj.put("score", rs.getString("score"));
				array.put(obj);
			}
			System.out.println("JSON ARRAY:"+ array.toString());

			response.put("user_reco", array);
			response.put("statusCode",STATUS_SUCCESS_CODE);
			response.put("statusMessage", STATUS_SUCCESS_MESSAGE);

		} catch (SQLException e) {

			response.put("statusCode",STATUS_ERROR_CODE);
			response.put("statusMessage", STATUS_ERROR_MESSAGE);
			return response.toString();
		}finally
		{
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}


		return response.toString();
	}


	public String getItemRecommendation(String userid) 
	{
		// TODO Auto-generated method stub
		ResultSet rs;

		JSONObject response = new JSONObject();

		try {

			stmt = conn_item.createStatement();
			String query2 = "select trackid from finalproject.orderhistory where userid="+userid+"  order by purchasedate desc LIMIT 1;";
			rs=stmt.executeQuery(query2);
			String itemid = null;
			while(rs.next())
			{
				itemid = rs.getString("trackid");
				System.out.println("Last purchased track = "+ itemid);
			}
			if(itemid!=null)
			{

				stmt = conn.createStatement();
				String query = "Select * from  `mydatabase`.`Item_Reco` where itemid = '"+itemid+"';";
				rs = stmt.executeQuery(query);
				JSONArray array = new JSONArray();

				while(rs.next()){
					JSONObject obj = new JSONObject();

					obj.put("itemid", rs.getString("itemid"));
					obj.put("recoitemid", rs.getString("recoitemid"));
					obj.put("score", rs.getString("score"));
					array.put(obj);
				}
				System.out.println("Item Recmd JSON ARRAY:"+ array.toString());

				response.put("item_reco", array);
				response.put("statusCode",STATUS_SUCCESS_CODE);
				response.put("statusMessage", STATUS_SUCCESS_MESSAGE);
			}


			else
			{

				response.put("statusCode",STATUS_ERROR_CODE);
				response.put("statusMessage", STATUS_ERROR_MESSAGE+"No Records found");
				return response.toString();
			}
		} catch (SQLException e  ) {

			response.put("statusCode",STATUS_ERROR_CODE);
			response.put("statusMessage", STATUS_ERROR_MESSAGE);
			return response.toString();
		}finally
		{
			
			try {
				conn_item.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}


		return response.toString();
	}




	public String getNewUserRecommendation(String userid) {
		String output= null;
		JSONObject response1 = new JSONObject();	
		JSONObject obj = new JSONObject();
	JSONArray array = new JSONArray();
		ResultSet rs;
			try {
				stmt = conn_item.createStatement();
				
				String query2 = "select trackid,ratings from finalproject.orderhistory where userid="+userid+";";
				rs=stmt.executeQuery(query2);
				int count =0;
				while(rs.next())
				{
					JSONObject item = new JSONObject();
					item.put("rating", rs.getString("ratings"));				
					item.put("itemId", rs.getString("trackid"));
					count++;
					//System.out.println("Last purchased track = "+ t.getTrackid()+"\t   ratings"+ t.getRatings());
				array.put(item);
				}
			obj.put("ratingList", array);
			
			System.out.println("JSON FORMAT NEWUSER "+ obj.toString());
if(count!=0)
{
			Client client = Client.create();

	        WebResource webResource = client.resource("http://54.67.5.81:8080/UserBasedRecommendation/rest/userbased/newuserrecommend");

	     // POST method
	        ClientResponse response = webResource.accept("application/json")
	                .type("application/json").post(ClientResponse.class, obj.toString());
			if (response.getStatus() != 200) {

				throw new RuntimeException("Failed : HTTP error code : "

			                        + response.getStatus());

			}
		 output = response.getEntity(String.class);
			System.out.println("Server response .... \n");	
			System.out.println(output);
			} 
			}catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				response1.put("statusCode",STATUS_ERROR_CODE);
				response1.put("statusMessage", STATUS_ERROR_MESSAGE+"No Records found");
				return response1.toString();
			}finally{
				try {
					conn_item.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					response1.put("statusCode",STATUS_ERROR_CODE);
					response1.put("statusMessage", STATUS_ERROR_MESSAGE+"No Records found");
					return response1.toString();
				}
				
			}
		
			response1.put("newUser", output);
			response1.put("statusCode",STATUS_SUCCESS_CODE);
			response1.put("statusMessage", STATUS_SUCCESS_MESSAGE);
			return response1.toString();

	}

	

	public static void main(String[] args)
	{

		RecommendDao dao = new RecommendDao();
		//dao.getUserRecommendation("10");

		//dao.getItemRecommendation("10");

dao.getNewUserRecommendation("abc");

	}

}
