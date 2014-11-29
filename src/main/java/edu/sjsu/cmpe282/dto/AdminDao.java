package edu.sjsu.cmpe282.dto;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;


public class AdminDao {
	
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
	public String addCatalog(String catalogDetails)
	{
		JSONObject catalogs = new JSONObject(catalogDetails);

		String catalogName= catalogs.get("catalogName").toString();
		String catalogID = catalogs.get("catalogID").toString();

		JSONObject response = new JSONObject();
		awsAuthentication();
		String tableName = "catalog" ;
		Map<String, AttributeValue> catalog = new HashMap<String, AttributeValue>();
		PutItemRequest catalogRequest = new PutItemRequest().withTableName(tableName).withItem(catalog);

		catalog.put("catalogID", new AttributeValue().withS(catalogID));
		catalog.put("catalogName", new AttributeValue().withS(catalogName));
		catalogRequest = new PutItemRequest().withTableName("catalog").withItem(catalog);
		client.putItem(catalogRequest);


		response.put("statusCode",STATUS_SUCCESS_CODE);
		response.put("statusMessage", STATUS_SUCCESS_MESSAGE);
		return response.toString();




	}

	
	
	public String addProduct(String productDeatils)
	{

		JSONObject product = new JSONObject(productDeatils);
		JSONObject response = new JSONObject();
		awsAuthentication();
		String tableName = "products";

		Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();

		PutItemRequest itemRequest = new PutItemRequest().withTableName(tableName).withItem(item);

		item.put("productID", new AttributeValue().withN( product.get("productID").toString()));
		item.put("productName", new AttributeValue().withS(product.get("productName").toString()));
		item.put("productDescription", new AttributeValue().withS(product.get("productDescription").toString()));
		item.put("productPrice", new AttributeValue().withN(product.get("productPrice").toString()));
		item.put("quantity", new AttributeValue().withN(product.get("quantity").toString()));
		item.put("categoryID", new AttributeValue().withS(product.get("categoryID").toString()));

		client.putItem(itemRequest);


		response.put("statusCode",STATUS_SUCCESS_CODE);
		response.put("statusMessage", STATUS_SUCCESS_MESSAGE);
		return response.toString();

	}
}
