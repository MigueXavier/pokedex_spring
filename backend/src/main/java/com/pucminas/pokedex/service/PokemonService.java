package com.pucminas.pokedex.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.pucminas.pokedex.model.Pokemon;
import com.pucminas.pokedex.repository.PokemonRepository;

@Service
public class PokemonService {

	private final PokemonRepository repository;

	public PokemonService(PokemonRepository repository) {
		this.repository = repository;
	}

	/** Lista todos os Pokémon; name (contém, sem diferenciar maiúsculas) e type (exato) são filtros opcionais. */
	public List<Pokemon> listar(String name, String type) {
		String nome = name == null ? "" : name.trim().toLowerCase();
		String tipo = type == null ? "" : type.trim();
		return repository.findAll(Sort.by("id")).stream()
				.filter(p -> nome.isEmpty() || p.getName().toLowerCase().contains(nome))
				.filter(p -> tipo.isEmpty() || p.getTypes().stream().anyMatch(t -> t.equalsIgnoreCase(tipo)))
				.toList();
	}

	public Pokemon buscar(Integer id) {
		return repository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pokémon não encontrado: " + id));
	}

	public Pokemon criar(Pokemon pokemon) {
		validar(pokemon);
		if (pokemon.getId() == null) {
			int proximo = repository.findTopByOrderByIdDesc().map(p -> p.getId() + 1).orElse(1);
			pokemon.setId(proximo);
		} else if (repository.existsById(pokemon.getId())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um Pokémon com o id " + pokemon.getId());
		}
		return repository.save(pokemon);
	}

	public Pokemon atualizar(Integer id, Pokemon pokemon) {
		buscar(id);
		validar(pokemon);
		pokemon.setId(id);
		return repository.save(pokemon);
	}

	public void remover(Integer id) {
		buscar(id);
		repository.deleteById(id);
	}

	private void validar(Pokemon p) {
		if (p.getName() == null || p.getName().isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O nome é obrigatório");
		}
		p.setName(p.getName().trim());
		checarStat("hp", p.getHp());
		checarStat("attack", p.getAttack());
		checarStat("defense", p.getDefense());
		checarStat("specialAttack", p.getSpecialAttack());
		checarStat("specialDefense", p.getSpecialDefense());
		checarStat("speed", p.getSpeed());
		if (p.getTypes().size() > 2) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Um Pokémon pode ter no máximo 2 tipos");
		}
	}

	private void checarStat(String campo, Integer valor) {
		if (valor == null || valor < 0 || valor > 255) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, campo + " deve estar entre 0 e 255");
		}
	}
}
