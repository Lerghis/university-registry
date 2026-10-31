package com.university.registry;

import com.university.registry.web.PingController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RegistryApplication
{
    public static void main(String[] args)
    {
        // is the Spring version of what Main does by hand. It creates the container, builds every object, and starts Tomcat.
        SpringApplication.run(RegistryApplication.class, args);
    }
}
