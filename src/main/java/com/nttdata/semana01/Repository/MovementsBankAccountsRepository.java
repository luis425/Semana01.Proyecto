package com.nttdata.semana01.Repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.nttdata.semana01.Entity.MovementsBankAccounts;

@Repository
public interface MovementsBankAccountsRepository extends ReactiveCrudRepository<MovementsBankAccounts, String> {

}
