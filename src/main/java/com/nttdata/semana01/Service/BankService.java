package com.nttdata.semana01.Service;

import org.springframework.stereotype.Service;

import com.nttdata.semana01.Entity.Bank;
import com.nttdata.semana01.Repository.BankRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class BankService {

	private final BankRepository bankRepository;
 
	public Flux<Bank> getAllBank() {
		return bankRepository.findAll();
	}

	public Flux<Bank> getAllBankByCode(String code) {
		return bankRepository.findAll().filter(x -> x.getCode().equals(code));
	}

	public Mono<Bank> createBank(Bank bank) {
		return bankRepository.save(bank);
	}

	public Mono<Bank> getBankbyId(String id) {
		return bankRepository.findById(id);
	}

	public Mono<Bank> deleteBank(String id) {
		return bankRepository.findById(id).flatMap(existsBank -> bankRepository
				.delete(existsBank).then(Mono.just(existsBank)));
	}
}
