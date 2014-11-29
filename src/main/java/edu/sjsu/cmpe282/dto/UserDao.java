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

			conn = DriverManager.getConnection("jdbc:mysql://cloudservices.cvz5dtczqgms.us-west-1.rds.amazonaws.com:3306/user","root","cloudservices"); 
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

			System.out.println(user.getEmail()+"\t"+user.getFirstName()+"\t"+user.getLastName()+"\t"+ user.getPasswd()+"\t");
			stmt = conn.createStatement();
			user.setLastLoginTime(getCurrentDateTime());
			String query = "INSERT INTO `cloudservices`.`user` (`email`, `firstname`, `lastname`, `password`,`lastloggedintime`) VALUES ('" + user.getEmail() + "', '" + user.getFirstName() + "', '" +user.getLastName()  + "', '" + user.getPasswd() + "','"+user.getLastLoginTime()+"');";
			stmt.executeUpdate(query);
			response.put("statusCode",STATUS_SUCCESS_CODE);
			response.put("statusMessage", STATUS_SUCCESS_MESSAGE);
			response.put("email", user.getEmail());
			response.put("firstName",user.getFirstName());
			//response.put("lastLoginTime", lastLoginTime);			

		} catch (SQLException e) {

			response.put("statusCode",STATUS_ERROR_CODE);
			response.put("statusMessage", STATUS_ERROR_MESSAGE);
			return response.toString();

		}

		return response.toString();
	}

	public JSONObject updateTime(String email)
	{
		JSONObject response = new JSONObject();
		try {
			stmt = conn.createStatement();

			String query = "update `cloudservices`.`user` set lastloggedintime = '"+getCurrentDateTime()+"'where email ='"+email+"';";
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
		String firstName = null;
		String lastLoginTime = null ;
		JSONObject response = new JSONObject();

		try {
			stmt = conn.createStatement();
			String query = "Select * from cloudservices.user where email = '"+user.getEmail()+"';";
			rs = stmt.executeQuery(query);
			rs.next();
			origPasswd = rs.getString("password");
			firstName =  rs.getString("firstName");
			lastLoginTime =  rs.getString("lastloggedintime");
			response = updateTime(user.getEmail());
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
			response.put("email", user.getEmail());
			response.put("firstName",firstName);
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

	public String getProductByID(String productID)
	{
		awsAuthentication();

		JSONObject response = new JSONObject();
		JSONObject product = getProductByIDFromDynamoDB(productID);

		response.put("statusCode",STATUS_SUCCESS_CODE);
		response.put("statusMessage", STATUS_SUCCESS_MESSAGE);
		response.put("catalog",product);
		System.out.println("RESPONSE"+ response.toString());

		return response.toString();

	}

	public JSONObject getProductByIDFromDynamoDB(String productID)
	{

		//	JSONObject product = new JSONObject();


		String tableName = "products";
		HashMap<String, Condition> filter = new HashMap<String, Condition>();

		Condition hashKeyCondition = new Condition().withComparisonOperator(
				ComparisonOperator.EQ.toString()).withAttributeValueList(new AttributeValue().withN(productID));

		filter.put("productID", hashKeyCondition);

		QueryRequest queryRequest = new QueryRequest().withTableName(tableName).withKeyConditions(filter);

		QueryResult result = client.query(queryRequest);
		System.out.println("Query Result:" + result);

		JSONObject p = new JSONObject();

		List<Map<String, AttributeValue>> items = result.getItems();
		for (Map<?, ?> item : items) {
			Set s = item.keySet();  
			Iterator i  = s.iterator(); 

			while(i.hasNext()) {

				String key =  (String) i.next();
				String value = item.get(key).toString();
				String actualValue = (value.substring(3,(value.length())-2));
				p.put(key, actualValue);
			}

		}


		return p;


	}





	public void awsAuthentication()
	{

		AWSCredentials credentials = new BasicAWSCredentials("AKIAI3QN42KZIHNSDIFA","do3c/rNdJnDW/rlCCSKzT5NvAyzKnw7qeCPvSSYL");

		client = new AmazonDynamoDBClient(credentials);
		Region usWest2 = Region.getRegion(Regions.US_WEST_1);
		client.setRegion(usWest2);

	}




	public String getCatalog()
	{

		JSONObject response = new JSONObject();
		try{
			awsAuthentication();
			JSONArray catalog = new JSONArray();
			catalog = getCatalogfromDynamoDB();

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

	public JSONArray getCatalogfromDynamoDB()
	{
		String tableName = "catalog";

		ScanRequest scanRequest = new ScanRequest(tableName);
		ScanResult scanResult = client.scan(scanRequest);


		List<Map<String, AttributeValue>> items = scanResult.getItems();
		int n=1;
		JSONArray array = new JSONArray();
		for (Map<?, ?> item : items) {
			Set s = item.keySet();  
			Iterator i  = s.iterator(); 
			JSONObject p = new JSONObject();

			while(i.hasNext()) {

				String key =  (String) i.next();
				String value = item.get(key).toString();
				String actualValue = (value.substring(3,(value.length())-2));
				p.put(key, actualValue);
			}
			array.put(p);
		}
		return array;

	}


	public String getCategory(String catalogID)
	{

		JSONObject response = new JSONObject();

		try{
			awsAuthentication();
			JSONArray product = new JSONArray();
			product = getCategoryfromDynamoDB(catalogID);

			response.put("statusCode",STATUS_SUCCESS_CODE);
			response.put("statusMessage", STATUS_SUCCESS_MESSAGE);
			response.put("category",product);

			System.out.println("response "+ response.toString());
		} catch (AmazonServiceException ase) {
			System.err.println(ase.getMessage());

			response.put("statusCode",STATUS_ERROR_CODE);
			response.put("statusMessage", STATUS_ERROR_MESSAGE + ase.getMessage() );
			//System.out.println("RESPONSE--ERROR"+ response.toString());
			return response.toString();

		} 

		return response.toString();

	}

	private JSONArray getCategoryfromDynamoDB(String catalogID) {
		String tableName = "category";

		HashMap<String, Condition> scanFilter = new HashMap<String, Condition>();

		Condition hashKeyCondition = new Condition().withComparisonOperator(
				ComparisonOperator.EQ.toString()).withAttributeValueList(new AttributeValue().withS(catalogID));

		scanFilter.put("catalogID", hashKeyCondition);
		ScanRequest scanRequest = new ScanRequest(tableName).withScanFilter(scanFilter);
		ScanResult scanResult = client.scan(scanRequest);

		List<Map<String, AttributeValue>> items = scanResult.getItems();
		JSONArray array = new JSONArray();
		for (Map<?, ?> item : items) {
			Set s = item.keySet();  
			Iterator i  = s.iterator(); 
			JSONObject p = new JSONObject();

			while(i.hasNext()) {

				String key =  (String) i.next();
				String value = item.get(key).toString();
				String actualValue = (value.substring(3,(value.length())-2));
				p.put(key, actualValue);
			}

			array.put(p);
		}
		return array;

	}



	public String addToCart(String email, String productID, String quantity)
	{
		awsAuthentication();

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



	public String viewItemsInCart (String emailID)
	{
		JSONObject response = new JSONObject();

		awsAuthentication();


		// can do it in this method also......
		/*		 HashMap<String, Condition> filter = new HashMap<String, Condition>();

		 Condition hashKeyCondition = new Condition().withComparisonOperator(
		 ComparisonOperator.EQ.toString()).withAttributeValueList(new AttributeValue().withS(emailID));

		 filter.put("emailID", hashKeyCondition);

		 QueryRequest queryRequest = new QueryRequest().withTableName("cart").withKeyConditions(filter);

		 QueryResult result = client.query(queryRequest);
		 System.out.println("Query Result:" + result);*/

		System.out.println("---------------------------------------------");


		HashMap<String, Condition> scanFilter = new HashMap<String, Condition>();



		Condition hashKeyCondition1 = new Condition().withComparisonOperator(
				ComparisonOperator.EQ.toString()).withAttributeValueList(new AttributeValue().withS(emailID));

		scanFilter.put("emailID",hashKeyCondition1 );
		ScanRequest scanRequest = new ScanRequest("cart").withScanFilter(scanFilter);
		ScanResult scanResult = client.scan(scanRequest);
		List<Map<String, AttributeValue>> items = scanResult.getItems();
		JSONObject cartItems = new JSONObject();
		JSONArray array = new JSONArray();

		for (Map<?, ?> item : items) {
			Set s = item.keySet();  
			Iterator i  = s.iterator(); 
			JSONObject p = new JSONObject();

			while(i.hasNext()) {

				String key =  (String) i.next();
				String value = item.get(key).toString();
				String actualValue = (value.substring(3,(value.length())-2));
				p.put(key, actualValue);
			}
			array.put(p);
			//System.out.println(p);
		}
		cartItems.put("items", array);
		//	System.out.println(cartItems.toString());
		response.put("statusCode",STATUS_SUCCESS_CODE);
		response.put("statusMessage", STATUS_SUCCESS_MESSAGE);
		response.put("cartItems", cartItems);
		System.out.println(response.toString());
		return response.toString();

	}



	public String mainMenuLoad()
	{
		JSONObject response = new JSONObject();

		awsAuthentication();
		/*		
		String tableName_CATALOG = "catalog";

		JSONObject catalogName = new JSONObject();

		ScanRequest scanRequest = new ScanRequest(tableName_CATALOG);
		ScanResult scanResult = client.scan(scanRequest);


		List<Map<String, AttributeValue>> items = scanResult.getItems();
		int n=1;
		JSONArray array = new JSONArray();
		for (Map<?, ?> item : items) {

String cName = item.get("catalogName").toString();
String actualcName = (cName.substring(3,(cName.length())-2));
System.out.println(actualcName);
String cID = item.get("catalogID").toString();
String actualcID= (cID.substring(3,(cID.length())-2));
System.out.println(actualcID);
System.out.println(getCategoryfromDynamoDB(actualcID.toString()));
System.out.println("------------------------");
		}*/


		System.out.println(getCategoryfromDynamoDB("1000"));

		response.put("statusCode",STATUS_SUCCESS_CODE);
		response.put("statusMessage", STATUS_SUCCESS_MESSAGE);

		return response.toString();	

	}

	public String addOrderPayment(String emailID){

		JSONObject response = new JSONObject();

		String items = viewItemsInCart(emailID);
		JSONObject obj = new JSONObject(items);
		JSONObject c = (JSONObject) obj.get("cartItems");
		JSONArray array = (JSONArray) c.get("items");

		int i;
		try {

			for (i = 0; i < array.length(); i++) {

				JSONObject item = array.getJSONObject(i);
				String productID = item.get("productID").toString().trim() ;	
				int quantity = Integer.parseInt(item.get("quantity").toString().trim()) ;
				int price = Integer.parseInt(item.get("productPrice").toString().trim());
				int totalPrice = price*quantity;
				String productName =item.get("productName").toString();

				String dop = getCurrentDateTime();
				stmt = conn.createStatement();
				String query = "INSERT INTO `cloudservices`.`orderPayments` (`emailID`, `productID`, `productName`, `productQuantity`,`productPrice`,`DOP`) VALUES ('" + emailID + "', '" + productID+ "', '" +productName + "', '" + quantity+ "','"+ totalPrice+"','"+dop+"');";
				stmt.executeUpdate(query);

				/*     Remove from cart         */
				String removeItemStatus = removeFromCartAfterOrder(emailID, productID);
				
				/* Deducting the product quantity from products table */
				String  deductStatus=  deductProductQuantity(productID,quantity);
				
				System.out.println("Remove Item Status "+removeItemStatus);
				System.out.println("deduct Quantity Status"+ deductStatus);
			}

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



	public String removeFromCartAfterOrder(String emailID, String productID)
	{
		awsAuthentication();

		String tableName = "cart";
		JSONObject response = new JSONObject();

		Map<String, AttributeValue> attributeList = new HashMap<String, AttributeValue>();
		attributeList.put("emailID", new AttributeValue().withS(emailID));
		attributeList.put("productID", new AttributeValue().withS(productID));

		for (Map.Entry<String, AttributeValue> item : attributeList.entrySet()) {
			String attributeName = item.getKey();
			AttributeValue value = item.getValue();
			System.out.println("AttributeName"+ attributeName+"\t value "+value);
		}


		DeleteItemRequest deleteItemRequest = new DeleteItemRequest()
		.withTableName(tableName).withKey(attributeList);

		DeleteItemResult deleteItemResult = client.deleteItem(deleteItemRequest);
		System.out.println("DELETE ITEM RESULT"+ deleteItemResult);


		response.put("statusCode",STATUS_SUCCESS_CODE);
		response.put("statusMessage", STATUS_SUCCESS_MESSAGE + "Product removed from cart");

		return response.toString();
	}


	public String deductProductQuantity(String productID, int quantity)
	{

		JSONObject response = new JSONObject();

		awsAuthentication();
		String tableName = "products";
		Map<String, AttributeValueUpdate> updateItems = new HashMap<String, AttributeValueUpdate>();

		JSONObject item=  getProductByIDFromDynamoDB(productID);
		HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
		key.put("productID", new AttributeValue().withN(productID.trim()));

		updateItems.put("quantity", 
				new AttributeValueUpdate()
		.withAction(AttributeAction.ADD)
		.withValue(new AttributeValue().withN("-"+quantity)));

		UpdateItemRequest updateItemRequest = new UpdateItemRequest()
		.withTableName(tableName)
		.withKey(key)
		.withAttributeUpdates(updateItems);

		UpdateItemResult result = client.updateItem(updateItemRequest);

		response.put("statusCode",STATUS_SUCCESS_CODE);
		response.put("statusMessage", STATUS_SUCCESS_MESSAGE);
		System.out.println(response.toString());
		return response.toString();

	}

	public String orderHistory(String emailID)
	{
		JSONObject response = new JSONObject();
		ResultSet rs;

		try {
			stmt = conn.createStatement();
			String query = "Select * from cloudservices.orderPayments where emailID = '"+emailID+"';";
			rs = stmt.executeQuery(query);
			JSONArray array = new JSONArray();

			while(rs.next()){
				JSONObject obj = new JSONObject();
				System.out.println(rs.getString("orderID"));

				obj.put("orderID", rs.getString("orderID"));
				obj.put("productID", rs.getString("productID"));
				obj.put("productName", rs.getString("productName"));
				obj.put("productQuantity", rs.getInt("productQuantity"));
				obj.put("productPrice", rs.getInt("productPrice"));
				obj.put("dop", rs.getString("dop"));


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
		User u = new User();
		u.setEmail("pooja@gmail.com");
		u.setPasswd("1234");

		//	new UserDao().checkUser(u);

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

		new UserDao().deductProductQuantity("3", 3);

	}

}
