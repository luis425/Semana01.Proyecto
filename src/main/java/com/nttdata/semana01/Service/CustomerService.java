package com.nttdata.semana01.Service;

import org.springframework.stereotype.Service;

import com.nttdata.semana01.Entity.Customer; 
import com.nttdata.semana01.Repository.CustomerRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CustomerService {

	private final CustomerRepository customerRepository;
	 
	public Flux<Customer> getAllCustomer() {
		return customerRepository.findAll();
	}
	
	public Mono<Customer> createCustomer(Customer customer) {
		return customerRepository.save(customer);
	}
 	
	public Flux<Customer> getAllCustomerByCode(String code_Customer) {
		return customerRepository.findAll().filter(x -> x.getCode_Customer().equals(code_Customer));
	}

	public Flux<Customer> getAllCustomerByCodeBank(String code_Bank) {
		return customerRepository.findAll().filter(x -> x.getBank().getCode().equals(code_Bank));
	}
	
}
