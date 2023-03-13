package com.learnpath.spring.mongo.models;

//import lombok.Data;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document("animals")
public class Animal {

    @Id
    private String id;
    private String name;
    private int age;
    private double height;
}
