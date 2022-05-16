package com.nttdata.semana01.Service;

import org.springframework.stereotype.Service; 
import com.nttdata.semana01.Entity.CustomerType;
import com.nttdata.semana01.Repository.CustomerTypeRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CustomerTypeService {

	private final CustomerTypeRepository customerTypeRepository;
	
	public Mono<CustomerType> createCustomerType(CustomerType customerType) {
		return customerTypeRepository.save(customerType);
	}
	
	public Mono<CustomerType> getCustomerTypebyId(Integer id) {
		return customerTypeRepository.findById(id);
	}
	
	public Flux<CustomerType> getAllCustomerType() {
		return customerTypeRepository.findAll();
	}

}
