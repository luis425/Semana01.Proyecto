package com.nttdata.semana01.Service;

import org.springframework.stereotype.Service;
 
import com.nttdata.semana01.Entity.Credits; 
import com.nttdata.semana01.Repository.CreditsRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CreditsService {

private final CreditsRepository creditsRepository;
	
	public Mono<Credits> createCredits(Credits credits) {
		return creditsRepository.save(credits);
	}
	
	
	public Flux<Credits> getAllCreditsByCodeCustomerAndIdTypeCredits(String code_Customer, Integer typeCredits) {
		return creditsRepository.findAll().filter(x -> x.getCustomer().getCode_Customer().equals(code_Customer) && x.getTypeCredits().getId().equals(typeCredits));
	}
	
	public Flux<Credits> getAllCreditsByNumberAccount(String numberAccount) {
		return creditsRepository.findAll().filter(x -> x.getNumberCredits().equals(numberAccount));
	}
	
	public Flux<Credits> getAllCreditsByCodeCustomer(String numberAccount) {
		return creditsRepository.findAll().filter(x -> x.getCustomer().getCode_Customer().equals(numberAccount));
	}
	
}
