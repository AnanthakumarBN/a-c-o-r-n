package acorn.db;

import javax.persistence.EntityManager;

/**
 * EMetabolismController
 * @author lukasz
 */
public class EMetabolismController extends EntityController {
    
    /**
     * Adds @metabolism.
     */
    public void addMetabolism(EMetabolism metabolism) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(metabolism);
            em.getTransaction().commit();
        } finally {
            em.close();
        }  
    }
    
    /**
     * Merges @metabolism.
     */
    public void mergeMetabolism(EMetabolism metabolism) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(metabolism);
            em.getTransaction().commit();
        } finally {
            em.close();
        }  
    }

}
