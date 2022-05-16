package com.nttdata.semana01.Controller;

import java.util.ArrayList;
import java.util.Date;
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
import com.nttdata.semana01.Entity.BankAccounts;
import com.nttdata.semana01.Entity.Customer; 
import com.nttdata.semana01.Entity.TypeBankAccounts;
import com.nttdata.semana01.Service.BankAccountsService;
import com.nttdata.semana01.Service.CustomerService;
import com.nttdata.semana01.Service.TypeBankAccountsService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/bankAccounts")
public class BankAccountsController {
	
	private final CustomerService customerService;
	
	private final TypeBankAccountsService typeBankAccountsService;
	
	private final BankAccountsService bankAccountsService;
	
	private String codigoValidatorCustomer;

	private Integer codigoValidatorTypeBankAccounts;

	@PostMapping
	public Mono<BankAccounts> create(@RequestBody BankAccounts bankAccounts) {

		boolean validationvalue = this.ValidationRegisterRequest(bankAccounts);

		if (validationvalue) {

			Flux<Customer> list = this.customerService.getAllCustomerByCode(bankAccounts.getCustomer().getCode_Customer());

			List<Customer> listCustomer = new ArrayList<>();

			list.collectList().subscribe(listCustomer::addAll);

			var typeBanksAccounts = this.typeBankAccountsService.getTypeBankAccountsbyId(bankAccounts.getTypeBankAccounts().getId());

			List<TypeBankAccounts> listtypeBanksAccounts = new ArrayList<>();

			typeBanksAccounts.flux().collectList().subscribe(listtypeBanksAccounts::addAll);
			
			try {

				Thread.sleep(6 * 1000);

				codigoValidatorCustomer = this.ValidardorCustomer(listCustomer,bankAccounts);

				log.info("Validar Codigo Repetido --->" + codigoValidatorCustomer);

				codigoValidatorTypeBankAccounts = this.ValidardorTypeBankAccounts(listtypeBanksAccounts,bankAccounts);

				log.info("Obtener valor para validar Id --->" + codigoValidatorTypeBankAccounts);

				if (codigoValidatorCustomer == "" ) {
					return Mono.error(new ResponseStatusException(HttpStatus.PRECONDITION_FAILED,
							"El Codigo de Customer no existe"));
				}

				if (codigoValidatorTypeBankAccounts == 0) {
					return Mono.error(new ResponseStatusException(HttpStatus.PRECONDITION_FAILED,
							"El Id de Tipo Cuenta Bancario no existe"));
				}
			
				// Validar dependiendo el Tipo de Cliente 
				
				if(listCustomer.get(0).getCustomertype().getId().equals(1)) {
					
					// Personal
					
					Flux<BankAccounts> listFluxBankAccounts = this.bankAccountsService.getAllBankAccountsByCodeCustomerAndIdTypeBankAccount(bankAccounts.getCustomer().getCode_Customer(),bankAccounts.getTypeBankAccounts().getId());

					List<BankAccounts> listBankAccounts = new ArrayList<>();

					listFluxBankAccounts.collectList().subscribe(listBankAccounts::addAll);
					
					Thread.sleep(5 * 1000);
					
					log.info("Obtener valor para si hay registro --->" + listBankAccounts.toString());
					
					if(listBankAccounts.isEmpty()) {

						if(bankAccounts.getTypeBankAccounts().getId().equals(3) && bankAccounts.getDateLastBankAccount() == null) {
							return Mono.error(new ResponseStatusException(HttpStatus.PRECONDITION_FAILED,
									"Es necesario registrar el atributo dateLastBankAccount, cuando el Tipo de Cuenta es Plazo Fijo, con el formato yyyy-MM-ddT08:55:17.688+00:00"));
						}else {

							bankAccounts.setDateCreationBankAccount(new Date());	
							return this.bankAccountsService.createBankAccountsRepository(bankAccounts);
							
						}
						
					}else {
						return Mono.error(new ResponseStatusException(HttpStatus.PRECONDITION_FAILED,
								"El Cliente Personal solo puede tener  un máximo de una cuenta de ahorro, una cuenta corriente o cuentas a plazo fijo."));
					}
					
				}else {
					
					// Empresarial 
										
					if(bankAccounts.getTypeBankAccounts().getId().equals(1)) {
						return Mono.error(new ResponseStatusException(HttpStatus.PRECONDITION_FAILED,
								"Un Cliente Empresarial no puede tener una Cuenta de Ahorro."));
					}else if(bankAccounts.getTypeBankAccounts().getId().equals(3)) {
						return Mono.error(new ResponseStatusException(HttpStatus.PRECONDITION_FAILED,
								"Un Cliente Empresarial no puede tener una cuenta de Plazo Fijo."));
					}
					else {

						bankAccounts.setDateCreationBankAccount(new Date());	
						return this.bankAccountsService.createBankAccountsRepository(bankAccounts);
					}
					 
				}
				
			} catch (Exception e) {
				System.out.println(e);
				return Mono.error(new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage()));
			}

		} else {
			return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST));
		}

	}
	
	 @GetMapping(value = "/{number_Account}")
     public Mono<ResponseEntity<Flux<BankAccounts>>> getBankAccountsByNumber_Account(@PathVariable String number_Account){
 		Flux<BankAccounts> list = this.bankAccountsService.getAllBankAccountsByNumberAccount(number_Account);
         return  Mono.just(ResponseEntity.ok()
                 .contentType(MediaType.APPLICATION_JSON)
                 .body(list))
        		 .defaultIfEmpty(ResponseEntity.notFound().build()); 
     }
	 
	 @GetMapping(value = "/BankAccountsbyCodeCustomer/{code_Customer}")
     public Mono<ResponseEntity<Flux<BankAccounts>>> getBankAccountsByCodeCustomer(@PathVariable String code_Customer){
 		Flux<BankAccounts> list = this.bankAccountsService.getAllBankAccountsByCodeCustomer(code_Customer);
         return  Mono.just(ResponseEntity.ok()
                 .contentType(MediaType.APPLICATION_JSON)
                 .body(list))
        		 .defaultIfEmpty(ResponseEntity.notFound().build()); 
     }
	
	public boolean ValidationRegisterRequest(BankAccounts bankAccounts) {

		boolean validatorbankAccounts;

		if (bankAccounts.getId() != null ) {
			validatorbankAccounts = false;
		} else if (bankAccounts.getTypeBankAccounts().getId() == null || bankAccounts.getTypeBankAccounts().getId()  == 0) {
			validatorbankAccounts = false;
		} else if (bankAccounts.getNumberAccount() == null || bankAccounts.getNumberAccount() == "") {
			validatorbankAccounts = false;
		} else if (bankAccounts.getKeyAccount() == 0) {
			validatorbankAccounts = false;
		} else if (bankAccounts.getAvailableBalanceAccount() == 0.00) {
			validatorbankAccounts = false;
		} else if (bankAccounts.getCustomer().getCode_Customer() == null || bankAccounts.getCustomer().getCode_Customer() == "") {
			validatorbankAccounts = false;
		} else {
			validatorbankAccounts = true;
		}

		return validatorbankAccounts;
	}
	
	public String ValidardorCustomer(List<Customer> list1,BankAccounts bankAccounts) {

		if (list1.isEmpty()) {
			codigoValidatorCustomer = "";
		} else {
			codigoValidatorCustomer = list1.get(0).getCode_Customer();
			
			bankAccounts.getCustomer().setId(list1.get(0).getId());
			bankAccounts.getCustomer().setCode_Customer(codigoValidatorCustomer);
			bankAccounts.getCustomer().setName_Customer(list1.get(0).getName_Customer());
			bankAccounts.getCustomer().setLast_Name_Customer(list1.get(0).getLast_Name_Customer());
			bankAccounts.getCustomer().setDirection_Customer(list1.get(0).getDirection_Customer());
			bankAccounts.getCustomer().setEmail_Customer(list1.get(0).getEmail_Customer());
			bankAccounts.getCustomer().setPhone_Number_Customer(list1.get(0).getPhone_Number_Customer());
			bankAccounts.getCustomer().setDNI_Customer(list1.get(0).getDNI_Customer());
			bankAccounts.getCustomer().setCustomertype(list1.get(0).getCustomertype());
			bankAccounts.getCustomer().setBank(list1.get(0).getBank());
			
		}

		return codigoValidatorCustomer;
	}
	
	public Integer ValidardorTypeBankAccounts(List<TypeBankAccounts> list1,BankAccounts bankAccounts) {

		if (list1.isEmpty()) {
			codigoValidatorTypeBankAccounts = 0;
		} else {
			codigoValidatorTypeBankAccounts = list1.get(0).getId();
			
			bankAccounts.getTypeBankAccounts().setId(codigoValidatorTypeBankAccounts);
			bankAccounts.getTypeBankAccounts().setDescription(list1.get(0).getDescription());
			bankAccounts.getTypeBankAccounts().setCommission(list1.get(0).getCommission());
			bankAccounts.getTypeBankAccounts().setMaximumLimit(list1.get(0).getMaximumLimit());
		}

		return codigoValidatorTypeBankAccounts;
	}
}
