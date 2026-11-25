package com.pucminas.pokedex;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "pokedex.seed.enabled=false")
class PokedexApplicationTests {

	@Test
	void contextLoads() {
	}

}
