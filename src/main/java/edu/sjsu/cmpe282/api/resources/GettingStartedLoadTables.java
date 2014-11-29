package edu.sjsu.cmpe282.api.resources;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;

public class GettingStartedLoadTables {
	static AmazonDynamoDBClient client;
    static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    static String productCatalogTableName = "products";
   
    
    public static void main(String[] args) throws Exception {
        createClient();

        try {

            uploadSampleProducts(productCatalogTableName);
            

        } catch (AmazonServiceException ase) {
            System.err.println("Data load script failed.");
        }
    }
    
    private static void createClient() throws Exception {
//        AWSCredentials credentials = new PropertiesCredentials(
//                GettingStartedLoadTables.class.getResourceAsStream("AwsCredentials.properties"));
    	// client = new AmazonDynamoDBClient(credentials);
    	
        AWSCredentials credentials = new BasicAWSCredentials("AKIAI3QN42KZIHNSDIFA","do3c/rNdJnDW/rlCCSKzT5NvAyzKnw7qeCPvSSYL");

        client = new AmazonDynamoDBClient(credentials);
        client.setRegion(Region.getRegion(Regions.US_WEST_1));      
        
       
    }

    private static void uploadSampleProducts(String tableName) {
        
        try {
            // Add books.
            Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();

            PutItemRequest itemRequest = new PutItemRequest().withTableName(tableName).withItem(item);
            
            /*item.put("productID", new AttributeValue().withN("001"));
            item.put("productName", new AttributeValue().withS("Acer V5-471P-6615 14 inch Touchscreen Laptop PC with Intel Core i5-3337U Processor, 4GB Memory, 500GB Hard Drive and Windows 8"));
            item.put("productDescription", new AttributeValue().withS("Finally, there's a light and lean computing machine with touch:"
            		+ " the V5-471P-6615 Acer Touchscreen Laptop. At only 1" 
            		+"slim and super light, this laptop packs potent power and graphics, while integrating a touchscreen for exciting hands-on navigation. Open and close programs, browse online and breeze through photo albums"
            		+ " - using nothing but your fingers with the V5-471P-6615 Acer Touchscreen Laptop.1"));
            item.put("productPrice", new AttributeValue().withN("500"));
            item.put("quantity", new AttributeValue().withN("10"));
            item.put("categoryID", new AttributeValue().withS("100"));
            
            client.putItem(itemRequest);
            item.clear();
            
            item.put("productID", new AttributeValue().withN("002"));
            item.put("productName", new AttributeValue().withS("HP Refurbished Sparkling Black 15.6 inch Pavilion TouchSmart 15-b129wm Laptop PC with AMD A6-4455M Accelerated Processor, 4GB Memory, Touchscreen, 500GB Hard Drive and Windows 8"));
            item.put("productDescription", new AttributeValue().withS("HP believes in touch technology for everyone. That's why they took a great everyday laptop and gave it a touchscreen, which lets you make the most of Windows 8. With the refurbished Sparkling Black 15.6 HP Pavilion TouchSmart Laptop PC, 15-b129wm, you can scroll effortlessly through social networks, share photos with a few simple taps or start a Skype chat with a touch, all on a thin and light design you'll want with you all the time."));
            item.put("productPrice", new AttributeValue().withN("363"));
            item.put("quantity", new AttributeValue().withN("5"));
            item.put("categoryID", new AttributeValue().withS("100"));
            client.putItem(itemRequest);
            item.clear();
        	
            item.put("productID", new AttributeValue().withN("003"));
            item.put("productName", new AttributeValue().withS("Dell Sleekbook Carbon Fiber 12.5 inch XPS XPSU12-4671CRBFB Laptop PC with Intel Core i5-4200U Processor, 4GB Memory, Touchscreen, 128GB SSD and Windows 8.1 with Voice Assistant*"));
            item.put("productDescription", new AttributeValue().withS("Dell Sleekbook Carbon Fiber 12.5inch XPS XPSU12-4671CRBFB Laptop PC with Intel Core i5-4200U Processor, 4GB Memory, Touchscreen, 128GB SSD and Windows 8.1 with Voice Assistant*"));
            item.put("productPrice", new AttributeValue().withN("910"));
            item.put("quantity", new AttributeValue().withN("15"));
            item.put("categoryID", new AttributeValue().withS("100"));
            client.putItem(itemRequest);
            item.clear();*/
            /*
            item.put("productID", new AttributeValue().withN("004"));
            item.put("productName", new AttributeValue().withS("Dell Pre-Owned, Refurbished GX755 Small Form Factor Desktop PC with Intel Core 2 Duo Processor, 4GB Memory, 750GB Hard Drive and Windows 7 Professional (Monitor Not Included)"));
            item.put("productDescription", new AttributeValue().withS("The Dell Refurbished GX755 Small Form Factor Desktop PC has an Intel processor for the computing power you need. A 750GB hard drive gives you tons of room to store all of your multimedia files. A DVD-RW lets you watch movies, burn backup discs and more."));
            item.put("productPrice", new AttributeValue().withN("210"));
            item.put("quantity", new AttributeValue().withN("3"));
            item.put("categoryID", new AttributeValue().withS("101"));
            client.putItem(itemRequest);
            item.clear();*/
        	
        	
        	
        	Map<String, AttributeValue> category = new HashMap<String, AttributeValue>();
            PutItemRequest categoryRequest = new PutItemRequest().withTableName(tableName).withItem(category);
            
            category.put("categoryID", new AttributeValue().withS("100"));
            category.put("categoryName", new AttributeValue().withS("Laptops"));
            category.put("catalogID", new AttributeValue().withS("1000"));
            categoryRequest = new PutItemRequest().withTableName("category").withItem(category);
            client.putItem(categoryRequest);
            category.clear();
            
            category.put("categoryID", new AttributeValue().withS("101"));
            category.put("categoryName", new AttributeValue().withS("Desktops"));
            category.put("catalogID", new AttributeValue().withS("1000"));
            categoryRequest = new PutItemRequest().withTableName("category").withItem(category);
            client.putItem(categoryRequest);
            category.clear();

            category.put("categoryID", new AttributeValue().withS("102"));
            category.put("categoryName", new AttributeValue().withS("Softwares"));
            category.put("catalogID", new AttributeValue().withS("1000"));
            categoryRequest = new PutItemRequest().withTableName("category").withItem(category);
            client.putItem(categoryRequest);
            category.clear();

            category.put("categoryID", new AttributeValue().withS("103"));
            category.put("categoryName", new AttributeValue().withS("Android"));
            category.put("catalogID", new AttributeValue().withS("1001"));
            categoryRequest = new PutItemRequest().withTableName("category").withItem(category);
            client.putItem(categoryRequest);

        	
   
            
            
            
/*
           Map<String, AttributeValue> catalog = new HashMap<String, AttributeValue>();
            PutItemRequest catalogRequest = new PutItemRequest().withTableName(tableName).withItem(catalog);
            
            catalog.put("catalogID", new AttributeValue().withS("1000"));
            catalog.put("catalogName", new AttributeValue().withS("Computers"));
            catalogRequest = new PutItemRequest().withTableName("catalog").withItem(catalog);
            client.putItem(catalogRequest);
            catalog.clear();
            
            catalog.put("catalogID", new AttributeValue().withS("1001"));
            catalog.put("catalogName", new AttributeValue().withS("Cell Phones"));
            catalogRequest = new PutItemRequest().withTableName("catalog").withItem(catalog);
            client.putItem(catalogRequest);
            catalog.clear();

            catalog.put("catalogID", new AttributeValue().withS("1002"));
            catalog.put("catalogName", new AttributeValue().withS("Cameras"));
            catalogRequest = new PutItemRequest().withTableName("catalog").withItem(catalog);
            client.putItem(catalogRequest);*/
            

                
        }   catch (AmazonServiceException ase) {
            System.err.println("Failed to create item in " + tableName + " " + ase);
        } 

    }


}
