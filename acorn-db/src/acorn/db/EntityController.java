package acorn.db;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * EntityController
 * @author lukasz
 */
public class EntityController {
    private EntityManagerFactory emf;

    protected EntityManager getEntityManager() {
        if(emf == null){
            emf = Persistence.createEntityManagerFactory("acorn-dbPU");
        }
        return emf.createEntityManager();
    }
}

