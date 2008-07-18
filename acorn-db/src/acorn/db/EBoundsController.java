package acorn.db;

import java.util.List;
import javax.persistence.EntityManager;

/**
 *  EBoundsController
 * @author lukasz
 */
public class EBoundsController extends EntityController {
     
    /**
     * Adds @bounds.
     */
    public void addBounds(EBounds bounds) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(bounds);
            em.getTransaction().commit();
        } finally {
            em.close();
        }  
    }
    
    /**
     * Returns list of all bounds binded to @model.
     * @return - list of all bounds binded to @model
     */
    public List<EBounds> getBounds(EModel model) {
        EntityManager em = getEntityManager();
        try {
            List<EBounds> bounds;
            
            bounds = em.createNamedQuery("EBounds.findByModel").
                    setParameter("model", model).
                    getResultList();
            if (model.getParent() != null) bounds.addAll(em.createNamedQuery("EBounds.findByParent").
                    setParameter("model", model).
                    setParameter("parent", model.getParent()).
                    getResultList());
            
            return bounds;
        } finally {
            em.close();
        }
    }
    
}
