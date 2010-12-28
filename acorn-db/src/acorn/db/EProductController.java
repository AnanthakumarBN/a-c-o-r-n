package acorn.db;

import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;

/**
 * EProductController
 * @author lukasz
 */
public class EProductController extends EntityController {

    /**
     * Returns list of all products binded to @reaction.
     * @return - list of all products binded to @reaction
     */
    public List<EProduct> getProducts(EReaction reaction) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            List<EProduct> products = (List<EProduct>) em.createQuery("SELECT r FROM EProduct r WHERE r.reaction = :reaction").
                    setParameter("reaction", reaction).getResultList();
            em.getTransaction().commit();
            return products;
        } finally {
            em.close();
        }
    }

    /**
     * Adds @product.
     */
    public void addProduct(EProduct product) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(product);
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
            reactions = em.createNamedQuery("EProduct.getReactionBySpecies").
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
            List<EReaction> reactions = em.createNamedQuery("EProduct.getReactionBy2Species").
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
            List<ESpecies> species = em.createNamedQuery("EProduct.getSpeciesByReaction").
                    setParameter("reaction", reaction).getResultList();
            em.getTransaction().commit();
            return species;
        } finally {
            em.close();
        }
    }
}
