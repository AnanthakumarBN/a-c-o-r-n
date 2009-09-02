/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package acorn.db;

import javax.persistence.EntityManager;

/**
 *
 * @author markos
 */
public class EVisTransitionController extends EntityController {

    public void removeTransition(EVisTransition trans) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            EVisTransition transToRemove = em.find(EVisTransition.class, trans.getId());
            if (transToRemove != null) {
                em.remove(transToRemove);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }

    }
}
