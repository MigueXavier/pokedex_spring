package com.pucminas.pokedex.controller;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pucminas.pokedex.model.PokemonType;
import com.pucminas.pokedex.repository.PokemonTypeRepository;

@RestController
@RequestMapping("/api/types")
public class TypeController {

	private final PokemonTypeRepository repository;

	public TypeController(PokemonTypeRepository repository) {
		this.repository = repository;
	}

	@GetMapping
	public List<PokemonType> listar() {
		return repository.findAll(Sort.by("id"));
	}
}
