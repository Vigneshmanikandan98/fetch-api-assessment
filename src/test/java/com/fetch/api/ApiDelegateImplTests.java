package com.fetch.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.internal.constraintvalidators.hv.UUIDValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fetch.api.model.CustomError;
import com.fetch.api.service.ApiDelegateImpl;
import com.fetch.api.service.ReceiptService;
import com.fetch.model.InlineResponse200;
import com.fetch.model.InlineResponse2001;
import com.fetch.model.Item;
import com.fetch.model.Receipt;

/**
 * Test Class for ApiDelegateImplTests
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(OffsetDateTime.class)
public class ApiDelegateImplTests {
	
	private ReceiptService receiptService = new ReceiptService();
	private ApiDelegateImpl apiDelegate = new ApiDelegateImpl(receiptService);
	private String testID = "";
    
	/**
	 * isValidUUID(): To validate the UUID using regular expression.
	 * @param uuid
	 * @return
	 */
    public static boolean isValidUUID(String uuid) {
    	String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
        Pattern UUID_PATTERN = Pattern.compile(UUID_REGEX);
        if (uuid == null) {
            return false;
        }
        Matcher matcher = UUID_PATTERN.matcher(uuid);
        return matcher.matches();
    }
    
    /**
     * setup():To insert a test receipt before test run.
     */
    @BeforeEach
    public void setup() {
    	List<Item> items = new ArrayList<>();
		Receipt validReceipt = new Receipt();
		Item item = new Item();
		item.setShortDescription("testProduct");
		item.setPrice("2.2");
		items.add(item);
        validReceipt.setRetailer("RetailerName");
        validReceipt.setPurchaseDate(LocalDate.now());
        validReceipt.setPurchaseTime("13:01");
        validReceipt.setItems(items);
        validReceipt.setTotal("6.49");

        ResponseEntity<InlineResponse200> response = apiDelegate.receiptsProcessPost(validReceipt);
        testID = response.getBody().getId();
    }

    /**
     * testReceiptProcess(): To test receipt process
     */
    @Test
	public void testReceiptProcess() {
		
    	List<Item> items = new ArrayList<>();
		Receipt validReceipt = new Receipt();
		Item item = new Item();
		item.setShortDescription("testProduct");
		item.setPrice("2.2");
		items.add(item);
        validReceipt.setRetailer("RetailerName");
        validReceipt.setPurchaseDate(LocalDate.now());
        validReceipt.setPurchaseTime("13:01");
        validReceipt.setItems(items);
        validReceipt.setTotal("6.49");
        ResponseEntity<InlineResponse200> response = apiDelegate.receiptsProcessPost(validReceipt);
        
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(isValidUUID(response.getBody().getId()));
	}
    
    /**
     * testReceiptProcessEmptyArray(): To test an empty array.
     */
    @Test
	public void testReceiptProcessEmptyArray() {
		
		Receipt validReceipt = new Receipt();
		CustomError thrownError = assertThrows(CustomError.class, () -> {
			 ResponseEntity<InlineResponse200> response = apiDelegate.receiptsProcessPost(validReceipt);
        });
        
        assertEquals("Invalid Retailer name, Invalid Purchase time, Invalid Purchased Date, List of items are empty, Invalid Total", thrownError.getMessage());
        assertEquals(Integer.toString(HttpStatus.INTERNAL_SERVER_ERROR.value()), thrownError.getCode());
	}
    
    /**
     * testReceiptProcessInvalidRetailerName(): TO insert Invalid retailer name
     */
    @Test
    public void testReceiptProcessInvalidRetailerName() {
    	List<Item> items = new ArrayList<>();
		Receipt validReceipt = new Receipt();
		Item item = new Item();
		item.setShortDescription("testProduct");
		item.setPrice("2.2");
		items.add(item);
        validReceipt.setPurchaseDate(LocalDate.now());
        validReceipt.setPurchaseTime("13:01");
        validReceipt.setItems(items);
        validReceipt.setTotal("6.49");
        
        CustomError thrownError = assertThrows(CustomError.class, () -> {
			 ResponseEntity<InlineResponse200> response = apiDelegate.receiptsProcessPost(validReceipt);
       });
        
        assertEquals("Invalid Retailer name", thrownError.getMessage());
        assertEquals(Integer.toString(HttpStatus.INTERNAL_SERVER_ERROR.value()), thrownError.getCode());
    }
    
    /**
     * testReceiptProcessInvalidPurchaseTime(): To test invalid purchase time.
     */
    @Test
    public void testReceiptProcessInvalidPurchaseTime() {
    	List<Item> items = new ArrayList<>();
		Receipt validReceipt = new Receipt();
		Item item = new Item();
		item.setShortDescription("testProduct");
		item.setPrice("2.2");
		items.add(item);
        validReceipt.setRetailer("TestRetailer");
        validReceipt.setPurchaseDate(LocalDate.now());
        validReceipt.setItems(items);
        validReceipt.setTotal("6.49");
        
        CustomError thrownError = assertThrows(CustomError.class, () -> {
			 ResponseEntity<InlineResponse200> response = apiDelegate.receiptsProcessPost(validReceipt);
       });
        
        assertEquals("Invalid Purchase time", thrownError.getMessage());
        assertEquals(Integer.toString(HttpStatus.INTERNAL_SERVER_ERROR.value()), thrownError.getCode());
    }
    
    /**
     * testReceiptProcessInvalidPurchaseTimeRegex(): To test invalid purchase time with input.
     */
    @Test
    public void testReceiptProcessInvalidPurchaseTimeRegex() {
    	List<Item> items = new ArrayList<>();
		Receipt validReceipt = new Receipt();
		Item item = new Item();
		item.setShortDescription("testProduct");
		item.setPrice("2.2");
		items.add(item);
        validReceipt.setRetailer("TestRetailer");
        validReceipt.setPurchaseDate(LocalDate.now());
        validReceipt.setPurchaseTime("1301");
        validReceipt.setItems(items);
        validReceipt.setTotal("6.49");
        
        CustomError thrownError = assertThrows(CustomError.class, () -> {
			 ResponseEntity<InlineResponse200> response = apiDelegate.receiptsProcessPost(validReceipt);
       });
        
        assertEquals("Invalid Purchase time", thrownError.getMessage());
        assertEquals(Integer.toString(HttpStatus.INTERNAL_SERVER_ERROR.value()), thrownError.getCode());
    }
    
    /**
     * testReceiptProcessInvalidDate(): To test invalid date.
     */
    @Test
    public void testReceiptProcessInvalidDate() {
    	List<Item> items = new ArrayList<>();
		Receipt validReceipt = new Receipt();
		Item item = new Item();
		item.setShortDescription("testProduct");
		item.setPrice("2.2");
		items.add(item);
        validReceipt.setRetailer("TestRetailer");
        validReceipt.setPurchaseTime("13:01");
        validReceipt.setItems(items);
        validReceipt.setTotal("6.49");
        
        CustomError thrownError = assertThrows(CustomError.class, () -> {
			 ResponseEntity<InlineResponse200> response = apiDelegate.receiptsProcessPost(validReceipt);
       });
        
        assertEquals("Invalid Purchased Date", thrownError.getMessage());
        assertEquals(Integer.toString(HttpStatus.INTERNAL_SERVER_ERROR.value()), thrownError.getCode());
    }
    
    /**
     * testReceiptProcessEmptyItems(): To process empty items receipt.
     */
    @Test
    public void testReceiptProcessEmptyItems() {
    	List<Item> items = new ArrayList<>();
		Receipt validReceipt = new Receipt();
        validReceipt.setRetailer("TestRetailer");
        validReceipt.setPurchaseDate(LocalDate.now());
        validReceipt.setPurchaseTime("13:01");
        validReceipt.setItems(items);
        validReceipt.setTotal("6.49");
        
        CustomError thrownError = assertThrows(CustomError.class, () -> {
			 ResponseEntity<InlineResponse200> response = apiDelegate.receiptsProcessPost(validReceipt);
       });
        
        assertEquals("List of items are empty", thrownError.getMessage());
        assertEquals(Integer.toString(HttpStatus.INTERNAL_SERVER_ERROR.value()), thrownError.getCode());
    }
    
    /**
     * testReceiptProcessInvalidItemDescription(): To test invalid Item description.
     */
    @Test
    public void testReceiptProcessInvalidItemDescription() {
    	List<Item> items = new ArrayList<>();
		Receipt validReceipt = new Receipt();
		Item item = new Item();
		item.setPrice("2.2");
		items.add(item);
        validReceipt.setRetailer("TestRetailer");
        validReceipt.setPurchaseDate(LocalDate.now());
        validReceipt.setPurchaseTime("13:01");
        validReceipt.setItems(items);
        validReceipt.setTotal("6.49");
        
        CustomError thrownError = assertThrows(CustomError.class, () -> {
			 ResponseEntity<InlineResponse200> response = apiDelegate.receiptsProcessPost(validReceipt);
       });
        
        assertEquals("Invalid Item Description", thrownError.getMessage());
        assertEquals(Integer.toString(HttpStatus.INTERNAL_SERVER_ERROR.value()), thrownError.getCode());
    }
    
    /**
     * testReceiptProcessInvalidItemPrice(): To test invalid item price.
     */
    @Test
    public void testReceiptProcessInvalidItemPrice() {
    	List<Item> items = new ArrayList<>();
		Receipt validReceipt = new Receipt();
		Item item = new Item();
		item.setShortDescription("testProduct");
		items.add(item);
        validReceipt.setRetailer("TestRetailer");
        validReceipt.setPurchaseDate(LocalDate.now());
        validReceipt.setPurchaseTime("13:01");
        validReceipt.setItems(items);
        validReceipt.setTotal("6.49");
        
        CustomError thrownError = assertThrows(CustomError.class, () -> {
			 ResponseEntity<InlineResponse200> response = apiDelegate.receiptsProcessPost(validReceipt);
       });
        
        assertEquals("Invalid Item Price", thrownError.getMessage());
        assertEquals(Integer.toString(HttpStatus.INTERNAL_SERVER_ERROR.value()), thrownError.getCode());
    }
    
    /**
     * testReceiptProcessInvalidReceiptTotal(): To test invalid item total.
     */
    @Test
    public void testReceiptProcessInvalidReceiptTotal() {
    	List<Item> items = new ArrayList<>();
		Receipt validReceipt = new Receipt();
		Item item = new Item();
		item.setShortDescription("testProduct");
		item.setPrice("2.2");
		items.add(item);
        validReceipt.setRetailer("TestRetailer");
        validReceipt.setPurchaseDate(LocalDate.now());
        validReceipt.setPurchaseTime("13:01");
        validReceipt.setItems(items);
        
        CustomError thrownError = assertThrows(CustomError.class, () -> {
			 ResponseEntity<InlineResponse200> response = apiDelegate.receiptsProcessPost(validReceipt);
       });
        
        assertEquals("Invalid Total", thrownError.getMessage());
        assertEquals(Integer.toString(HttpStatus.INTERNAL_SERVER_ERROR.value()), thrownError.getCode());
    }
    
    /**
     * testReceiptsPoints(): To test Invalid receipt points
     */
    @Test
    public void testReceiptsPoints() {
    	ResponseEntity<InlineResponse2001> response = apiDelegate.receiptsIdPointsGet(testID);
    	
    	assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getPoints());
    	
    }
    
    /**
     * testReceiptsPointsInvalidID(): To test points endpoint with invalid ID.
     */
    @Test
    public void testReceiptsPointsInvalidID() {
    	
    	CustomError thrownError = assertThrows(CustomError.class, () -> {
    		ResponseEntity<InlineResponse2001> response = apiDelegate.receiptsIdPointsGet("1233");
      });
    	
    	assertEquals("Invalid Receipt ID.", thrownError.getMessage());
        assertEquals(Integer.toString(HttpStatus.INTERNAL_SERVER_ERROR.value()), thrownError.getCode());
    	
    }
    
    /**
     * testReceiptPointsReceiptOne(): To test given sample receipt one.
     */
    @Test
    public void testReceiptPointsReceiptOne() {
    	List<Item> items = new ArrayList<>();

    	String[][] itemData = {
    	    {"Mountain Dew 12PK", "6.49"},
    	    {"Emils Cheese Pizza", "12.25"},
    	    {"Knorr Creamy Chicken", "1.26"},
    	    {"Doritos Nacho Cheese", "3.35"},
    	    {"   Klarbrunn 12-PK 12 FL OZ  ", "12.00"}
    	};

    	for (String[] data : itemData) {
    	    Item item = new Item();
    	    item.setShortDescription(data[0]);
    	    item.setPrice(data[1]);
    	    items.add(item);
    	}

    	Receipt validReceipt = new Receipt();
    	validReceipt.setRetailer("Target");
    	validReceipt.setPurchaseDate(LocalDate.parse("2022-01-01"));
    	validReceipt.setPurchaseTime("13:01");
    	validReceipt.setItems(items);
    	validReceipt.setTotal("35.35");
    	ResponseEntity<InlineResponse200> processResponse = apiDelegate.receiptsProcessPost(validReceipt);
    	ResponseEntity<InlineResponse2001> pointsResponse = apiDelegate.receiptsIdPointsGet(processResponse.getBody().getId());
    	
    	assertEquals(HttpStatus.CREATED, pointsResponse.getStatusCode());
        assertNotNull(pointsResponse.getBody());
        assertEquals("28", String.valueOf(pointsResponse.getBody().getPoints()));
    
    }
    
    /**
     * testReceiptPointsReceiptTwo(): To test given sample receipt two.
     */
    @Test
    public void testReceiptPointsReceiptTwo() {
    	List<Item> items = new ArrayList<>();

    	String[][] itemData = {
    	    {"Gatorade", "2.25"},
    	    {"Gatorade", "2.25"},
    	    {"Gatorade", "2.25"},
    	    {"Gatorade", "2.25"}
    	};

    	for (String[] data : itemData) {
    	    Item item = new Item();
    	    item.setShortDescription(data[0]);
    	    item.setPrice(data[1]);
    	    items.add(item);
    	}

    	Receipt validReceipt = new Receipt();
    	validReceipt.setRetailer("M&M Corner Market");
    	validReceipt.setPurchaseDate(LocalDate.parse("2022-03-20"));
    	validReceipt.setPurchaseTime("14:33");
    	validReceipt.setItems(items);
    	validReceipt.setTotal("9.00");
    	ResponseEntity<InlineResponse200> processResponse = apiDelegate.receiptsProcessPost(validReceipt);
    	ResponseEntity<InlineResponse2001> pointsResponse = apiDelegate.receiptsIdPointsGet(processResponse.getBody().getId());
    	
    	assertEquals(HttpStatus.CREATED, pointsResponse.getStatusCode());
        assertNotNull(pointsResponse.getBody());
        assertEquals("109", String.valueOf(pointsResponse.getBody().getPoints()));
    
    }
}
