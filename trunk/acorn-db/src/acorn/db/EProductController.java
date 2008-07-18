package acorn.db;

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
            return (List<EProduct>) em.createQuery("SELECT r FROM EProduct r WHERE r.reaction = :reaction").
                        setParameter("reaction", reaction).getResultList();
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
}
