package com.company.tasksapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class TasksApiApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(TasksApiApplication.class, args);
    }
}
