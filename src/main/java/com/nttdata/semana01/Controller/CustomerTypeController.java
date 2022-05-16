package com.nttdata.semana01.Controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
 
import com.nttdata.semana01.Entity.CustomerType; 
import com.nttdata.semana01.Service.CustomerTypeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/customerType")
public class CustomerTypeController {

	private final CustomerTypeService customerTypeSerivce;

	private Integer codigoValidator;
	
	// Registrar CustomerType

	@PostMapping
	public Mono<CustomerType> createBank(@RequestBody CustomerType customerType) {

		boolean validationvalue = this.ValidationRegisterCustomerTypeRequest(customerType);

		if (validationvalue) {

			try {
				
				 var typeCostumer = this.customerTypeSerivce.getCustomerTypebyId(customerType.getId());
	
				 List<CustomerType> list1 = new ArrayList<>();
	
				  typeCostumer.flux().collectList().subscribe(list1::addAll);

				  Thread.sleep(5 * 1000);
				  
				  log.info("Obtener valor para validar Id --->" + list1);
				
				  codigoValidator = this.Validardor(list1);
				 
				  if(codigoValidator != 0 && codigoValidator.equals(customerType.getId())) {
		               return Mono.error(new ResponseStatusException(HttpStatus.PRECONDITION_FAILED,"El Id de Tipo Cliente ya existe"));
			 	   }else {
			 		  return this.customerTypeSerivce.createCustomerType(customerType);
			 	   } 
				 
			} catch (Exception e) {
				System.out.println(e);
				return Mono.error(new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage()));
			}

		} else {
			return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST));
		}

	}

	 @GetMapping
     public Mono<ResponseEntity<Flux<CustomerType>>>getAllCustomerType() {
         Flux<CustomerType> list = this.customerTypeSerivce.getAllCustomerType();
         return  Mono.just(ResponseEntity.ok()
                 .contentType(MediaType.APPLICATION_JSON)
                 .body(list)); 
     }
	 
	public Integer Validardor(List<CustomerType> list1) {
		 
		if(list1.isEmpty()) {
				 codigoValidator = 0;
		 }else {
		 	  codigoValidator = list1.get(0).getId();   
		 }
		
		return codigoValidator;
	}
	
	public boolean ValidationRegisterCustomerTypeRequest(CustomerType customerType) {

		boolean validatorCustomerType;

		if (customerType.getId() == null || customerType.getId() == 0) {
			validatorCustomerType = false;
		} else if (customerType.getDescription() == null || customerType.getDescription() == "") {
			validatorCustomerType = false;
		} else {
			validatorCustomerType = true;
		}

		return validatorCustomerType;
	}
}
