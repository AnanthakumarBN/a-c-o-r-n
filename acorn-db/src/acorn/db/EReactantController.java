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

    public List<EReaction> getReactions(ESpecies spec){
        EntityManager em = getEntityManager();
        try{
            return em.createNamedQuery("EReactant.getReactionBySpecies").
                    setParameter("spec1", spec).getResultList();
        }finally{
        }
    }

    public List<EReaction> getReactions(ESpecies spec1, ESpecies spec2){
        EntityManager em = getEntityManager();
        try{
            return em.createNamedQuery("EReactant.getReactionBy2Species").
                    setParameter("spec1", spec1).setParameter("spec2",spec2).getResultList();
        }finally{
        }
    }

    public List<ESpecies> getSpecies(EReaction reaction){
        EntityManager em = getEntityManager();
        try{
            return em.createNamedQuery("EReactant.getSpeciesByReaction").
                    setParameter("reaction", reaction).getResultList();
        }finally{
        }
    }
}
