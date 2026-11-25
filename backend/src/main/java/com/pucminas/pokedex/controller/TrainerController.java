package com.pucminas.pokedex.controller;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.pucminas.pokedex.model.Trainer;
import com.pucminas.pokedex.repository.TrainerRepository;

@RestController
@RequestMapping("/api/trainers")
public class TrainerController {

	private final TrainerRepository repository;

	public TrainerController(TrainerRepository repository) {
		this.repository = repository;
	}

	@GetMapping
	public List<Trainer> listar() {
		return repository.findAll(Sort.by("id"));
	}

	@GetMapping("/{id}")
	public Trainer buscar(@PathVariable Integer id) {
		return repository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Treinador não encontrado: " + id));
	}
}
