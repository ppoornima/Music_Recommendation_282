package edu.sjsu.cmpe282.dto;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodb.model.ConditionalCheckFailedException;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeAction;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.AttributeValueUpdate;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.ConditionalOperator;
import com.amazonaws.services.dynamodbv2.model.DeleteItemRequest;
import com.amazonaws.services.dynamodbv2.model.DeleteItemResult;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest;
import com.amazonaws.services.dynamodbv2.model.UpdateItemResult;
import com.fasterxml.jackson.databind.introspect.WithMember;

import edu.sjsu.cmpe282.domain.User;

public class UserDao {

	public static final int STATUS_SUCCESS_CODE = 200;
	public static final String STATUS_SUCCESS_MESSAGE = "success";
	public static final int STATUS_ERROR_CODE =500;
	public static final String STATUS_ERROR_MESSAGE="error";

	Connection conn = null;
	Statement stmt = null;
	static AmazonDynamoDBClient client = new AmazonDynamoDBClient(new ProfileCredentialsProvider());

	// Constructor with JDBC connection
	public UserDao()
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


	public String getCurrentDateTime()
	{		  
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		java.util.Date date = new java.util.Date();
		System.out.println("Current Date Time : " + dateFormat.format(date));
		return dateFormat.format(date);
	}

	public String addUser(User user)
	{
		JSONObject response = new JSONObject();
		try {

			System.out.println(user.getUserid()+"\t"+ user.getPasswd()+"\t");
			stmt = conn.createStatement();
			user.setLastlogin(getCurrentDateTime());
			String query = "INSERT INTO `finalproject`.`user` (`userid`, `pwd`,`lastlogin`) VALUES ('"+user.getUserid()  + "', '" + user.getPasswd() + "','"+user.getLastlogin()+"');";
			stmt.executeUpdate(query);
			response.put("statusCode",STATUS_SUCCESS_CODE);
			response.put("statusMessage", STATUS_SUCCESS_MESSAGE);
			response.put("userid", user.getUserid());
			//response.put("firstName",user.getFirstName());
			//response.put("lastLoginTime", lastLoginTime);			

		} catch (SQLException e) {

			response.put("statusCode",STATUS_ERROR_CODE);
			response.put("statusMessage", STATUS_ERROR_MESSAGE);
			return response.toString();

		}

		return response.toString();
	}

	public JSONObject updateTime(String userid)
	{
		JSONObject response = new JSONObject();
		try {
			stmt = conn.createStatement();

			String query = "update `finalproject`.`user` set lastlogin = '"+getCurrentDateTime()+"'where userid ='"+userid+"';";
			stmt.executeUpdate(query);

		} catch (SQLException e) {

			e.printStackTrace();
			response.put("statusCode",STATUS_ERROR_CODE);
			response.put("statusMessage", STATUS_ERROR_MESSAGE+"\t Error updating last logged in time!");
			return response;

		}

		response.put("statusCode",STATUS_SUCCESS_CODE);
		response.put("statusMessage", STATUS_SUCCESS_MESSAGE+"\t last logged in time updated");

		return response;
	}

	public String checkUser(User user){
		ResultSet rs;
		String origPasswd = null;
		String lastLoginTime = null ;
		JSONObject response = new JSONObject();

		try {
			stmt = conn.createStatement();
			String query = "Select * from finalproject.user where userid = '"+user.getUserid()+"';";
			rs = stmt.executeQuery(query);
			rs.next();
			origPasswd = rs.getString("pwd");
			lastLoginTime =  rs.getString("lastlogin");
			response = updateTime(user.getUserid());
			System.out.println("Password from db : "+ origPasswd );
			System.out.println("Password entered : "+user.getPasswd());

		} catch (SQLException e) {

			response.put("statusCode",STATUS_ERROR_CODE);
			response.put("statusMessage", STATUS_ERROR_MESSAGE);
			return response.toString();
		}
		if(user.getPasswd().equals(origPasswd))
		{

			response.put("statusCode",STATUS_SUCCESS_CODE);
			response.put("statusMessage", STATUS_SUCCESS_MESSAGE);
			response.put("userid", user.getUserid());
			//response.put("firstName",firstName);
			response.put("lastLoginTime", lastLoginTime);


		}
		else
		{

			response.put("statusCode",STATUS_ERROR_CODE);
			response.put("statusMessage", STATUS_ERROR_MESSAGE);
			return response.toString();

		}

		return response.toString();

	}











	public String getCatalog()
	{

		JSONObject response = new JSONObject();
		try{
			
			JSONArray catalog = new JSONArray();
			//catalog = getCatalogfromDynamoDB();

			response.put("statusCode",STATUS_SUCCESS_CODE);
			response.put("statusMessage", STATUS_SUCCESS_MESSAGE);
			response.put("catalog",catalog);

			System.out.println("response "+ response.toString());
		}
		catch (AmazonServiceException ase) {
			System.err.println(ase.getMessage());

			response.put("statusCode",STATUS_ERROR_CODE);
			response.put("statusMessage", STATUS_ERROR_MESSAGE);
			return response.toString();

		} 

		return response.toString();

	}









