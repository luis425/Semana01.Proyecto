package com.nttdata.semana01.Repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.nttdata.semana01.Entity.Credits;

public interface CreditsRepository extends ReactiveCrudRepository<Credits, String>{

}
