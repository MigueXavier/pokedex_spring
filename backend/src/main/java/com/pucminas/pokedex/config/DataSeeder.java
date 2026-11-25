package com.pucminas.pokedex.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.pucminas.pokedex.model.Pokemon;
import com.pucminas.pokedex.model.PokemonType;
import com.pucminas.pokedex.model.Trainer;
import com.pucminas.pokedex.repository.PokemonRepository;
import com.pucminas.pokedex.repository.PokemonTypeRepository;
import com.pucminas.pokedex.repository.TrainerRepository;

/**
 * Popula o MongoDB na primeira execução com os dados do Script_Pokedex.sql
 * (convertidos para CSV em resources/seed por scripts/gerar_seed.py).
 * Só insere se a coleção estiver vazia, então não duplica nem sobrescreve nada.
 */
@Component
@ConditionalOnProperty(name = "pokedex.seed.enabled", havingValue = "true", matchIfMissing = true)
public class DataSeeder implements CommandLineRunner {

	private final PokemonRepository pokemons;
	private final PokemonTypeRepository types;
	private final TrainerRepository trainers;

	public DataSeeder(PokemonRepository pokemons, PokemonTypeRepository types, TrainerRepository trainers) {
		this.pokemons = pokemons;
		this.types = types;
		this.trainers = trainers;
	}

	@Override
	public void run(String... args) throws IOException {
		if (types.count() == 0) {
			types.saveAll(ler("seed/types.csv", c -> new PokemonType(Integer.valueOf(c[0]), c[1])));
		}
		if (pokemons.count() == 0) {
			pokemons.saveAll(ler("seed/pokemons.csv", this::paraPokemon));
		}
		if (trainers.count() == 0) {
			trainers.saveAll(ler("seed/trainers.csv", this::paraTreinador));
		}
	}

	private Pokemon paraPokemon(String[] c) {
		Pokemon p = new Pokemon();
		p.setId(Integer.valueOf(c[0]));
		p.setName(c[1]);
		p.setHp(Integer.valueOf(c[2]));
		p.setAttack(Integer.valueOf(c[3]));
		p.setDefense(Integer.valueOf(c[4]));
		p.setSpecialAttack(Integer.valueOf(c[5]));
		p.setSpecialDefense(Integer.valueOf(c[6]));
		p.setSpeed(Integer.valueOf(c[7]));
		p.setTypes(c.length > 8 && !c[8].isBlank() ? Arrays.asList(c[8].split("\\|")) : new ArrayList<>());
		return p;
	}

	private Trainer paraTreinador(String[] c) {
		List<Integer> ids = new ArrayList<>();
		if (c.length > 3 && !c[3].isBlank()) {
			for (String id : c[3].split("\\|")) {
				ids.add(Integer.valueOf(id));
			}
		}
		return new Trainer(Integer.valueOf(c[0]), c[1], Integer.valueOf(c[2]), ids);
	}

	private <T> List<T> ler(String caminho, Function<String[], T> conversor) throws IOException {
		List<T> resultado = new ArrayList<>();
		try (BufferedReader leitor = new BufferedReader(
				new InputStreamReader(new ClassPathResource(caminho).getInputStream(), StandardCharsets.UTF_8))) {
			leitor.readLine(); // cabeçalho
			String linha;
			while ((linha = leitor.readLine()) != null) {
				if (!linha.isBlank()) {
					resultado.add(conversor.apply(linha.split(",", -1)));
				}
			}
		}
		return resultado;
	}
}
