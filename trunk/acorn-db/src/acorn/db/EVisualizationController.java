/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package acorn.db;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

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

    public void removeVisualization(String visName) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            EVisualization visualization = (EVisualization) em.createNamedQuery("EVisualization.getByName").setParameter("name", visName).getSingleResult();
            em.remove(visualization);
            em.getTransaction().commit();
        }catch(NoResultException ex){
            return;
        }
        finally {
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

    public List<EVisualization> getAllVisualizations() {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            List<EVisualization> vl = em.createNamedQuery("EVisualization.getAllVisualizations").getResultList();
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

    public boolean isVisualizationNameUsed(String visName) {
        List<EVisualization> visualizations = getAllVisualizations();

        for (EVisualization vis : visualizations) {
            if (vis.getName().equals(visName)) {
                return true;
            }
        }
        return false;
    }

    public List<EVisualization> getModelVisualizations(EModel model) {
        EntityManager em = getEntityManager();
        try {
            return em.createNamedQuery("EVisualization.getVisualizationsByModel").setParameter("model", model).getResultList();
        } finally {
            em.close();
        }
    }

    /**
     *
     * @param modelName
     * @return visualizations connected to modelName model or its descendants
     */
    public List<EVisualization> getDescVisualizations(String modelName) {
        EntityManager em = getEntityManager();
        EModelController mc = new EModelController();

        List<EVisualization> visualizations = new ArrayList<EVisualization>(0);
        EModel model = null;
        try {
            model = mc.getModelByName(modelName);
        } catch (NoResultException ex) {
            return visualizations;
        }
        List<EModel> models = new ArrayList<EModel>(1);
        models.add(model);
        try {
            while (!models.isEmpty()) {
                for (EModel mod : models) {
                    visualizations.addAll(getModelVisualizations(mod));
                }
                models = mc.getChildrenByModelList(models);
            }
            return visualizations;
        } finally {
            em.close();
        }
    }
}
