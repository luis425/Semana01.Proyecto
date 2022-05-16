package com.nttdata.semana01.Entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient; 
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Document
@Data
@Builder
public class Bank {

	@Transient
	public static final String SEQUENCE_NAME = "bank_sequence";
	
	@Id
	private String id;
	 
	private String code;
	
	private String BankName;
	
	private String DirectionMain;
	
	
}