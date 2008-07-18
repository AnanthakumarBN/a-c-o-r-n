package acorn.db;

import javax.persistence.EntityManager;

/**
 * ECompartmentController
 * @author lukasz
 */
public class ECompartmentController extends EntityController {

    /**
     * Adds @compartment.
     */
    public void addCompartment(ECompartment compartment) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(compartment);
            em.getTransaction().commit();
        } finally {
            em.close();
        }  
    }
}
