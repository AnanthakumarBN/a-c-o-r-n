package acorn.db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.persistence.EntityManager;
import java.util.List;
import javax.persistence.NoResultException;

/**
 * EReactionController
 * @author lukasz
 */
public class EReactionController extends EntityController {

    /**
     * Adds @reaction.
     */
    public void addReaction(EReaction reaction) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(reaction);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    /*
     * @params reactionSid - sid of reaction for which list of reactants sids is computed
     * @returns - list of sid of Species for reaction with reactionSid
     */
    public ArrayList<String> getReactantsSpeciesList(int rId) {
        EntityManager em = getEntityManager();
        ArrayList<String> al = new ArrayList();
        List<EReactant> reactColl;
        EReactantController rc = new EReactantController();
        EReaction r;

        //finds reaction by Sid - reaction parameter 
        try {
            em.getTransaction().begin();
            r = (EReaction) em.createNamedQuery("EReaction.findById").setParameter("id", rId).getSingleResult();
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        //gets reactants from reaction and gets sid of species
        reactColl = rc.getReactants(r);
        for (EReactant reactant : reactColl) {
            ESpecies species = (ESpecies) reactant.getSpecies();
            al.add(species.getSid());
        }
        return al;
    }

    /**
     * @param rId - id of reaction for which list of products sids is computed
     * @return - list of sid of Species for reaction with reactionSid
     */
    public ArrayList<String> getProductsSpeciesList(int rId) {
        EntityManager em = getEntityManager();
        ArrayList<String> al = new ArrayList();
        List<EProduct> prodColl;
        EProductController pc = new EProductController();
        EReaction r;

        //finds reaction by id - reaction parameter 
        try {
            em.getTransaction().begin();
            r = (EReaction) em.createNamedQuery("EReaction.findById").setParameter("id", rId).getSingleResult();
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        prodColl = pc.getProducts(r);
        for (EProduct product : prodColl) {
            ESpecies species = (ESpecies) product.getSpecies();
            al.add(species.getSid());
        }
        return al;
    }

    public EReaction getByModelNameAndReactionSid(String reactionSid, String modelName) {
        EntityManager em = getEntityManager();
        EReaction r = null;
        EModel m = null;
        EMetabolism metabol = null;

        try {
            em.getTransaction().begin();
            m = (EModel) em.createNamedQuery("EModel.findByName").setParameter("name", modelName).getSingleResult();
            metabol = m.getMetabolism();
            r = (EReaction) em.createNamedQuery("EMetabolism.findByIDAndReactionSid").setParameter("id", metabol.getId()).setParameter("sid", reactionSid).getSingleResult();
            em.getTransaction().commit();
        } catch (NoResultException nre) {
            return null;
        } finally {
            em.close();
        }
        return r;
    }


       public EReaction getByModelIdAndReactionSid(int modelId, String reactionSid) {
        EntityManager em = getEntityManager();
        EReaction r = null;
        EModel m = null;
        EMetabolism metabol = null;

        try {
            em.getTransaction().begin();
            m = em.find(EModel.class, modelId);
            metabol = m.getMetabolism();
            r = (EReaction) em.createNamedQuery("EMetabolism.findByIDAndReactionSid").setParameter("id", metabol.getId()).setParameter("sid", reactionSid).getSingleResult();
            em.getTransaction().commit();
        } catch (NoResultException nre) {
            return null;
        } finally {
            em.close();
        }
        return r;
    }
    /**
     *
     * @param reactionSid  - sid of reaction
     * @param model - model of reaction
     * @return reaction - searched reaction
     */
    public EReaction getByModelAndReactionSid(String reactionSid, EModel model) {
        EntityManager em = getEntityManager();
        EReaction r = null;
        EMetabolism metabol = null;
        try {
            em.getTransaction().begin();
            metabol = model.getMetabolism();
            r = (EReaction) em.createNamedQuery("EMetabolism.findByIDAndReactionSid").
                    setParameter("id", metabol.getId()).setParameter("sid", reactionSid).getSingleResult();
            em.getTransaction().commit();
        } catch (NoResultException nre) {
            return null;
        } finally {
            em.close();
        }
        return r;
    }

    public List<EReaction> getByModelName(String modelName) {
        EntityManager em = getEntityManager();
        EModelController mc = new EModelController();
        List<EReaction> reactionList = new ArrayList<EReaction>();
        EModel m = null;
        EMetabolism metabolism = null;

        try {
            em.getTransaction().begin();
            m = (EModel) em.createNamedQuery("EModel.findByName").setParameter("name", modelName).getSingleResult();
            metabolism = m.getMetabolism();
            reactionList = (List<EReaction>) em.createNamedQuery("EReaction.findByMetabolism").setParameter("metabolism", metabolism).getResultList();
            em.getTransaction().commit();

            //remove reactions that were removed from model
            Collection<EReaction> detachedReactions = mc.getDetachedReactions(m.getId());
            reactionList.removeAll(detachedReactions);
        } catch (NoResultException nre) {
            return null;
        } finally {
            em.close();
        }
        return reactionList;
    }

     public List<EReaction> getByModelId(int modelId) {
        EntityManager em = getEntityManager();
        EModelController mc = new EModelController();
        List<EReaction> reactionList = new ArrayList<EReaction>();
        EModel m = null;
        EMetabolism metabolism = null;

        try {
            em.getTransaction().begin();
            m = em.find(EModel.class, modelId);
            metabolism = m.getMetabolism();
            reactionList = (List<EReaction>) em.createNamedQuery("EReaction.findByMetabolism").setParameter("metabolism", metabolism).getResultList();
            em.getTransaction().commit();

            //remove reactions that were removed from model
            Collection<EReaction> detachedReactions = mc.getDetachedReactions(m.getId());
            reactionList.removeAll(detachedReactions);
        } catch (NoResultException nre) {
            return null;
        } finally {
            em.close();
        }
        return reactionList;
    }
}
