package acorn.db;

import java.util.Collection;
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
            em.getTransaction().begin();
            List<EReactant> reactants = (List<EReactant>) em.createQuery("SELECT r FROM EReactant r WHERE r.reaction = :reaction").
                    setParameter("reaction", reaction).getResultList();
            em.getTransaction().commit();
            return reactants;
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

    public List<EReaction> getReactions(ESpecies spec, int modelId) {
        EntityManager em = getEntityManager();

        EModelController mc = new EModelController();
        ESpeciesController sc = new ESpeciesController();

        List<EReaction> reactions = null;
        try {
            em.getTransaction().begin();
            reactions = em.createNamedQuery("EReactant.getReactionBySpecies").
                    setParameter("spec1", spec).getResultList();
            em.getTransaction().commit();

        } finally {
            em.close();
        }
        Collection<EReaction> detachedReactions = mc.getDetachedReactions(modelId);
        reactions.removeAll(detachedReactions);
        return reactions;
    }

    public List<EReaction> getReactions(ESpecies spec1, ESpecies spec2) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            List<EReaction> reactions = em.createNamedQuery("EReactant.getReactionBy2Species").
                    setParameter("spec1", spec1).setParameter("spec2", spec2).getResultList();
            em.getTransaction().commit();
            return reactions;
        } finally {
            em.close();
        }
    }

    public List<ESpecies> getSpecies(EReaction reaction) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            List<ESpecies> species = em.createNamedQuery("EReactant.getSpeciesByReaction").
                    setParameter("reaction", reaction).getResultList();
            em.getTransaction().commit();
            return species;
        } finally {
            em.close();
        }
    }
}
