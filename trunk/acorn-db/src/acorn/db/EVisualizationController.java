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
        } catch (NoResultException ex) {
            return null;
        } finally {
            em.close();
        }
    }

    public List<EVisualization> getAllModelVisualizations(EModel model) {
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

    public static final char VIS_NAME_SEPARATOR = '.';

    public static String visNameForUser(String visName, String login) {
        return visName + VIS_NAME_SEPARATOR + login;
    }

    public static String stripVisNameFromUser(String visName) {
        int ind = visName.indexOf(VIS_NAME_SEPARATOR);
        if (ind == -1)
            return visName;
        else
            //assumes that "_" cannot be used in user name
            return visName.substring(ind+1);
    }

    public boolean isVisualizationNameUsed(String visName, String login) {
        EVisualization vis = getVisualizationByName(visNameForUser(visName, login));
        if (vis == null) {
            return false;
        } else {
            return true;
        }
    }

//    public List<EVisualization> getModelVisualizationsForUser(EModel model, String login) {
//        //temporary solution (to not to change the db schema)
//        //better add user field to visualisation
//        EntityManager em = getEntityManager();
//        try {
//            em.getTransaction().begin();
//            List<EVisualization> visualizations = em.createNamedQuery("EVisualization.getVisualizationsByModel").setParameter("model", model).setParameter("name", login + "_%").getResultList();
//            em.getTransaction().commit();
//            return visualizations;
//        } finally {
//            em.close();
//        }
//    }

     /**
     *
     * @param modelName
     * @return all visualizations connected to modelId model or its descendants
     */
    public List<EVisualization> getDescVisualizationsAll(int modelId) {
        EModelController mc = new EModelController();
        EModel rootModel = null;
        List<EVisualization> visualizations = new ArrayList<EVisualization>();

        EntityManager em = getEntityManager();
        try {
            rootModel = mc.getModel(modelId);
            List<EModel> models = mc.getDescModels(rootModel);

            em.getTransaction().begin();
            for (EModel mod : models) {
                visualizations.addAll(em.createNamedQuery("EVisualization.getByModel").setParameter("model", mod).getResultList());
            }
            em.getTransaction().commit();
            return visualizations;
        } finally {
            //em.close();
        }
    }

    /**
     *
     * @param modelName
     * @return visualizations connected to modelId model or its descendants that are owned by a given user
     */
    public List<EVisualization> getDescVisualizationsForUser(int modelId, String login) {
        EModelController mc = new EModelController();
        EUserController uc = new EUserController();
        EModel rootModel = null;
        EUser user = null;
        List<EVisualization> visualizations = new ArrayList<EVisualization>();

        EntityManager em = getEntityManager();
        try {
            user = uc.findUserByLogin(login);
            rootModel = mc.getModel(modelId);
            List<EModel> models = mc.getDescModels(rootModel, user);

            em.getTransaction().begin();
            for (EModel mod : models) {
                //temporary solution (to not to change the db schema)
                //better add user field to visualisation
                visualizations.addAll(em.createNamedQuery("EVisualization.getUserVisualizationsByModel").setParameter("model", mod).setParameter("name", login + "_%").getResultList());
            }
            em.getTransaction().commit();
            return visualizations;
        } finally {
            //em.close();
        }
    }

    /**
     *
     * @param modelName
     * @return shared (created by guest) visualizations connected to modelId model or its descendants
     */
    public List<EVisualization> getDescVisualizationsShared(int modelId) {
        EModelController mc = new EModelController();
        EUserController uc = new EUserController();
        EModel rootModel = null;
        List<EVisualization> visualizations = new ArrayList<EVisualization>();

        EntityManager em = getEntityManager();
        try {
            rootModel = mc.getModel(modelId);
            List<EModel> models = mc.getDescModelsShared(rootModel);

            em.getTransaction().begin();
            for (EModel mod : models) {
                //temporary solution (to not to change the db scherootModelma)
                //better add user field to visualisation
                visualizations.addAll(em.createNamedQuery("EVisualization.getUserVisualizationsByModel").setParameter("model", mod).setParameter("name", "_%").getResultList());
            }
            em.getTransaction().commit();
            return visualizations;
        } finally {
            //em.close();
        }
    }


         /**
     *
     * @param modelName
     * @return all visualizations connected to modelId model or its ancestors
     */
    public List<EVisualization> getAncestorVisualizationsAll(int modelId) {
        EModelController mc = new EModelController();
        EModel startModel = null;
        List<EVisualization> visualizations = new ArrayList<EVisualization>();

        EntityManager em = getEntityManager();
        try {
            startModel = mc.getModel(modelId);
            List<EModel> models = mc.getAncestorsModels(startModel);

            em.getTransaction().begin();
            for (EModel mod : models) {
                visualizations.addAll(em.createNamedQuery("EVisualization.getByModel").setParameter("model", mod).getResultList());
            }
            em.getTransaction().commit();
            return visualizations;
        } finally {
            //em.close();
        }
    }

    /**
     *
     * @param modelName
     * @return visualizations connected to modelId model or its ancestors that are owned by a given user
     */
    public List<EVisualization> getAncestorVisualizationsForUser(int modelId, String login) {
        EModelController mc = new EModelController();
        EUserController uc = new EUserController();
        EModel startModel = null;
        EUser user = null;
        List<EVisualization> visualizations = new ArrayList<EVisualization>();

        EntityManager em = getEntityManager();
        try {
            user = uc.findUserByLogin(login);
            startModel = mc.getModel(modelId);
            List<EModel> models = mc.getAncestorsModels(startModel);

            em.getTransaction().begin();
            for (EModel mod : models) {
                //temporary solution (to not to change the db schema)
                //better add user field to visualisation
                visualizations.addAll(em.createNamedQuery("EVisualization.getUserVisualizationsByModel").setParameter("model", mod).setParameter("name", login + EVisualizationController.VIS_NAME_SEPARATOR + "%").getResultList());
            }
            em.getTransaction().commit();
            return visualizations;
        } finally {
            //em.close();
        }
    }

    /**
     *
     * @param modelName
     * @return shared (created by guest) visualizations connected to modelId model or its descendants
     */
    public List<EVisualization> getAncestorVisualizationsShared(int modelId) {
        EModelController mc = new EModelController();
        EUserController uc = new EUserController();
        EModel startModel = null;
        List<EVisualization> visualizations = new ArrayList<EVisualization>();

        EntityManager em = getEntityManager();
        try {
            startModel = mc.getModel(modelId);
            List<EModel> models = mc.getAncestorsModels(startModel);

            em.getTransaction().begin();
            for (EModel mod : models) {
                //temporary solution (to not to change the db schema)
                //better add user field to visualisation
                visualizations.addAll(em.createNamedQuery("EVisualization.getUserVisualizationsByModel").setParameter("model", mod).setParameter("name", EVisualizationController.VIS_NAME_SEPARATOR+"%").getResultList());
            }
            em.getTransaction().commit();
            return visualizations;
        } finally {
            //em.close();
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
