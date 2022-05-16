package com.nttdata.semana01.Repository;

//import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.nttdata.semana01.Entity.Bank;
 

@Repository
public interface BankRepository extends ReactiveCrudRepository<Bank, String>{

}
