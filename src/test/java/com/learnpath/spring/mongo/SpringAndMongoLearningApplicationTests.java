package com.learnpath.spring.mongo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SpringAndMongoLearningApplicationTests {


	@Test
/**
 * Questo è un file di test di una classe Java del progetto spring.mongo.
 * La classe di test utilizza il framework di testing JUnit 5 per testare il caricamento del contesto
 * dell'applicazione Spring Boot. La classe di test è annotata con @SpringBootTest, che indica che viene
 * utilizzato il contesto completo dell'applicazione Spring Boot per il test. Il metodo contextLoads() è
 * un test di base che verifica che il contesto dell'applicazione venga caricato correttamente.
 * In questo caso, il test verifica che la stringa "a" è uguale a "a".
 */
	void contextLoads() {
		Assertions.assertEquals("a","a");
	}

}
