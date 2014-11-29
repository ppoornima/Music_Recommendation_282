package edu.sjsu.cmpe282.dto;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeAction;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.AttributeValueUpdate;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.DeleteItemRequest;
import com.amazonaws.services.dynamodbv2.model.DeleteItemResult;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest;
import com.amazonaws.services.dynamodbv2.model.UpdateItemResult;

public class CartDao {
	public static final int STATUS_SUCCESS_CODE = 200;
	public static final String STATUS_SUCCESS_MESSAGE = "success";
	public static final int STATUS_ERROR_CODE =500;
	public static final String STATUS_ERROR_MESSAGE="error";

	
	static AmazonDynamoDBClient client = new AmazonDynamoDBClient(new ProfileCredentialsProvider());
	public void awsAuthentication()
	{

		AWSCredentials credentials = new BasicAWSCredentials("AKIAI3QN42KZIHNSDIFA","do3c/rNdJnDW/rlCCSKzT5NvAyzKnw7qeCPvSSYL");

		client = new AmazonDynamoDBClient(credentials);
		Region usWest2 = Region.getRegion(Regions.US_WEST_1);
		client.setRegion(usWest2);

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
	public String getCurrentDateTime()
	{		  
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		java.util.Date date = new java.util.Date();
		System.out.println("Current Date Time : " + dateFormat.format(date));
		return dateFormat.format(date);
	}
	
	public String updateCart(String cartDetails)
	{

		System.out.println("CART DETAILS : "+ cartDetails);
		JSONObject cart =  new JSONObject(  cartDetails);

		int emailIDLength = cart.get("emailID").toString().length();
		String emailID = cart.get("emailID").toString().substring(1,emailIDLength-1);
		String productID =  cart.get("productID").toString();
		String quantity=cart.get("productQuantity").toString();
		String productName = cart.get("productName").toString() ;
		String productPrice = cart.get("productPrice").toString();


		JSONObject response = new JSONObject();

		awsAuthentication();
		String tableName = "cart";
		Map<String, AttributeValueUpdate> updateItems = new HashMap<String, AttributeValueUpdate>();
		String timestamp= getCurrentDateTime();



		Map<String, AttributeValue> attributeList = new HashMap<String, AttributeValue>();
		attributeList.put("emailID", new AttributeValue().withS(emailID));
		attributeList.put("productID", new AttributeValue().withS(productID));



		updateItems.put("quantity", 
				new AttributeValueUpdate()
		.withAction(AttributeAction.ADD)
		.withValue(new AttributeValue().withN("+"+quantity)));

		updateItems.put("productName", new AttributeValueUpdate().withAction(AttributeAction.PUT)
				.withValue(new AttributeValue().withS(productName)));

		updateItems.put("timestamp", new AttributeValueUpdate().withAction(AttributeAction.PUT)
				.withValue(new AttributeValue().withS(timestamp)));
		updateItems.put("productPrice", new AttributeValueUpdate().withAction(AttributeAction.PUT)
				.withValue(new AttributeValue().withS(productPrice)));


		UpdateItemRequest updateItemRequest = new UpdateItemRequest()
		.withTableName(tableName)
		.withKey(attributeList)
		.withAttributeUpdates(updateItems);



		UpdateItemResult result = client.updateItem(updateItemRequest);
		response.put("statusCode",STATUS_SUCCESS_CODE);
		response.put("statusMessage", STATUS_SUCCESS_MESSAGE);
		return response.toString();

	}

	public String removeFromCart(String cartItem)
	{
		awsAuthentication();

		JSONObject cItem = new JSONObject(cartItem);
		String emailID= cItem.get("emailID").toString();
		String productID= cItem.get("productID").toString();
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
		response.put("statusMessage", STATUS_SUCCESS_MESSAGE);

		return response.toString();
	}

}
