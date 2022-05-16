package com.nttdata.semana01.Entity;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Document
@Data
@Builder
public class MovementsBankAccounts {

	@Id
	private String id;
	
	private String MovementsBankAccountsCode;
	
	// Monto
	private double amount;
	 
	@JsonFormat(pattern="dd-MM-yyyy" , timezone="GMT-05:00")
	private Date dateMovement = new Date();
		
	private String description;
	
	private BankAccounts bankAccounts;
	
	private String numberAccountDestination;
	
}
