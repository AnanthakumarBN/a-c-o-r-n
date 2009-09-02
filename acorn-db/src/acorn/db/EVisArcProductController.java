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
public class EVisArcProductController extends EntityController{

    public void removeEVisArcProduct(EVisArcProduct prod){
        EntityManager em = getEntityManager();

        try{
            em.getTransaction().begin();
            EVisArcProduct prodToRemove = em.find(EVisArcProduct.class, prod.getId());
            if(prodToRemove != null){
                em.remove(prodToRemove);
            }

            em.getTransaction().commit();
        }finally{
            em.close();
        }
    }

    public void removeEvisArcProducts(EVisualization visualization, EReaction reaction){
        EntityManager em = getEntityManager();

        try{
            em.getTransaction().begin();
            Collection<EVisArcProduct> arcProducts = em.createNamedQuery("EVisArcProduct.getByReaction").setParameter("reaction", reaction).setParameter("visualization", visualization).getResultList();
            em.getTransaction().commit();

            for(EVisArcProduct prod : arcProducts){
                removeEVisArcProduct(prod);
            }
        }finally{
            em.close();
        }
    }
}
