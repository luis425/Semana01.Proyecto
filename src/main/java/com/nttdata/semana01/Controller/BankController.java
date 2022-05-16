package com.nttdata.semana01.Controller;
  
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping; 
import org.springframework.web.bind.annotation.RestController; 
import org.springframework.web.server.ResponseStatusException;
 
import com.nttdata.semana01.Entity.Bank;
import com.nttdata.semana01.Service.BankService; 

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/bank")
public class BankController {

		private final BankService bankSerivce;
		 
		private String codigoValidator;
		
		// Registrar Banco
		
	 	@PostMapping  
	    public Mono<Bank> createBank(@RequestBody Bank bank){ 
	 		
	 	   boolean validationvalue = this.ValidationRegisterBankRequest(bank);
	 	   
	 	   if(validationvalue) {
	 		    		
	 		  /* Se agrego una busqueda por Codigo; por la razon que el id que se generar para el registro de Banco es un
	 		   	 autogeneracion Randon que nos brindan MongoDB; y transladando a casos reales es de preferencia no relacionar 
	 		   	 las consultas por ID, si no por un atributo Codigo para identificar con mas precision el datos que se desea consultar
	 		  */
	 		   
		 	  Flux<Bank> list = this.bankSerivce.getAllBankByCode(bank.getCode());
		 	   
		 	  List<Bank> list1 = new ArrayList<>();
		 	  
		 	  list.collectList().subscribe(list1::addAll);
		 	  
		 	 try{
		 		 
		 		  // Se agrego un Sleep para detener el proceso para obtener el valor deseado del Atributo list1 
		 		 
		 		  Thread.sleep(6*1000);
		 		 
		 		  if(list1.isEmpty()) {
		 			 codigoValidator = "";
		 		  }else {
			 		  codigoValidator = list1.get(0).getCode();   
		 		  }
		 		 
		 		  log.info("Validar Codigo Repetido --->" + codigoValidator);
			 	   
			 	   /* Validar si ya existe Codigo de Banco Registrado; se opto en crear un atributo code para la entidad Banco ya que 
			 	    el id es autogenerado con un  codigo Randon */
			 	   
			 	   if(codigoValidator != "" && codigoValidator.equals(bank.getCode())) {
		               return Mono.error(new ResponseStatusException(HttpStatus.PRECONDITION_FAILED,"El Codigo de Banco ya existe"));
			 	   }else {
			 		  return this.bankSerivce.createBank(bank);	
			 	   }
		 		 
			 	 }catch (Exception e) {
			 		   System.out.println(e);
			 		  return Mono.error(new ResponseStatusException(HttpStatus.CONFLICT,e.getMessage()));
			 	}
		 	  
	 	   }else {
	 		  return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST));
	 	   }
	 	     
	    }

	 	 @GetMapping(value = "/{code}")
	     public Mono<ResponseEntity<Flux<Bank>>> getBankByCode(@PathVariable String code){
	 		Flux<Bank> list = this.bankSerivce.getAllBankByCode(code);
	         return  Mono.just(ResponseEntity.ok()
	                 .contentType(MediaType.APPLICATION_JSON)
	                 .body(list))
	        		 .defaultIfEmpty(ResponseEntity.notFound().build()); 
	     }
	 	 
	 	 @GetMapping(value = "/BankbyId/{id}")
	 	 public Mono<ResponseEntity<Bank>> getBankById(@PathVariable String id){
	         var bank = this.bankSerivce.getBankbyId(id);
	         return bank.map(ResponseEntity::ok)
	                 .defaultIfEmpty(ResponseEntity.notFound().build());
	     }
	 	 
	 	
	 	 @GetMapping
	     public Mono<ResponseEntity<Flux<Bank>>>getAllBank() {
	         Flux<Bank> list = this.bankSerivce.getAllBank();
	         return  Mono.just(ResponseEntity.ok()
	                 .contentType(MediaType.APPLICATION_JSON)
	                 .body(list)); 
	     }
	 	 
	 	 
	 	@PutMapping(value = "/{code}")
	    public Mono<Bank> updateBank(@PathVariable String code,@RequestBody Bank bank){ 
	 		   		 
	 		
	 		 // Condicion para validar que no se puede actualizar el ID 
	 		
	 		 if(bank.getId() != null) {
	 			 return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,"El Atributo Id no puede actualizarse por ser un dato unico"));
	 		 }
	 		
		 	  Flux<Bank> list = this.bankSerivce.getAllBankByCode(code);
		 	   
		 	  List<Bank> list1 = new ArrayList<>();
		 	  
		 	  list.collectList().subscribe(list1::addAll);
		 	  
		 	 try{
		 		 
		 		  Thread.sleep(7*1000);
		 		 
		 		  if(list1.isEmpty()) {
		 			 return Mono.error(new ResponseStatusException(HttpStatus.PRECONDITION_FAILED,"El Codigo de Banco no existe"));
		 		  }else {
		 			 Bank bankUpdate = this.ValidationUpdateBankRequest(list1, bank);
		 			 return this.bankSerivce.createBank(bankUpdate);	
		 		  }
		 		 
			 	   
			 	 }catch (Exception e) {
			 		   System.out.println(e);
			 		  return Mono.error(new ResponseStatusException(HttpStatus.CONFLICT,e.getMessage()));
			 	}
		 	  
	    }
	 	 
	 	@DeleteMapping("/{id}")
	    public Mono<ResponseEntity<Void>> deleteBankById(@PathVariable String id){
	 		  
		 	 try{
		 		return this.bankSerivce.deleteBank(id)
		                .map(r -> ResponseEntity.ok().<Void>build())
		                .defaultIfEmpty(ResponseEntity.notFound().build());
		 		  
		 	 }catch (Exception e) {
		 		   System.out.println(e);
		 		  return Mono.error(new ResponseStatusException(HttpStatus.CONFLICT,e.getMessage()));
		 	}
		 		  
	    }
	 	 
	 	 // Validaciones para Regitrar Banco
	 	 
	 	 public boolean ValidationRegisterBankRequest (Bank bank) {
	 		 
	 		 boolean validatorbank;
	 		 
	 		 if(bank.getCode() == null || bank.getCode() == "") {
	 			validatorbank = false;
	 		 }else if(bank.getBankName() == null ||  bank.getBankName() == "") {
	 			validatorbank = false;
	 		 }else if (bank.getDirectionMain() == null || bank.getDirectionMain() == "") {
	 			validatorbank = false;
	 		 }else {
	 			validatorbank = true;
	 		 }
	 		 	 		 
	 		 return validatorbank;
	 	 }
	 	 
	 	 // Validaciones para Actualizar Banco
	 	 
		 public Bank ValidationUpdateBankRequest (List<Bank> list1, Bank bank) {
	 		 
	 		 
		 		if(bank.getDirectionMain() == null || bank.getDirectionMain() == "") {
		 			bank.setDirectionMain(list1.get(0).getDirectionMain());
		 		}else {
		 			// se mantiene el dato enviado en el request 
		 			log.info("Valor Direccion -->" + bank.getDirectionMain());
		 		}
		 		
		 		if(bank.getBankName() == null || bank.getBankName() == "") {
		 			bank.setBankName(list1.get(0).getBankName());
		 		}else {
		 			// se mantiene el dato enviado en el request 
		 			log.info("Valor Name -->" + bank.getBankName());
		 		}	 
		 		
		 		
		 		if(bank.getId() == null || bank.getId() == "") {
		 			bank.setId(list1.get(0).getId());
		 		}
		 		
		 		if(bank.getCode() == null || bank.getCode() == "") {
		 			bank.setCode(list1.get(0).getCode());
		 		}
		 		
		 		 return bank;
		 	 }
	 	 
	 	 
	 	 
}
