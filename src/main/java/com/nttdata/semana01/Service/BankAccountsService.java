package com.nttdata.semana01.Service;

import org.springframework.stereotype.Service;
 
import com.nttdata.semana01.Entity.BankAccounts; 
import com.nttdata.semana01.Repository.BankAccountsRepository; 

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class BankAccountsService {

	private final BankAccountsRepository bankAccountsRepository;
	
	public Mono<BankAccounts> createBankAccountsRepository(BankAccounts bankAccounts) {
		return bankAccountsRepository.save(bankAccounts);
	}
	
	public Flux<BankAccounts> getAllBankAccountsByCodeCustomerAndIdTypeBankAccount(String code_Customer, Integer typeBankAccounts) {
		return bankAccountsRepository.findAll().filter(x -> x.getCustomer().getCode_Customer().equals(code_Customer) && x.getTypeBankAccounts().getId().equals(typeBankAccounts));
	}
	
	public Flux<BankAccounts> getAllBankAccountsByNumberAccount(String numberAccount) {
		return bankAccountsRepository.findAll().filter(x -> x.getNumberAccount().equals(numberAccount));
	}
	
	public Flux<BankAccounts> getAllBankAccountsByCodeCustomer(String numberAccount) {
		return bankAccountsRepository.findAll().filter(x -> x.getCustomer().getCode_Customer().equals(numberAccount));
	}
	
}
