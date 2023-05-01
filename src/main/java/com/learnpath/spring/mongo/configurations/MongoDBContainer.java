package com.learnpath.spring.mongo.configurations;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.PullImageResultCallback;
import com.learnpath.spring.mongo.models.Animal;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoDatabase;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Configuration
@EnableReactiveMongoRepositories(basePackages = "com.learnpath.spring.mongo.repositories")
public class MongoDBContainer implements DisposableBean {

    private static final String MONGO_IMAGE_NAME = "mongo:latest";
    private static final String MONGO_CONTAINER_NAME = "mongo-container";
    private static final String MONGO_DB_NAME = "learnpath";
    private static final int MONGO_INTERNAL_PORT = 27017;
    private static final int MONGO_EXTERNAL_PORT = 27017;

    private final DockerClient dockerClient;
    private final CreateContainerResponse mongoContainer ;
    private final MongoClient mongoClient ;

    public MongoDBContainer() throws InterruptedException {

        this.dockerClient = DockerClientBuilder.getInstance().build();
        this.mongoContainer = startMongoContainer();
        this.mongoClient = createMongoClient();
        log.info("MongoDB container started successfully");
    }

    private CreateContainerResponse startMongoContainer() throws InterruptedException {
        log.info("Pulling MongoDB image...");
        dockerClient.pullImageCmd(MONGO_IMAGE_NAME).exec(new PullImageResultCallback()).awaitSuccess();
        log.info("MongoDB image pulled successfully");

        log.info("Creating MongoDB container...");
        ExposedPort tcp = ExposedPort.tcp(MONGO_INTERNAL_PORT);
        Ports portBindings = new Ports();
        portBindings.bind(tcp, Ports.Binding.bindPort(MONGO_EXTERNAL_PORT));
        CreateContainerCmd createCommand = dockerClient.createContainerCmd(MONGO_IMAGE_NAME)
                .withName(MONGO_CONTAINER_NAME)
                .withExposedPorts(tcp)
                .withPortBindings(portBindings)
                .withEnv("MONGO_INITDB_DATABASE=" + MONGO_DB_NAME);
        CreateContainerResponse container = createCommand.exec();
        log.info("MongoDB container created successfully");

        log.info("Starting MongoDB container...");
        dockerClient.startContainerCmd(container.getId()).exec();
        waitForMongoDBToBeReady();
        log.info("MongoDB container started successfully");

        return container;
    }

    private MongoClient createMongoClient() {
        List<Container> containers = dockerClient.listContainersCmd()
                .withNameFilter(Arrays.asList(MONGO_CONTAINER_NAME))
                .exec();
        if (containers.isEmpty()) {
            throw new DockerException("MongoDB container not found",400);
        }

        String containerId = containers.get(0).getId();
        String containerIpAddress = dockerClient.inspectContainerCmd(containerId)
                .exec().getNetworkSettings().getIpAddress();
        log.info("MongoDB container IP address: {}", containerIpAddress);

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyToClusterSettings(builder ->
                        builder.hosts(Arrays.asList(new ServerAddress(containerIpAddress, MONGO_EXTERNAL_PORT))))
                .build();

        MongoClient mongoClient = MongoClients.create(settings);
        MongoDatabase database = mongoClient.getDatabase(MONGO_DB_NAME);
        database.createCollection("animals");

        List<Animal> animals = Arrays.asList(
                Animal.builder().name("Lion").age(10).height(1.2).build(),
                Animal.builder().name("Elephant").age(20).height(3.5).build(),
                Animal.builder().name("Giraffe").age(5).height(5.0).build(),
                Animal.builder().name("Monkey").age(3).height(0.5).build(),
                Animal.builder().name("Penguin").age(2).height(0.7).build()
        );

        database.getCollection("animals").insertMany(animals.stream()
                .map(animal -> new Document()
                        .append("name", animal.getName())
                        .append("age", animal.getAge())
                        .append("height", animal.getHeight()))
                .collect(Collectors.toList()));
        log.info("MongoDB client created successfully");

        return mongoClient;
    }


    private void waitForMongoDBToBeReady() throws InterruptedException {
        boolean isMongoDBReady = false;
        int retries = 0;
        while (!isMongoDBReady && retries < 30) {
            try {
                mongoClient.getDatabase(MONGO_DB_NAME).runCommand(new Document("ping", "1"));
                isMongoDBReady = true;
            } catch (Exception e) {
                log.info("Waiting for MongoDB to be ready...");
                TimeUnit.SECONDS.sleep(1);
                retries++;
            }
        }
        if (!isMongoDBReady) {
            throw new RuntimeException("Could not connect to MongoDB");
        }
    }


    @Override
    public void destroy() throws Exception {
        if (mongoClient != null) {
            mongoClient.close();
            log.info("MongoDB client closed successfully");
        }

        if (mongoContainer != null) {
            dockerClient.stopContainerCmd(mongoContainer.getId()).exec();
            log.info("MongoDB container stopped successfully");

            dockerClient.removeContainerCmd(mongoContainer.getId()).exec();
            log.info("MongoDB container removed successfully");
        }

        dockerClient.close();
        log.info("Docker client closed successfully");
    }

}
