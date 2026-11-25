package com.pucminas.pokedex.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.pucminas.pokedex.model.Pokemon;

public interface PokemonRepository extends MongoRepository<Pokemon, Integer> {

	Optional<Pokemon> findTopByOrderByIdDesc();
}
