package com.nttdata.semana01.Service;

import org.springframework.stereotype.Service;
 
import com.nttdata.semana01.Entity.TypeCredits; 
import com.nttdata.semana01.Repository.TypeCreditsRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TypeCreditsService {

private final TypeCreditsRepository typeCreditsRepository;
	
	public Mono<TypeCredits> createTypeCredits(TypeCredits typeCredits) {
		return typeCreditsRepository.save(typeCredits);
	}
	
	public Mono<TypeCredits> getTypeCreditsbyId(Integer id) {
		return typeCreditsRepository.findById(id);
	}
	
	public Flux<TypeCredits> getAllTypeCredits() {
		return typeCreditsRepository.findAll();
	}
	
}
