package com.grocery;

import java.util.Map;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Configuration
@Profile("dev")
public class DevRunner {
	
	public CommandLineRunner run(){
        return args -> System.out.println("Hello from Dev env!");
    }

}
