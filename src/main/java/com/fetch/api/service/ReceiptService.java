package com.fetch.api.service;

import org.springframework.stereotype.Service;

import com.fetch.model.Item;
import com.fetch.model.Receipt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service to process handling Receipts
 */
@Service
public class ReceiptService {
	
	private static final String TIME_24HOUR_PATTERN = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$";
	
	// To save the receipts in-memory.
    private Map<String, Receipt> receiptStore = new ConcurrentHashMap<>();

    /**
     * save(): To save the receipt in the receiptStore map with its ID as key.
     * @param receipt
     * @return
     */
    public String save(Receipt receipt) {
        String id = UUID.randomUUID().toString();
        receiptStore.put(id, receipt);
        return id;
    }

    /**
     * findByID(): To find a receipt based on the ID.
     * @param id
     * @return
     */
    public Receipt findById(String id) {
        return receiptStore.get(id);
    }
    
    /**
     * isValidTime(): To check whether the time format is correct using regular expression.
     * @param time
     * @return
     */
    public static boolean isValidTime(String time) {
        Pattern pattern = Pattern.compile(TIME_24HOUR_PATTERN);
        Matcher matcher = pattern.matcher(time);
        return matcher.matches();
    }
    
    /**
     * validateReceipt(): To validate the receipt before saving it to the in-memory map.
     * @param receipt
     * @return
     */
    public List<String> validateReceipt(Receipt receipt) {
    	
    	List<String> errors = new ArrayList<>();
    	
    	if(receipt.getRetailer() == null || receipt.getRetailer().isEmpty()) {
    		errors.add("Invalid Retailer name");
    	}
    	
    	if(receipt.getPurchaseTime() == null || !isValidTime(receipt.getPurchaseTime())) {
    		errors.add("Invalid Purchase time");
    	}
    	
    	if(receipt.getPurchaseDate() == null) {
    		errors.add("Invalid Purchased Date");
    	}
    	
    	if(receipt.getItems().size() == 0) {
    		errors.add("List of items are empty");
    	}else {
    		for(Item item: receipt.getItems()) {
    			if(item.getShortDescription() == null || item.getShortDescription().isEmpty()) {
    				errors.add("Invalid Item Description");
    				break;
    			}
    			if(item.getPrice() == null || item.getPrice().isEmpty()) {
    				errors.add("Invalid Item Price");
    				break;
    			}
    		}
    	}
    	
    	if(receipt.getTotal() == "" || receipt.getTotal() == null) {
    		errors.add("Invalid Total");
    	}
    	
    	return errors;
    }
    
    /**
     * calculatePoints(): To Calculate points for the receipt based on the id.
     * @param id
     * @return
     */
    public long calculatePoints(String id) {
    	
    	Receipt receipt = receiptStore.get(id);
    	
    	long points = 0;
		
    	//To calculate points based on length of the retailer's name
		for (char c : receipt.getRetailer().toCharArray()) {
            if (Character.isLetterOrDigit(c)) {
                points++;
            }
        }
				
		double total = Double.parseDouble(receipt.getTotal());
		
		//To check if the total is a round amount.
		if(total % 1 == 0) {
			points += 50;
		}
				
		//To check if the total is divisible by 0.25
		if(total % 0.25 == 0) {
			points += 25;
		}
				
		//To calculate points for every 2 items in the receipt.
		points += (long) ((receipt.getItems().size())/2) * 5; 
		
		//To calculate points based on the description.
		for(Item item: receipt.getItems()) {
			if(item.getShortDescription().trim().length() % 3 == 0) {
				Double price = Double.parseDouble(item.getPrice()) *  0.2;
				points += Math.ceil(price);
			}
		}
		
		//To check if the day of the purchase date is odd or not.
		if(receipt.getPurchaseDate().getDayOfMonth() % 2 != 0) {
			points += 6;
		}
		
		//To check if the purchase time is between 2pm to 4pm.
		int hours = Integer.parseInt(receipt.getPurchaseTime().substring(0, 2));
		if(hours >= 14 && hours < 16) {
			points += 10;
		}
		
		return points;
    }
}
