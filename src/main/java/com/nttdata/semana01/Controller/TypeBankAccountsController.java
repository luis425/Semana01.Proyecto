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
 
import com.nttdata.semana01.Entity.TypeBankAccounts; 
import com.nttdata.semana01.Service.TypeBankAccountsService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/typeBankAccounts")
public class TypeBankAccountsController {

	private final TypeBankAccountsService typeBankAccountsService;

	private Integer codigoValidator;
	
	// Registrar CustomerType

	@PostMapping
	public Mono<TypeBankAccounts> createTypeBankAccounts(@RequestBody TypeBankAccounts typeBankAccounts) {

		boolean validationvalue = this.ValidationRegisterTypeBankAccountsRequest(typeBankAccounts);

		if (validationvalue) {

			try {
				
				 var typeBankAccount = this.typeBankAccountsService.getTypeBankAccountsbyId(typeBankAccounts.getId());
	
				 List<TypeBankAccounts> list1 = new ArrayList<>();
	
				 typeBankAccount.flux().collectList().subscribe(list1::addAll);

				  Thread.sleep(5 * 1000);
				  
				  log.info("Obtener valor para validar Id --->" + list1);
				
				  codigoValidator = this.Validardor(list1);
				 
				  if(codigoValidator != 0 && codigoValidator.equals(typeBankAccounts.getId())) {
		               return Mono.error(new ResponseStatusException(HttpStatus.PRECONDITION_FAILED,"El Id de Tipo Cuenta Bancario ya existe"));
			 	  }else {
			 		  return this.typeBankAccountsService.createTypeBankAccounts(typeBankAccounts);
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
     public Mono<ResponseEntity<Flux<TypeBankAccounts>>>getAllCustomerType() {
         Flux<TypeBankAccounts> list = this.typeBankAccountsService.getAllTypeBankAccounts();
         return  Mono.just(ResponseEntity.ok()
                 .contentType(MediaType.APPLICATION_JSON)
                 .body(list)); 
     }
	 
	public Integer Validardor(List<TypeBankAccounts> list1) {
		 
		if(list1.isEmpty()) {
				 codigoValidator = 0;
		 }else {
		 	  codigoValidator = list1.get(0).getId();   
		 }
		
		return codigoValidator;
	}
	
	public boolean ValidationRegisterTypeBankAccountsRequest(TypeBankAccounts typeBankAccounts) {

		boolean validatorTypeBankAccounts;

		if (typeBankAccounts.getId() == null || typeBankAccounts.getId() == 0) {
			validatorTypeBankAccounts = false;
		} else if (typeBankAccounts.getDescription() == null || typeBankAccounts.getDescription() == "") {
			validatorTypeBankAccounts = false;
		} else {
			
			if(typeBankAccounts.getCommission() == null) {
				typeBankAccounts.setCommission(0);
			}
			
			if(typeBankAccounts.getMaximumLimit() == null) {
				typeBankAccounts.setMaximumLimit(0);
			}
			
			validatorTypeBankAccounts = true;
		}

		return validatorTypeBankAccounts;
	}
}
