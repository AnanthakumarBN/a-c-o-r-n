/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package acorn.db;

import javax.persistence.EntityManager;

/**
 *
 * @author Mateusza
 */
public class EVisPlaceController extends EntityController {

    public EVisPlace getPlaceByName(String name) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            EVisPlace v = (EVisPlace) em.createNamedQuery("EVisualization.getByName").setParameter("name", name).getSingleResult();
            em.getTransaction().commit();
            return v;
        } finally {
            em.close();
        }
    }
}
