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
import com.nttdata.semana01.Entity.Credits;
import com.nttdata.semana01.Entity.Customer; 
import com.nttdata.semana01.Entity.TypeCredits;
import com.nttdata.semana01.Service.BankAccountsService;
import com.nttdata.semana01.Service.CreditsService;
import com.nttdata.semana01.Service.CustomerService; 
import com.nttdata.semana01.Service.TypeCreditsService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/credits")
public class CreditsController {

	private final CustomerService customerService;

	private final TypeCreditsService typeCreditsService;

	private final BankAccountsService bankAccountsService;

	private final CreditsService creditsService;

	private String codigoValidatorCustomer;

	private Integer codigoValidatorTypeCredits;

	private String codigoValidatorNumberBankAccount;

	@PostMapping
	public Mono<Credits> create(@RequestBody Credits credits) {

		boolean validationvalue = this.ValidationRegisterRequest(credits);

		List<BankAccounts> listBankAccounts = new ArrayList<>();

		if (validationvalue) {

			Flux<Customer> list = this.customerService.getAllCustomerByCode(credits.getCustomer().getCode_Customer());

			List<Customer> listCustomer = new ArrayList<>();

			list.collectList().subscribe(listCustomer::addAll);

			var typeCredits = this.typeCreditsService.getTypeCreditsbyId(credits.getTypeCredits().getId());

			List<TypeCredits> listTypeCredits = new ArrayList<>();

			typeCredits.flux().collectList().subscribe(listTypeCredits::addAll);

			if (credits.isStatusRelationAccount() == true) {

				Flux<BankAccounts> BankAccounts = this.bankAccountsService
						.getAllBankAccountsByNumberAccount(credits.getBankAccounts().getNumberAccount());

				BankAccounts.collectList().subscribe(listBankAccounts::addAll);

			}

			try {

				Thread.sleep(8 * 1000);

				codigoValidatorCustomer = this.ValidardorCustomer(listCustomer, credits);

				log.info("Validar Codigo Repetido --->" + codigoValidatorCustomer);

				codigoValidatorTypeCredits = this.ValidardorTypeCredits(listTypeCredits, credits);

				log.info("Obtener valor para validar Id --->" + codigoValidatorTypeCredits);

				if (codigoValidatorCustomer == "") {
					return Mono.error(new ResponseStatusException(HttpStatus.PRECONDITION_FAILED,
							"El Codigo de Customer no existe"));
				}

				if (codigoValidatorTypeCredits == 0) {
					return Mono.error(new ResponseStatusException(HttpStatus.PRECONDITION_FAILED,
							"El Id de Tipo Cuenta Bancario no existe"));
				}

				if (credits.isStatusRelationAccount() == true) {

					codigoValidatorNumberBankAccount = this.ValidardorBankAccount(listBankAccounts, credits);

					log.info("Obtener valor para validar Id --->" + codigoValidatorNumberBankAccount);

					if (codigoValidatorCustomer == "") {
						return Mono.error(new ResponseStatusException(HttpStatus.PRECONDITION_FAILED,
								"El Codigo que se intenta relacion a una Cuenta Bancaria no existe"));
					}

				}

				// Validar dependiendo el Tipo de Cliente

				if (credits.getTypeCredits().getId().equals(1)) {

					// Credito Personal

					/*
					 * if (listBankAccounts.get(0).getAvailableBalanceAccount() != credits
					 * .getAvailableBalanceCreditMaximum()) { return Mono.error(new
					 * ResponseStatusException(HttpStatus.PRECONDITION_FAILED,
					 * "La tarjeta Asociada no puede tener diferente Saldo de la Cuenta Credito que se desea asociar"
					 * )); }
					 */

					if (listCustomer.get(0).getCustomertype().getId().equals(1)) {

						if (credits.isStatusRelationAccount() == true) {

							Flux<Credits> listFluxCredits = this.creditsService
									.getAllCreditsByCodeCustomerAndIdTypeCredits(
											credits.getCustomer().getCode_Customer(), credits.getTypeCredits().getId());

							List<Credits> listCreditsFluxVacio = new ArrayList<>();

							listFluxCredits.collectList().subscribe(listCreditsFluxVacio::addAll);

							Thread.sleep(5 * 1000);

							log.info("Obtener valor para si hay registro --->" + listCreditsFluxVacio.size());

							if (listCreditsFluxVacio.isEmpty()) {

								credits.setDateCreationCredit(new Date());
								credits.setStatusAccount(true);
								credits.setStatusRelationAccount(true);
								credits.setAvailableBalanceCreditMaximum(
										listBankAccounts.get(0).getAvailableBalanceAccount());
								credits.setAvailableBalanceCredit(listBankAccounts.get(0).getAvailableBalanceAccount());

								return this.creditsService.createCredits(credits);

							} else {
								return Mono.error(new ResponseStatusException(HttpStatus.PRECONDITION_FAILED,
										"Solo se permite un solo Credito por persona"));
							}

						} else {

							Flux<Credits> listFluxCredits = this.creditsService
									.getAllCreditsByCodeCustomerAndIdTypeCredits(
											credits.getCustomer().getCode_Customer(), credits.getTypeCredits().getId());

							List<Credits> listCreditsFluxVacio = new ArrayList<>();

							listFluxCredits.collectList().subscribe(listCreditsFluxVacio::addAll);

							Thread.sleep(5 * 1000);

							log.info("Obtener valor para si hay registro --->" + listCreditsFluxVacio.size());

							if (listCreditsFluxVacio.isEmpty()) {

								credits.setDateCreationCredit(new Date());
								credits.setStatusAccount(true);
								credits.setStatusRelationAccount(false);
								credits.setBankAccounts(null);
								credits.setAvailableBalanceCredit(credits.getAvailableBalanceCreditMaximum());

								return this.creditsService.createCredits(credits);

							} else {
								return Mono.error(new ResponseStatusException(HttpStatus.PRECONDITION_FAILED,
										"Solo se permite un solo Credito por persona"));
							}

						}
					} else {
						return Mono.error(new ResponseStatusException(HttpStatus.PRECONDITION_FAILED,
								"El cliente que se quiere asociar no es un tipo de Cliente Personal"));
					}

				} else if (listCustomer.get(0).getCustomertype().getId().equals(2)) {

					if (listCustomer.get(0).getCustomertype().getId().equals(2)) {

						if (credits.isStatusRelationAccount() == true) {

							credits.setDateCreationCredit(new Date());
							credits.setStatusAccount(true);
							credits.setStatusRelationAccount(true);
							credits.setAvailableBalanceCreditMaximum(
									listBankAccounts.get(0).getAvailableBalanceAccount());
							credits.setAvailableBalanceCredit(listBankAccounts.get(0).getAvailableBalanceAccount());

							return this.creditsService.createCredits(credits);

						} else {

							// Puede registrar cuantas veces quiera

							credits.setDateCreationCredit(new Date());
							credits.setStatusAccount(true);
							credits.setStatusRelationAccount(false);
							credits.setBankAccounts(null);
							credits.setAvailableBalanceCredit(credits.getAvailableBalanceCreditMaximum());

							return this.creditsService.createCredits(credits);

						}

					} else {
						return Mono.error(new ResponseStatusException(HttpStatus.PRECONDITION_FAILED,
								"El cliente que se quiere asociar no es un tipo de Cliente Empresarial"));
					}

				} else {

					if (credits.isStatusRelationAccount() == true) {

						credits.setDateCreationCredit(new Date());
						credits.setStatusAccount(true);
						credits.setStatusRelationAccount(true);
						credits.setAvailableBalanceCreditMaximum(listBankAccounts.get(0).getAvailableBalanceAccount());
						credits.setAvailableBalanceCredit(listBankAccounts.get(0).getAvailableBalanceAccount());

						return this.creditsService.createCredits(credits);

					} else {

						// Puede registrar cuantas veces quiera

						credits.setDateCreationCredit(new Date());
						credits.setStatusAccount(true);
						credits.setStatusRelationAccount(false);
						credits.setBankAccounts(null);
						credits.setAvailableBalanceCredit(credits.getAvailableBalanceCreditMaximum());

						return this.creditsService.createCredits(credits);

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
     public Mono<ResponseEntity<Flux<Credits>>> getBankAccountsByNumber_Account(@PathVariable String number_Account){
 		Flux<Credits> list = this.creditsService.getAllCreditsByNumberAccount(number_Account);
         return  Mono.just(ResponseEntity.ok()
                 .contentType(MediaType.APPLICATION_JSON)
                 .body(list))
        		 .defaultIfEmpty(ResponseEntity.notFound().build()); 
     }
	 
	 @GetMapping(value = "/CreditsbyCodeCustomer/{code_Customer}")
     public Mono<ResponseEntity<Flux<Credits>>> getBankAccountsByCodeCustomer(@PathVariable String code_Customer){
 		Flux<Credits> list = this.creditsService.getAllCreditsByCodeCustomer(code_Customer);
         return  Mono.just(ResponseEntity.ok()
                 .contentType(MediaType.APPLICATION_JSON)
                 .body(list))
        		 .defaultIfEmpty(ResponseEntity.notFound().build()); 
     }
	
	public boolean ValidationRegisterRequest(Credits credits) {

		boolean validatorbankAccounts;

		if (credits.isStatusRelationAccount() == true) {

			if (credits.getBankAccounts().getNumberAccount() == null
					|| credits.getBankAccounts().getNumberAccount() == "") {
				validatorbankAccounts = false;

			} else if (credits.getId() != null) {
				validatorbankAccounts = false;
			} else if (credits.getTypeCredits().getId() == null || credits.getTypeCredits().getId() == 0) {
				validatorbankAccounts = false;
			} else if (credits.getNumberCredits() == null || credits.getNumberCredits() == "") {
				validatorbankAccounts = false;
			} else if (credits.getKeyCredit() == 0) {
				validatorbankAccounts = false;
			} else if (credits.getAvailableBalanceCreditMaximum() != 0.00) {
				// Se agrego la validacion por el motivo que se tomara el saldo de la cuenta
				// asociada por eso no se debe enviar el saldo maximo
				validatorbankAccounts = false;
			} else if (credits.getAvailableBalanceCredit() != 0.00) {
				// Se agrego la validacion por el motivo que se tomara el saldo maximo del
				// Credito
				validatorbankAccounts = false;
			} else if (credits.getCustomer().getCode_Customer() == null
					|| credits.getCustomer().getCode_Customer() == "") {
				validatorbankAccounts = false;
			} else {
				validatorbankAccounts = true;

			}

		} else {

			if (credits.getId() != null) {
				validatorbankAccounts = false;
			} else if (credits.getTypeCredits().getId() == null || credits.getTypeCredits().getId() == 0) {
				validatorbankAccounts = false;
			} else if (credits.getNumberCredits() == null || credits.getNumberCredits() == "") {
				validatorbankAccounts = false;
			} else if (credits.getKeyCredit() == 0) {
				validatorbankAccounts = false;
			} else if (credits.getAvailableBalanceCreditMaximum() == 0.00) {
				validatorbankAccounts = false;
			} else if (credits.getAvailableBalanceCredit() != 0.00) {
				// Se agrego la validacion por el motivo que se tomara el saldo maximo del
				// Credito
				validatorbankAccounts = false;
			} else if (credits.getCustomer().getCode_Customer() == null
					|| credits.getCustomer().getCode_Customer() == "") {
				validatorbankAccounts = false;
			} else {
				validatorbankAccounts = true;
			}

		}

		return validatorbankAccounts;
	}

	public String ValidardorCustomer(List<Customer> list1, Credits credits) {

		if (list1.isEmpty()) {
			codigoValidatorCustomer = "";
		} else {
			codigoValidatorCustomer = list1.get(0).getCode_Customer();

			credits.getCustomer().setId(list1.get(0).getId());
			credits.getCustomer().setCode_Customer(codigoValidatorCustomer);
			credits.getCustomer().setName_Customer(list1.get(0).getName_Customer());
			credits.getCustomer().setLast_Name_Customer(list1.get(0).getLast_Name_Customer());
			credits.getCustomer().setDirection_Customer(list1.get(0).getDirection_Customer());
			credits.getCustomer().setEmail_Customer(list1.get(0).getEmail_Customer());
			credits.getCustomer().setPhone_Number_Customer(list1.get(0).getPhone_Number_Customer());
			credits.getCustomer().setDNI_Customer(list1.get(0).getDNI_Customer());
			credits.getCustomer().setCustomertype(list1.get(0).getCustomertype());
			credits.getCustomer().setBank(list1.get(0).getBank());

		}

		return codigoValidatorCustomer;
	}

	public Integer ValidardorTypeCredits(List<TypeCredits> list1, Credits credits) {

		if (list1.isEmpty()) {
			codigoValidatorTypeCredits = 0;
		} else {
			codigoValidatorTypeCredits = list1.get(0).getId();
			credits.getTypeCredits().setId(codigoValidatorTypeCredits);
			credits.getTypeCredits().setDescription(list1.get(0).getDescription());
		}

		return codigoValidatorTypeCredits;
	}

	public String ValidardorBankAccount(List<BankAccounts> list1, Credits credits) {

		if (list1.isEmpty()) {
			codigoValidatorNumberBankAccount = "";
		} else {
			codigoValidatorNumberBankAccount = list1.get(0).getNumberAccount();

			credits.getBankAccounts().setId(list1.get(0).getId());
			credits.getBankAccounts().setTypeBankAccounts(list1.get(0).getTypeBankAccounts());
			credits.getBankAccounts().setNumberAccount(codigoValidatorNumberBankAccount);
			credits.getBankAccounts().setKeyAccount(list1.get(0).getKeyAccount());
			credits.getBankAccounts().setAvailableBalanceAccount(list1.get(0).getAvailableBalanceAccount());
			credits.getBankAccounts().setDateCreationBankAccount(list1.get(0).getDateCreationBankAccount());
			credits.getBankAccounts().setDateLastBankAccount(list1.get(0).getDateLastBankAccount());
			credits.getBankAccounts().setStatusAccount(true);
			credits.getBankAccounts().setCustomer(list1.get(0).getCustomer());

		}

		return codigoValidatorCustomer;
	}

}
