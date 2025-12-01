package com.farmacontrol;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ServletComponentScan(basePackages = {"routes"})
@ComponentScan(basePackages = {"com.farmacontrol", "config"})
public class FarmaControlApplication {

    public static void main(String[] args) {
        SpringApplication.run(FarmaControlApplication.class, args);
    }
}
