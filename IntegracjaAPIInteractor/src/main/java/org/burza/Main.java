package org.burza;

import com.example.generated.SaveDataResponse;
import org.burza.soap_client.Client;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    CommandLineRunner test(Client client) {
        return args -> {
            String country = "Spain";

            if (args.length > 0) {
                country = args[0];
            }
            SaveDataResponse response = client.postData(country);
            System.err.println(response.getMessage());
        };
    }
}