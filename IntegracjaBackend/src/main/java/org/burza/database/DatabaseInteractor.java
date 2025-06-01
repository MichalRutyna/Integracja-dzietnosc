package org.burza.database;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.Query;
import org.burza.soap_api.DataPortImpl;
import org.burza.soap_api.HelperModel;

import java.util.ArrayList;
import java.util.List;
import java.util.ArrayList;

//nazwa bazy - jpa
public class DatabaseInteractor {
    public static void save(ArrayList<HelperModel> models)
    {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("Hibernate_JPA");
        EntityManager em = factory.createEntityManager();
        em.getTransaction().begin();
        for(HelperModel model : models){
            em.persist(model);
        }
        em.getTransaction().commit();
        em.close();
        factory.close();
    }

    public static List get(String dataset)
    {
        try {
            EntityManagerFactory factory =
                    Persistence.createEntityManagerFactory("Hibernate_JPA");
            EntityManager em = factory.createEntityManager();
            em.getTransaction().begin();
            Query query = em.createQuery("SELECT u FROM HelperModel u WHERE u.dataset = :dataset");
            query.setParameter("dataset", dataset);
            List data = query.getResultList();
            em.getTransaction().commit();
            em.close();
            factory.close();
            System.out.println("Fetched " + data);
            return data;
        } catch (Exception e) {
            System.err.println("Database get failed");
            return null;
        }

    }

    public static ArrayList<String> getAvailableDatasets() {
        EntityManagerFactory factory =
                Persistence.createEntityManagerFactory("Hibernate_JPA");
        EntityManager em = factory.createEntityManager();
        em.getTransaction().begin();
        Query query = em.createQuery("SELECT DISTINCT dataset FROM HelperModel");
        List data = query.getResultList();
        em.getTransaction().commit();
        em.close();
        factory.close();
        System.out.println("Fetched " + data);
        try {
            return (ArrayList<String>) data;
        }
        catch (Exception e) {
            System.err.println("Database returned non-string data");
            return null;
        }
    }
}
