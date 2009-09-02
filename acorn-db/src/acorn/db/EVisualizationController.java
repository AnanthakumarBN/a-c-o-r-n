/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package acorn.db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import org.visualapi.VisEdge;
import org.visualapi.VisPlace;
import org.visualapi.VisTransition;

/**
 *
 * @author Mateusz
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

    public List<VisEdge> getEdgesOfVisualization(String visName) {
        EVisualization vis = null;
        ETaskController eTaskController = new ETaskController();
        EModelController eModelController = new EModelController();

        EntityManager em = getEntityManager();
        try {
            vis = getVisualizationByName(visName);
        } catch (NoResultException ex) {
            return null;
        }

        List<EVisPlace> places = getPlaces(vis.getId());
        List<EVisTransition> transitions = getTransitions(vis.getId());
        List<EVisArcProduct> arcProducts = getArcProducts(vis.getId());
        List<EVisArcReactant> arcResources = getArcReactants(vis.getId());

        HashMap<String, VisPlace> placesMap = new HashMap<String, VisPlace>(0);
        HashMap<String, VisTransition> transitionMap = new HashMap<String, VisTransition>(0);
        EModel model = null;
        ETask task = null;

        try {
            em.getTransaction().begin();
            model = vis.getModel();
            task = model.getTask();
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        boolean isDoneAndFba = (eModelController.isDoneTask(model.getId()) && eModelController.isFbaTask(model.getId()));
        List<VisEdge> edges = new ArrayList<VisEdge>(0);

        // VisPlaces creation
        for (EVisPlace epl : places) {
            VisPlace pl = new VisPlace(epl.getSpeciesName(), epl.getSpeciesSid(), epl.getPosition(), epl.getXmlSid());
            placesMap.put(pl.getXmlSid(), pl);
        }

        // VisTransition creation
        for (EVisTransition etrans : transitions) {
            float flux = 0;
            if (isDoneAndFba) {
                flux = eTaskController.getFlux(task, etrans.getReactionSid());
            }
            VisTransition trans = new VisTransition(etrans.getReactionName(), etrans.getReactionSid(), etrans.getPosition(), etrans.getXmlSid(), flux);
            transitionMap.put(trans.getXmlSid(), trans);
        }

        // VisEdge creation
        for (EVisArcReactant arcResource : arcResources) {
            VisPlace source = placesMap.get(arcResource.getSource().getXmlSid());
            VisTransition target = transitionMap.get(arcResource.getTarget().getXmlSid());
            edges.add(new VisEdge(source, target));
        }
        for (EVisArcProduct arcProduct : arcProducts) {
            VisPlace target = placesMap.get(arcProduct.getTarget().getXmlSid());
            VisTransition source = transitionMap.get(arcProduct.getSource().getXmlSid());
            edges.add(new VisEdge(source, target));
        }
        return edges;
    }

    public void removeVisualization(String visName) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            EVisualization visualization = (EVisualization) em.createNamedQuery("EVisualization.getByName").setParameter("name", visName).getSingleResult();
            em.remove(visualization);
            em.getTransaction().commit();
        } catch (NoResultException ex) {
        } finally {
            em.close();
        }
    }

    public EVisualization getVisualizationByName(String name) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            EVisualization v = (EVisualization) em.createNamedQuery("EVisualization.getByName").setParameter("name", name).getSingleResult();
            em.getTransaction().commit();
            return v;
        } finally {
            em.close();
        }
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
            em.getTransaction().begin();
            List<EVisualization> visualizations = em.createNamedQuery("EVisualization.getVisualizationsByModel").setParameter("model", model).getResultList();
            em.getTransaction().commit();
            return visualizations;
        } finally {
            em.close();
        }
    }

    /**
     *
     * @param modelName
     * @return visualizations connected to modelName model or its descendants
     */
    public List<EVisualization> getDescVisualizations(int modelId) {
//        EntityManager em = getEntityManager();
        EModelController mc = new EModelController();

        List<EVisualization> visualizations = new ArrayList<EVisualization>(0);
        EModel model = null;

        try {
            model = mc.getModel(modelId);
            List<EModel> models = new ArrayList<EModel>(1);
            models.add(model);
            while (!models.isEmpty()) {
                for (EModel mod : models) {
                    visualizations.addAll(getModelVisualizations(mod));
                }
                models = mc.getChildrenByModelList(models);
            }
            return visualizations;
        } catch (NoResultException ex) {
            return visualizations;
        } finally {
//            em.close();
        }
    }

    /**
     * update visualization by removing form visualization transitions (reactions) that are removed from model
     * @param modelId id of model for which reactions are removed
     */
    public void removeDetachedReactions(int modelId) {
        EModelController mc = new EModelController();
        EReactionController rc = new EReactionController();
        EntityManager em = getEntityManager();

        EVisArcReactantController reactC = new EVisArcReactantController();
        EVisArcProductController prodC = new EVisArcProductController();

        EVisTransitionController tc = new EVisTransitionController();

        try {
            em.getTransaction().begin();
            EModel model = mc.getModel(modelId);
            Collection<EVisualization> visualizationColl = model.getEVisualizations();
            Collection<EReaction> detachedReactions = mc.getDetachedReactions(modelId);
            em.getTransaction().commit();

            for (EVisualization vis : visualizationColl) {

                em.getTransaction().begin();
                Collection<EVisTransition> transColl = vis.getTransitions();
                Collection<EVisArcProduct> arcProducts = vis.getArcProduct();
                Collection<EVisArcReactant> arcReactants = vis.getArcResource();
                em.getTransaction().commit();

                for (EVisTransition trans : transColl) {

                    em.getTransaction().begin();
                    EReaction reaction = trans.getReaction();
                    em.getTransaction().commit();

                    if (detachedReactions.contains(reaction)) {
                        reactC.removeEVisArcReactants(vis, reaction);
                        prodC.removeEvisArcProducts(vis, reaction);
                        tc.removeTransition(trans);
                    }
                }
            }

        } finally {
            em.getTransaction().begin();
            em.flush();
            em.getTransaction().commit();
            em.close();
        }
    }
}
