package com.fetch.api.service;


import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fetch.api.model.CustomError;
import com.fetch.api.DefaultApiDelegate;
import com.fetch.model.InlineResponse200;
import com.fetch.model.InlineResponse2001;
import com.fetch.model.Item;
import com.fetch.model.Receipt;

/**
 * Service class for DefaultApiDelegate
 */
@Service
public class ApiDelegateImpl implements DefaultApiDelegate {
	
	//Object for ReceiptService class
    private final ReceiptService receiptService;
	
	@Autowired
    public ApiDelegateImpl(ReceiptService receiptService) {
        this.receiptService = receiptService;
    }
	
	/**
	 * receiptsProcessPost(): To Process POST receipt method.
	 */
	@Override
	public ResponseEntity<InlineResponse200> receiptsProcessPost(@Valid Receipt receipt){
		
		try {
			
			InlineResponse200 response = new InlineResponse200();
			List<String> receiptErrors = receiptService.validateReceipt(receipt);
			if(receiptErrors.size() == 0) {
				response.setId(receiptService.save(receipt));
				return ResponseEntity.status(201).body(response);
			}else {
				throw new CustomError().message(String.join(", ", receiptErrors)).code(Integer.toString(HttpStatus.INTERNAL_SERVER_ERROR.value()));
			}
			
		} catch (CustomError ex) {
			throw ex;
       }
		
	}
	
	/**
	 * receiptsIdPointsGet(): To process points GET method.
	 */
	@Override
	public ResponseEntity<InlineResponse2001> receiptsIdPointsGet(String id){
		
		try {
			
			InlineResponse2001 response = new InlineResponse2001();
			if(receiptService.findById(id) != null) {
				response.setPoints(receiptService.calculatePoints(id));
				return ResponseEntity.status(201).body(response);
			}else {
				throw new CustomError().message("Invalid Receipt ID.").code(Integer.toString(HttpStatus.INTERNAL_SERVER_ERROR.value()));
			}
			
		}catch (CustomError ex) {
			throw ex;
		}
	}

}
