package com.nttdata.semana01.Service;

import org.springframework.stereotype.Service;

import com.nttdata.semana01.Entity.MovementsBankAccounts;
import com.nttdata.semana01.Entity.MovementsCredits;
import com.nttdata.semana01.Repository.MovementsCreditsRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MovementsCreditsService {

	private final MovementsCreditsRepository movementsCreditsRepository;
	
	public Mono<MovementsCredits> createMovementsCredits(MovementsCredits movementsCredits) {
		return movementsCreditsRepository.save(movementsCredits);
	}
	
	public Flux<MovementsCredits> getAllMovementsBankAccountsbyCodeCustomer(String code_Customer) {
		return movementsCreditsRepository.findAll().filter(x -> x.getCredits().getCustomer().getCode_Customer().equals(code_Customer));
	}
	
	public Flux<MovementsCredits> getAllMovementsBankAccount() {
		return movementsCreditsRepository.findAll();
	}

	public Flux<MovementsCredits> getMovementsCreditsbyNumberAccount(String numberAccount) {
		return movementsCreditsRepository.findAll().filter(x -> x.getCredits().getNumberCredits().equals(numberAccount));
	}
	
}
