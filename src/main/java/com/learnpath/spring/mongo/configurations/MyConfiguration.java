package com.learnpath.spring.mongo.configurations;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@Configuration
@EnableReactiveMongoRepositories(basePackages = "com.learnpath.spring.mongo.repositories")
public class MyConfiguration extends AbstractReactiveMongoConfiguration {

    @Override
    protected String getDatabaseName() {
        return "mydatabase";
    }
    @Value("${spring.data.mongodb.uri}")
    String mongoUri;

    @Override
    public MongoClient reactiveMongoClient() {
        return MongoClients.create(mongoUri);
    }

}
