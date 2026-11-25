package com.pucminas.pokedex.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.pucminas.pokedex.model.Trainer;

public interface TrainerRepository extends MongoRepository<Trainer, Integer> {
}
