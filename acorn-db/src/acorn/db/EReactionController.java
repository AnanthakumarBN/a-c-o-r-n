package acorn.db;

import javax.persistence.EntityManager;

/**
 * EReactionController
 * @author lukasz
 */
public class EReactionController extends EntityController {

    /**
     * Adds @reaction.
     */
    public void addReaction(EReaction reaction) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(reaction);
            em.getTransaction().commit();
        } finally {
            em.close();
        }  
    }
}