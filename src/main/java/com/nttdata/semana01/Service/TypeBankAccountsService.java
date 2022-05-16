package com.nttdata.semana01.Service;

import org.springframework.stereotype.Service;

import com.nttdata.semana01.Entity.TypeBankAccounts;
import com.nttdata.semana01.Repository.TypeBankAccountsRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
 


@Service
@RequiredArgsConstructor
public class TypeBankAccountsService {

	private final TypeBankAccountsRepository typeBankAccountsRepository;
	
	public Mono<TypeBankAccounts> createTypeBankAccounts(TypeBankAccounts customerType) {
		return typeBankAccountsRepository.save(customerType);
	}
	
	public Mono<TypeBankAccounts> getTypeBankAccountsbyId(Integer id) {
		return typeBankAccountsRepository.findById(id);
	}
	
	public Flux<TypeBankAccounts> getAllTypeBankAccounts() {
		return typeBankAccountsRepository.findAll();
	}

}