package com.newgen.tgv;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
    info = @Info(
        title = "NewGen TGV API",
        version = "1.0",
        description = "REST API for NewGen TGV train booking, seat selection, and ticketing"
    )
)
@SpringBootApplication
public class NewGenTgvApplication {

    public static void main(String[] args) {
        SpringApplication.run(NewGenTgvApplication.class, args);
    }

}
