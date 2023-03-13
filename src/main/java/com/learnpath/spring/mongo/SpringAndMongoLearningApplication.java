package com.learnpath.spring.mongo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringAndMongoLearningApplication {
	/**
	 * Questa è la classe principale del progetto spring.mongo.
	 * La classe è annotata con @SpringBootApplication, che indica che si tratta di un'applicazione Spring Boot.
	 * Il metodo main() è il punto di ingresso dell'applicazione e utilizza il metodo run() della classe
	 * SpringApplication per avviare l'applicazione Spring Boot. In questo modo, l'applicazione utilizza il contesto
	 * dell'applicazione Spring Boot per gestire le dipendenze e le configurazioni dell'applicazione.
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(SpringAndMongoLearningApplication.class, args);
	}

}
