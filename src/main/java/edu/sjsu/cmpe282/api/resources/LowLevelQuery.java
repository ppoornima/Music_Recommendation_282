package edu.sjsu.cmpe282.api.resources;

import java.text.SimpleDateFormat;
import java.util.Arrays;
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
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;

public class LowLevelQuery {

	static AmazonDynamoDBClient client = new AmazonDynamoDBClient(new ProfileCredentialsProvider());
	static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

	public static void main(String[] args) throws Exception {
		try {

			AWSCredentials credentials = new BasicAWSCredentials("AKIAI3QN42KZIHNSDIFA","do3c/rNdJnDW/rlCCSKzT5NvAyzKnw7qeCPvSSYL");

			client = new AmazonDynamoDBClient(credentials);
			Region usWest2 = Region.getRegion(Regions.US_WEST_1);
			client.setRegion(usWest2);


			// Get an item.
			//getProduct("1", "products");
			getProducts("100","products");
		}  
		catch (AmazonServiceException ase) {
			System.err.println(ase.getMessage());
		}  
	}

	private static void getProducts(String categoryID, String tableName)
	{
		HashMap<String, Condition> scanFilter = new HashMap<String, Condition>();

		Condition hashKeyCondition = new Condition().withComparisonOperator(
				ComparisonOperator.EQ.toString()).withAttributeValueList(new AttributeValue().withS(categoryID));

		scanFilter.put("categoryID", hashKeyCondition);
		ScanRequest scanRequest = new ScanRequest("products").withScanFilter(scanFilter);
		ScanResult scanResult = client.scan(scanRequest);


		JSONObject product = new JSONObject(); 
		//{}

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
		
		product.put("product", array);
		
		System.out.println("JSON OBJECT-----------------------");
		System.out.println(product.toString());
		
	}


	static void getProduct(String id, String tableName) {
		Map<String, AttributeValue> key = new HashMap<String, AttributeValue>();
		key.put("productID", new AttributeValue().withN(id));




		GetItemRequest getItemRequest = new GetItemRequest()
		.withTableName(tableName)
		.withKey(key)
		.withAttributesToGet(Arrays.asList("productID", "categorryID","productName", "productDescription","productPrice","quantity"));

		GetItemResult result = client.getItem(getItemRequest);

		// Check the response.
		System.out.println("Printing item after retrieving it....");
		printItem(result.getItem());            

	}

	private static void printItem(Map<String, AttributeValue> attributeList) {
		for (Map.Entry<String, AttributeValue> item : attributeList.entrySet()) {
			String attributeName = item.getKey();
			AttributeValue value = item.getValue();
			System.out.println(attributeName + " "
					+ (value.getS() == null ? "" : "S=[" + value.getS() + "]")
					+ (value.getN() == null ? "" : "N=[" + value.getN() + "]")
					+ (value.getB() == null ? "" : "B=[" + value.getB() + "] ")
					+ (value.getSS() == null ? "" : "SS=[" + value.getSS() + "]")
					+ (value.getNS() == null ? "" : "NS=[" + value.getNS() + "]")
					+ (value.getBS() == null ? "" : "BS=[" + value.getBS() + "] \n"));
		}
	}

}
