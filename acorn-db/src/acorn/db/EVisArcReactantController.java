/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package acorn.db;

import java.util.Collection;
import javax.persistence.EntityManager;

/**
 *
 * @author markos
 */
public class EVisArcReactantController extends EntityController{

    public void removeEvisAcrReactant(EVisArcReactant react){
        EntityManager em = getEntityManager();

        try{
            em.getTransaction().begin();
            EVisArcReactant reactToRemove = em.find(EVisArcReactant.class, react.getId());
            if(reactToRemove != null){
                em.remove(reactToRemove);
            }
            em.getTransaction().commit();
        }finally{
            em.close();
        }
    }

    public void removeEVisArcReactants(EVisualization visualization, EReaction reaction){
        EntityManager em = getEntityManager();

        try{
            em.getTransaction().begin();
            Collection<EVisArcReactant> arcReactants = em.createNamedQuery("EVisArcReactant.getByReaction")
                    .setParameter("reaction", reaction).setParameter("visualization", visualization).getResultList();
            em.getTransaction().commit();
            for(EVisArcReactant react : arcReactants){
                removeEvisAcrReactant(react);
            }
        }finally{
            em.close();
        }
    }
}
