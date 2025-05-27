package org.burza.soap_api;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.Query;

import java.util.ArrayList;
import java.util.List;

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
        if(DataPortImpl.AVAILABLE_DATASETS.contains(dataset)){
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
            return data;
        }
        else{
            return null;
        }

    }
}
