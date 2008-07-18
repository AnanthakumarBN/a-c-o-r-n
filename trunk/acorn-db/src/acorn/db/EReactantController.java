package acorn.db;

import java.util.List;
import javax.persistence.EntityManager;

/**
 * EReactantController
 * @author lukasz
 */
public class EReactantController extends EntityController {

    /**
     * Returns list of all reactants binded to @reaction.
     * @return - list of all reactants binded to @reaction
     */
    public List<EReactant> getReactants(EReaction reaction) {
        EntityManager em = getEntityManager();
        try {
            return (List<EReactant>) em.createQuery("SELECT r FROM EReactant r WHERE r.reaction = :reaction").
                        setParameter("reaction", reaction).getResultList();
        } finally {
            em.close();
        }
    }
    
    /**
     * Adds @reactant.
     */
    public void addReactant(EReactant reactant) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(reactant);
            em.getTransaction().commit();
        } finally {
            em.close();
        }  
    }
}
