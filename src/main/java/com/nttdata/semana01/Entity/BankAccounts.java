package com.nttdata.semana01.Entity;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data; 

@AllArgsConstructor
@Document
@Data
@Builder
public class BankAccounts {
	
	@Id
	private String id;
	
	private TypeBankAccounts typeBankAccounts;
	
	private String numberAccount;
	
	// Clave de cuenta - Deberia incriptarse
	private int keyAccount;
	
	// Monto Dispoible de la cuenta
	private double availableBalanceAccount;
	 
	@JsonFormat(pattern="dd-MM-yyyy" , timezone="GMT-05:00")
	private Date dateCreationBankAccount;
	
	// Postman enviar 2022-05-16T08:55:17.688+00:00
	@JsonFormat(pattern="dd-MM-yyyy" , timezone="GMT-05:00")
	private Date dateLastBankAccount;
	
	// Estado 
	private boolean statusAccount; 
	
	private Customer customer;
	

}
