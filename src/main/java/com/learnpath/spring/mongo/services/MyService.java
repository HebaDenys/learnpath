package com.learnpath.spring.mongo.services;

import com.learnpath.spring.mongo.models.Animal;
import com.learnpath.spring.mongo.repositories.AnimalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MyService {

    @Autowired
    private AnimalRepository repository;

    public Mono<Animal> createAnimal(Animal animal) {
        return repository.save(animal);
    }

    public Mono<Animal> getAnimalById(String id) {
        return repository.findById(id);
    }

    public Flux<Animal> getAllAnimals() {
        return repository.findAll();
    }

    public Mono<Animal> updateAnimal(String id, Animal animal) {
        return repository.findById(id)
                .flatMap(existingAnimal -> {
                    existingAnimal.setName(animal.getName());
                    existingAnimal.setAge(animal.getAge());
                    existingAnimal.setHeight(animal.getHeight());
                    return repository.save(existingAnimal);
                });
    }

    public Mono<Void> deleteAnimal(String id) {
        return repository.deleteById(id);
    }

}