	public String addToCart(String email, String productID, String quantity)
	{
		

		String tableName = "cart";
		Map<String, AttributeValue> cart = new HashMap<String, AttributeValue>();

		String timestamp= getCurrentDateTime();

		PutItemRequest itemRequest = new PutItemRequest().withTableName(tableName).withItem(cart);
		cart.put("emailID", new AttributeValue().withS(email));
		cart.put("productID", new AttributeValue().withS(productID));
		cart.put("timestamp", new AttributeValue().withS(timestamp)); 
		cart.put("quantity", new AttributeValue().withN(quantity));

		client.putItem(itemRequest);
		cart.clear();
		System.out.println("Item inserted into DB - cart table");
		return "";


	}






	public String addOrderPayment(String userid){

		JSONObject response = new JSONObject();

		CartDao dao = new CartDao();
		
		String items = dao.viewItemsInCart(userid);
		JSONObject obj = new JSONObject(items);
		//JSONObject c = (JSONObject) obj.get("cartItems");
		JSONArray array = (JSONArray) obj.get("cartItems");

		int i;
		try {

			for (i = 0; i < array.length(); i++) {

				JSONObject item = array.getJSONObject(i);
				String trackid = item.get("trackid").toString().trim() ;	
				String albumid = item.get("albumid").toString().trim() ;	
				String artistid = item.get("artistid").toString().trim() ;	
				String genreid = item.get("genreid").toString().trim() ;	
				String dop = getCurrentDateTime();
				stmt = conn.createStatement();
				String query = "INSERT INTO `finalproject`.`orderhistory`  VALUES (" + userid + ", '" + trackid+ "', '" +albumid + "', '" + artistid+ "','"+ genreid+"','"+dop+"');";
				stmt.executeUpdate(query);

				
			}
			/*     Remove from cart         */
			String removeItemStatus = removeFromCartAfterOrder(userid);
			
			System.out.println("Remove Item Status "+removeItemStatus);

			response.put("statusCode",STATUS_SUCCESS_CODE);
			response.put("statusMessage", STATUS_SUCCESS_MESSAGE+ i+"order placed");

		} catch (SQLException e) {
			e.printStackTrace();
			response.put("statusCode",STATUS_ERROR_CODE);
			response.put("statusMessage", STATUS_ERROR_MESSAGE);
			return response.toString();
		}
		catch(NumberFormatException ex){
			ex.printStackTrace();
			response.put("statusCode",STATUS_ERROR_CODE);
			response.put("statusMessage", STATUS_ERROR_MESSAGE);
			return response.toString();

		}

		return response.toString();
	}



	public String removeFromCartAfterOrder(String userid)
	{
	
		JSONObject response = new JSONObject();
		int i = 0;
		try {
			stmt = conn.createStatement();
			
			String query = "delete from finalproject.cart where userid = "+userid+";";
			 i = stmt.executeUpdate(query);
			System.out.println("Number of rows deleted:"+ i);
					
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


	
	public String orderHistory(String userid)
	{
		JSONObject response = new JSONObject();
		ResultSet rs;

		try {
			stmt = conn.createStatement();
			String query = "Select * from finalproject.orderhistory where userid = "+userid+";";
			rs = stmt.executeQuery(query);
			JSONArray array = new JSONArray();

			while(rs.next()){
				JSONObject obj = new JSONObject();
				//System.out.println(rs.getString("orderID"));

				obj.put("trackid", rs.getString("trackid"));
				obj.put("albumid", rs.getString("albumid"));
				obj.put("artistid", rs.getString("artistid"));
				obj.put("ratings", rs.getString("ratings"));


				array.put(obj);
			}
			System.out.println("JSON ARRAY:"+ array.toString());

			response.put("orders", array);
			response.put("statusCode",STATUS_SUCCESS_CODE);
			response.put("statusMessage", STATUS_SUCCESS_MESSAGE);


		} catch (SQLException e) {

			e.printStackTrace();
			response.put("statusCode",STATUS_ERROR_CODE);
			response.put("statusMessage", STATUS_ERROR_MESSAGE);
			return response.toString();
		}




		return response.toString();



	}






	public static void main(String[] args)
	{
		//User u = new User();
		//u.setEmail("pooja@gmail.com");
		//u.setPasswd("1234");

			new UserDao().orderHistory("user1");

		//new UserDao().getProducts("100");
		// new UserDao().getCategory("1001");
		//	
		//	new UserDao().addToCart("poornima@gmail.com", "003","1");
		//	new UserDao().addToCart("pooja@gmail.com", "002", "2");
		// new UserDao().addToCart("poornima@gmail.com", "003","3");
		//new UserDao().getProductByID("001");
		// new UserDao().removeFromCart("pooja@gmail.com", "002");
		//	new UserDao().updateCart("pooja@gmail.com", "001", "3");
		//	new UserDao().updateCart("poornima@gmail.com", "002", "2");

		//new UserDao().updateCart("poorni@gmail.com", "003","1");

		//new UserDao().mainMenuLoad();
		//new UserDao().viewItemsInCart("pooja@gmail.com");
		//new UserDao().addOrderPayment("pooja@gmail.com");
		//	new UserDao().orderHistory("pooja@gmail.com");


	}

}
