package com.nttdata.semana01.Repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.nttdata.semana01.Entity.CustomerType;

@Repository
public interface CustomerTypeRepository extends ReactiveCrudRepository<CustomerType, Integer>{
	
}
