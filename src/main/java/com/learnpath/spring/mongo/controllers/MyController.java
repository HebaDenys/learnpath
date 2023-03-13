package com.learnpath.spring.mongo.controllers;

import com.learnpath.spring.mongo.models.Animal;
import com.learnpath.spring.mongo.services.MyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/animals")
public class MyController {

    @Autowired
    private MyService service;

    @PostMapping
    public Mono<ResponseEntity<Animal>> createAnimal(@RequestBody Animal animal) {
        return service.createAnimal(animal)
                .map(savedAnimal -> new ResponseEntity<>(savedAnimal, HttpStatus.CREATED));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Animal>> getAnimalById(@PathVariable String id) {
        return service.getAnimalById(id)
                .map(animal -> new ResponseEntity<>(animal, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public Flux<Animal> getAllAnimals() {
        return service.getAllAnimals();
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Animal>> updateAnimal(@PathVariable String id, @RequestBody Animal animal) {
        return service.updateAnimal(id, animal)
                .map(updatedAnimal -> new ResponseEntity<>(updatedAnimal, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteAnimal(@PathVariable String id) {
        return service.deleteAnimal(id)
                .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)));
    }

}
