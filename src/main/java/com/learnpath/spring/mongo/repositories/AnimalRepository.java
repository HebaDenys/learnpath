package com.learnpath.spring.mongo.repositories;

import com.learnpath.spring.mongo.models.Animal;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface AnimalRepository extends ReactiveMongoRepository<Animal, String> {
    Flux<Animal> findByName(String name);
}
