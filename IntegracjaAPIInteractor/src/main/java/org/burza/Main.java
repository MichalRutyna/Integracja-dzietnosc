package org.burza;

import com.example.generated.SaveDataResponse;
import org.burza.api_interactors.ApiSDPInteractor;
import org.burza.soap_client.Client;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

@SpringBootApplication
public class Main {
    public static void suitableVariables() throws IOException, InterruptedException {
//        for (String o : ApiSDPInteractor.getSuitableVariables(Set.of(282), null)) {
        for (String o : ApiSDPInteractor.getSuitableVariables(Set.of(282), Set.of(2, 155))) {
            System.out.println(o);
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
//        suitableVariables();
        SpringApplication.run(Main.class, args);

    }

//    @Bean
//    CommandLineRunner test(Client client) {
//        return args -> {
//            SaveDataResponse response = client.postData("testest", new ArrayList<>());
//            System.err.println(response.getMessage());
//        };
//    }
}