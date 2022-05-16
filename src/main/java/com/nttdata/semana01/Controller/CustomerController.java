package com.nttdata.semana01.Controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.nttdata.semana01.Entity.Bank;
import com.nttdata.semana01.Entity.Customer;
import com.nttdata.semana01.Entity.CustomerType;
import com.nttdata.semana01.Service.BankService;
import com.nttdata.semana01.Service.CustomerService;
import com.nttdata.semana01.Service.CustomerTypeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/customer")
public class CustomerController {

	private final CustomerService customerService;

	private final CustomerTypeService customerTypeSerivce;

	private final BankService bankSerivce;

	private String codigoValidatorBank;

	private Integer codigoValidatorCustomerType;

	@GetMapping
	public Mono<ResponseEntity<Flux<Customer>>> getAllBank() {
		Flux<Customer> list = this.customerService.getAllCustomer();
		return Mono.just(ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(list));
	}

	@PostMapping
	public Mono<Customer> create(@RequestBody Customer customer) {

		boolean validationvalue = this.ValidationRegisterCustomerRequest(customer);

		if (validationvalue) {

			Flux<Bank> list = this.bankSerivce.getAllBankByCode(customer.getBank().getCode());

			List<Bank> listBank = new ArrayList<>();

			list.collectList().subscribe(listBank::addAll);

			var typeCostumer = this.customerTypeSerivce.getCustomerTypebyId(customer.getCustomertype().getId());

			List<CustomerType> listCustomerType = new ArrayList<>();

			typeCostumer.flux().collectList().subscribe(listCustomerType::addAll);

			try {

				Thread.sleep(6 * 1000);

				codigoValidatorBank = this.ValidardorBank(listBank,customer);

				log.info("Validar Codigo Repetido --->" + codigoValidatorBank);

				codigoValidatorCustomerType = this.ValidardorCustomerType(listCustomerType,customer);

				log.info("Obtener valor para validar Id --->" + codigoValidatorCustomerType);

				if (codigoValidatorBank == "" ) {
					return Mono.error(new ResponseStatusException(HttpStatus.PRECONDITION_FAILED,
							"El Codigo de Banco no existe"));
				}

				if (codigoValidatorCustomerType == 0) {
					return Mono.error(new ResponseStatusException(HttpStatus.PRECONDITION_FAILED,
							"El Id de Tipo Cliente no existe"));
				}

				return this.customerService.createCustomer(customer);

			} catch (Exception e) {
				System.out.println(e);
				return Mono.error(new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage()));
			}

		} else {
			return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST));
		}

	}
	
	 @GetMapping(value = "/{code_Customer}")
     public Mono<ResponseEntity<Flux<Customer>>> getCustomerByCode(@PathVariable String code_Customer){
 		Flux<Customer> list = this.customerService.getAllCustomerByCode(code_Customer);
         return  Mono.just(ResponseEntity.ok()
                 .contentType(MediaType.APPLICATION_JSON)
                 .body(list))
        		 .defaultIfEmpty(ResponseEntity.notFound().build()); 
     }

	 
	 @GetMapping(value = "/CustomerByCodeBank/{code_Bank}")
     public Mono<ResponseEntity<Flux<Customer>>> getCustomerByCodeBank(@PathVariable String code_Bank){
 		Flux<Customer> list = this.customerService.getAllCustomerByCodeBank(code_Bank);
         return  Mono.just(ResponseEntity.ok()
                 .contentType(MediaType.APPLICATION_JSON)
                 .body(list))
        		 .defaultIfEmpty(ResponseEntity.notFound().build()); 
     }
	 
	// Validaciones para Regitrar 

	public boolean ValidationRegisterCustomerRequest(Customer customer) {

		boolean validatorcustomer;

		if (customer.getCode_Customer() == null || customer.getCode_Customer() == "") {
			validatorcustomer = false;
		} else if (customer.getName_Customer() == null || customer.getName_Customer() == "") {
			validatorcustomer = false;
		} else if (customer.getLast_Name_Customer() == null || customer.getLast_Name_Customer() == "") {
			validatorcustomer = false;
		} else if (customer.getDirection_Customer() == null || customer.getDirection_Customer() == "") {
			validatorcustomer = false;
		} else if (customer.getEmail_Customer() == null || customer.getEmail_Customer() == "") {
			validatorcustomer = false;
		} else if (customer.getPhone_Number_Customer() == null || customer.getPhone_Number_Customer() == "") {
			validatorcustomer = false;
		} else if (customer.getDNI_Customer() == null || customer.getDNI_Customer() == "") {
			validatorcustomer = false;
		} else if (customer.getCustomertype().getId() == null || customer.getCustomertype().getId() == 0) {
			validatorcustomer = false;
		} else if (customer.getBank().getCode() == null || customer.getBank().getCode() == "") {
			validatorcustomer = false;
		} else {
			validatorcustomer = true;
		}

		return validatorcustomer;
	}

	public String ValidardorBank(List<Bank> list1,Customer customer) {

		if (list1.isEmpty()) {
			codigoValidatorBank = "";
		} else {
			codigoValidatorBank = list1.get(0).getCode();
			
			customer.getBank().setId(list1.get(0).getId());
			customer.getBank().setCode(codigoValidatorBank);
			customer.getBank().setBankName(list1.get(0).getBankName());
			customer.getBank().setDirectionMain(list1.get(0).getDirectionMain());
			
		}

		return codigoValidatorBank;
	}

	public Integer ValidardorCustomerType(List<CustomerType> list1,Customer customer) {

		if (list1.isEmpty()) {
			codigoValidatorCustomerType = 0;
		} else {
			codigoValidatorCustomerType = list1.get(0).getId();
			
			customer.getCustomertype().setId(codigoValidatorCustomerType);
			customer.getCustomertype().setDescription(list1.get(0).getDescription());
		}

		return codigoValidatorCustomerType;
	}
}