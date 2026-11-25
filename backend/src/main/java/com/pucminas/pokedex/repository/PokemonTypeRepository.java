package com.pucminas.pokedex.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.pucminas.pokedex.model.PokemonType;

public interface PokemonTypeRepository extends MongoRepository<PokemonType, Integer> {
}
