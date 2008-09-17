/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package acorn.db;

import java.util.List;
import javax.persistence.EntityManager;

/**
 *
 * @author Mateusza
 */
public class EVisualizationController extends EntityController {

    public void addVisualization(EVisualization vis) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(vis);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public EVisualization getVisualizationByName(String name) {
        EntityManager em = getEntityManager();
        EVisualization v = (EVisualization) em.createNamedQuery("EVisualization.getByName").setParameter("name", name).getSingleResult();
        return v;
    }

    public List<EVisualization> getVisualization(EModel model) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            List<EVisualization> vl = em.createNamedQuery("EVisualization.getByModel").
                    setParameter("model", model).
                    getResultList();
            em.getTransaction().commit();
            return vl;
        } finally {
            em.close();
        }
    }

    public List<EVisPlace> getPlaces(Long visId) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            List<EVisPlace> vl = em.createNamedQuery("EVisualization.getPlaces").
                    setParameter("id", visId).
                    getResultList();
            em.getTransaction().commit();
            return vl;
        } finally {
            em.close();
        }
    }

    public List<EVisTransition> getTransitions(Long visId) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            List<EVisTransition> vl = em.createNamedQuery("EVisualization.getTransitions").
                    setParameter("id", visId).
                    getResultList();
            em.getTransaction().commit();
            return vl;
        } finally {
            em.close();
        }
    }

    public List<EVisArcProduct> getArcProducts(Long visId) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            List<EVisArcProduct> vl = em.createNamedQuery("EVisualization.getArcProducts").
                    setParameter("id", visId).
                    getResultList();
            em.getTransaction().commit();
            return vl;
        } finally {
            em.close();
        }
    }
        
    public List<EVisArcReactant> getArcReactants(Long visId) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            List<EVisArcReactant> vl = em.createNamedQuery("EVisualization.getArcResources").
                    setParameter("id", visId).
                    getResultList();
            em.getTransaction().commit();
            return vl;
        } finally {
            em.close();
        }
    }
}
