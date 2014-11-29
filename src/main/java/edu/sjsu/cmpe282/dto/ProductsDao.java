package edu.sjsu.cmpe282.dto;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;


public class ProductsDao {
	
	
	
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
	
	public String getProducts(String categoryID)
	{

		JSONObject response = new JSONObject();
		try
		{
			awsAuthentication();
			JSONArray product = new JSONArray();
			product = getProductsfromDynamoDB(categoryID);

			response.put("statusCode",STATUS_SUCCESS_CODE);
			response.put("statusMessage", STATUS_SUCCESS_MESSAGE);
			response.put("dataProducts",product);
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

	
	public JSONArray getProductsfromDynamoDB(String categoryID)
	{
		String tableName = "products";

		HashMap<String, Condition> scanFilter = new HashMap<String, Condition>();

		Condition hashKeyCondition = new Condition().withComparisonOperator(
				ComparisonOperator.EQ.toString()).withAttributeValueList(new AttributeValue().withS(categoryID));

		scanFilter.put("categoryID", hashKeyCondition);
		ScanRequest scanRequest = new ScanRequest(tableName).withScanFilter(scanFilter);
		ScanResult scanResult = client.scan(scanRequest);



		List<Map<String, AttributeValue>> items = scanResult.getItems();
		JSONArray array = new JSONArray(); int k= 1;
		for (Map<?, ?> item : items) {
			Set s = item.keySet();  
			Iterator i  = s.iterator(); 
			JSONObject p = new JSONObject();

			while(i.hasNext()) {

				String key =  (String) i.next();
				String value = item.get(key).toString();
				String actualValue = (value.substring(3,(value.length())-2));
				p.put(key, actualValue.trim());
			}
			array.put(p);
			k++;
	System.out.println("ITEM:"+ p.toString());
		}
		System.out.println("Number of items:"+ k);
		System.out.println(array.toString());
		return array;

	}
	
	public static void main(String[] args)
	{
		
		new ProductsDao().getProducts("100");
		
	}
}
