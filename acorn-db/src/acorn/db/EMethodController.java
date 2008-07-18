package acorn.db;

import java.util.List;
import javax.persistence.EntityManager;

/**
 * EMethodController
 * @author lukasz
 */
public class EMethodController extends EntityController {

    /**
     * Adds @method.
     */
    public void addMethod(EMethod method) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(method);
            em.getTransaction().commit();
        } finally {
            em.close();
        }  
    }
    
    /**
     * Merges @method.
     */
    public void mergeMethod(EMethod method) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(method);
            em.getTransaction().commit();
        } finally {
            em.close();
        }  
    }
    
    public List<EMethod> findByIdent(String ident) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            List<EMethod> methodx = em.createNamedQuery("EMethod.findByIdent").
                    setParameter("ident", ident).
                    getResultList();
            em.getTransaction().commit();
            return methodx;
        } finally {
            em.close();
        } 
    }
    
}
