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
public class EVisPlaceController  extends EntityController {
    
     public EVisPlace getPlaceByName(String name) {
        EntityManager em = getEntityManager();
        EVisPlace v = (EVisPlace) em.createNamedQuery("EVisualization.getByName").setParameter("name", name).getSingleResult();
        return v;
    }
    
}
