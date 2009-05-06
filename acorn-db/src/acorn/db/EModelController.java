package acorn.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

/**
 * EModelController
 * @author lukasz
 */
public class EModelController extends EntityController {

    /**
     * Returns list of all models from database.
     * @return - list of all models from database
     */
    public List<EModel> getModels() {
        EntityManager em = getEntityManager();
        try {
            return (List<EModel>) em.createQuery("select m from EModel as m").getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Returns list of all models owned by @user.
     * @return - list of all models owned by @user
     */
    public List<EModel> getModels(EUser user) {
        EntityManager em = getEntityManager();
        try {
            return (List<EModel>) em.createNamedQuery("EModel.findByOwner").
                    setParameter("owner", user).
                    setHint("toplink.refresh", true).
                    getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Returns list of all shared models.
     * @return - list of all shared models
     */
    public List<EModel> getSharedModels() {
        EntityManager em = getEntityManager();
        try {
            return (List<EModel>) em.createQuery("SELECT e FROM EModel e WHERE e.shared = :shared").
                    setParameter("shared", true).
                    setHint("toplink.refresh", true).
                    getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Finds and returns @model.
     * @return - @model
     */
    public EModel getModel(EModel model) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            EModel modelx = (EModel) em.createNamedQuery("EModel.findById").
                    setParameter("id", model.getId()).
                    setHint("toplink.refresh", true).
                    getSingleResult();
            em.getTransaction().commit();
            return modelx;
        } finally {
            em.close();
        }
    }

    /**
     * Finds and returns @modelId.
     * @return - @modelId
     */
    public EModel getModel(Integer modelId) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            EModel modelx = (EModel) em.createNamedQuery("EModel.findById").
                    setParameter("id", modelId).
                    setHint("toplink.refresh", true).
                    getSingleResult();
            em.getTransaction().commit();
            return modelx;
        } finally {
            em.close();
        }
    }

    public EModel getModelByName(String name) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            EModel modelx = (EModel) em.createNamedQuery("EModel.findByName").
                    setParameter("name", name).
                    setHint("toplink.refresh", true).
                    getSingleResult();
            em.getTransaction().commit();
            return modelx;
        } finally {
            em.close();
        }
    }

    /**
     * Adds @model.
     */
    public void addModel(EModel model) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(model);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    /**
     * Merges @model with database.
     */
    public void mergeModel(EModel model) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(model);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    private void removeModel(EntityManager em, EModel model) {
        /* delete model's Model */
        if (model.getTask() != null) {
            em.remove(model.getTask());
        }

        /* delete children */
        for (EModel child : model.getEModelCollection()) {
            removeModel(em, child);
        }

        /* update Metabolism */
        model.getMetabolism().getEModelCollection().remove(model);

        if (model.getMetabolism().getEModelCollection().size() == 0) {
            /* delete Metabolism */
            em.remove(model.getMetabolism());
        }

        em.remove(model);
    }

    /**
     * Removes @model from database.
     */
    public void removeModel(EModel model) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            EModel modelx = em.find(EModel.class, model.getId());
            removeModel(em, modelx);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    /**
     * Removes @modelId from database.
     */
    public void removeModel(Integer modelId) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            EModel modelx = em.find(EModel.class, modelId);
            removeModel(modelx);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public void addModel(
            EMetabolism metabolism,
            EModel model,
            Map<String, ECompartment> compartment_map,
            Map<String, ESpecies> species_map,
            List<EReaction> reaction_list,
            List<EReactant> reactant_list,
            List<EProduct> product_list,
            List<EBounds> bounds_list) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();

            em.persist(metabolism);
            em.persist(model);

            model.getMetabolism().getEModelCollection().add(model);

            for (ECompartment c : compartment_map.values()) {
                em.persist(c);
                c.getMetabolism().getECompartmentCollection().add(c);
            }

            for (ESpecies sp : species_map.values()) {
                em.persist(sp);
                sp.getCompartment().getESpeciesCollection().add(sp);
            }

            for (EReaction r : reaction_list) {
                em.persist(r);
                r.getMetabolism().getEReactionCollection().add(r);
            }

            for (EReactant r : reactant_list) {
                em.persist(r);
                r.getSpecies().getEReactantCollection().add(r);
            /* r.getReaction().getEReactantCollection().add(r); */
            }

            for (EProduct p : product_list) {
                em.persist(p);
                p.getSpecies().getEProductCollection().add(p);
            /* p.getReaction().getEProductCollection().add(p); */
            }

            for (EBounds b : bounds_list) {
                em.persist(b);
                b.getModel().getEBoundsCollection().add(b);
            /* b.getReaction().getEBoundsCollection().add(b); */
            }

            em.merge(metabolism);

            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    /**
     * Removes @bounds.
     */
    public void removeBounds(EModel model) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            for (EBounds bounds : model.getEBoundsCollection()) {
                EBounds boundsx = em.find(EBounds.class, bounds.getId());
                em.remove(boundsx);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public List<EModel> getChildrenByName(String modelName) {
        EntityManager em = getEntityManager();
        try {
            return em.createNamedQuery("EModel.getChildrenByName").setParameter("name", modelName).getResultList();
        } finally {
            em.close();
        }
    }

    public List<EModel> getChildrenByModel(EModel model) {
        EntityManager em = getEntityManager();
        try {
            return em.createNamedQuery("EModel.getChildrenByModel").setParameter("model", model).getResultList();
        } finally {
            em.close();
        }
    }

    /**
     *
     * @param parentList list of parent models
     * @return list of children models
     */
    public List<EModel> getChildrenByModelList(List<EModel> parentList) {
        List<EModel> children = new ArrayList<EModel>(0);
        for (EModel model : parentList) {
            children.addAll(getChildrenByModel(model));
        }
        return children;
    }

    public EMethodData getMethodType(String modelName) {
        EntityManager em = getEntityManager();
        EMethodData method = null;
        try {
            method = (EMethodData) em.createNamedQuery("EModel.getMethodData").setParameter("modelName", modelName).getSingleResult();
            return method;
        } catch (NoResultException ex) {
            return method;
        } finally {
            em.close();
        }
    }

    public boolean isFbaTask(String modelName) {
        EMethodData method = getMethodType(modelName);
        EntityManager em = getEntityManager();
        try {
            em.createNamedQuery("EModel.isFba").setParameter("method", method).getSingleResult();
            return true;
        } catch (NoResultException ex) {
            return false;
        }
    }

    public boolean isDoneTask(String modelName) {
        EntityManager em = getEntityManager();
        try {
            ETask task = (ETask) em.createNamedQuery("EModel.getTask").setParameter("name", modelName).getSingleResult();
            if (task == null) {
                return false;
            }
            if (task.getStatus().equalsIgnoreCase(ETask.statusDone)) {
                return true;
            }
        } catch (NoResultException ex) {
            return false;
        }
        return false;
    }
}
