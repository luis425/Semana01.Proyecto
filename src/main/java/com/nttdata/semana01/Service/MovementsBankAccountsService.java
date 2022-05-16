package com.nttdata.semana01.Service;

import org.springframework.stereotype.Service; 
import com.nttdata.semana01.Entity.MovementsBankAccounts;
import com.nttdata.semana01.Repository.MovementsBankAccountsRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MovementsBankAccountsService {
	
	private final MovementsBankAccountsRepository movementsBankAccountsRepository;
	
	public Mono<MovementsBankAccounts> createMovementsBankAccounts(MovementsBankAccounts bankAccounts) {
		return movementsBankAccountsRepository.save(bankAccounts);
	}
	
	public Flux<MovementsBankAccounts> getAllMovementsBankAccountsByNumberAccount(String numberAccount,Integer ultimaFechaEnviado) {
		return movementsBankAccountsRepository.findAll().filter(x -> x.getBankAccounts().getNumberAccount().equals(numberAccount)
				                                               && x.getDateMovement().getMonth()+1 == ultimaFechaEnviado);
	}
	
	public Flux<MovementsBankAccounts> getAllMovementsBankAccountsbyCodeCustomer(String code_Customer) {
		return movementsBankAccountsRepository.findAll().filter(x -> x.getBankAccounts().getCustomer().getCode_Customer().equals(code_Customer));
	}
	
	public Flux<MovementsBankAccounts> getAllMovementsBankAccount() {
		return movementsBankAccountsRepository.findAll();
	}

	public Flux<MovementsBankAccounts> getMovementsBankAccountsbyNumberAccount(String numberAccount) {
		return movementsBankAccountsRepository.findAll().filter(x -> x.getBankAccounts().getNumberAccount().equals(numberAccount));
	}
	
}
