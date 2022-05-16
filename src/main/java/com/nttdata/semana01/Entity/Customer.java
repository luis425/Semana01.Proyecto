package com.nttdata.semana01.Entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Document
@Data
@Builder
public class Customer {

	@Id
	private String id;
	
	private String code_Customer;
	
	private String Name_Customer;
	
	private String Last_Name_Customer;
	
	private String Direction_Customer;
	
	private String Email_Customer;
	
	private String Phone_Number_Customer;
	
	//private String Birth_Date_Customer;
	
	//private String Register_Date_Customer;
	
	private String DNI_Customer;
	
	private CustomerType customertype;
	
	private Bank bank;
	
	// dni, tipo de cliente , banco pertenece
	
}
