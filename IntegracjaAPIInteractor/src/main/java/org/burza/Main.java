package org.burza;

import org.burza.models.RegionYearValueObj;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        EntityManagerFactory factory =
                Persistence.createEntityManagerFactory("Hibernate_JPA");
        EntityManager em = factory.createEntityManager();
        em.getTransaction().begin();
        RegionYearValueObj u1 = new RegionYearValueObj(null, "Rzeszów",2014, 10000.0);

        em.persist(u1);
        em.getTransaction().commit();
        em.close();
        factory.close();
        SpringApplication.run(Main.class, args);
    }
}