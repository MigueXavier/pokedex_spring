package com.pucminas.pokedex.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pucminas.pokedex.model.Pokemon;
import com.pucminas.pokedex.service.PokemonService;

@RestController
@RequestMapping("/api/pokemons")
public class PokemonController {

	private final PokemonService service;

	public PokemonController(PokemonService service) {
		this.service = service;
	}

	@GetMapping
	public List<Pokemon> listar(@RequestParam(required = false) String name,
			@RequestParam(required = false) String type) {
		return service.listar(name, type);
	}

	@GetMapping("/{id}")
	public Pokemon buscar(@PathVariable Integer id) {
		return service.buscar(id);
	}

	@PostMapping
	public ResponseEntity<Pokemon> criar(@RequestBody Pokemon pokemon) {
		return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(pokemon));
	}

	@PutMapping("/{id}")
	public Pokemon atualizar(@PathVariable Integer id, @RequestBody Pokemon pokemon) {
		return service.atualizar(id, pokemon);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> remover(@PathVariable Integer id) {
		service.remover(id);
		return ResponseEntity.noContent().build();
	}
}
