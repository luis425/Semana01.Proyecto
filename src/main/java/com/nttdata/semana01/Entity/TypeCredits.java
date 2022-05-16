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
public class TypeCredits {

	@Id
	private Integer id;
	 
	private String description;
	
} 