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

    /*
     * @params reactionSid - sid of reaction for which list of products sids is computed
     * @returns - list of sid of Species for reaction with reactionSid
     */
    public ArrayList<String> getProductsSpeciesList(int rId) {
        EntityManager em = getEntityManager();
        ArrayList<String> al = new ArrayList();
        List<EProduct> prodColl;
        EProductController pc = new EProductController();
        EReaction r;

        //finds reaction by Sid - reaction parameter 
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
        ArrayList<String> al = new ArrayList();
        EReaction r = null;
        EModel m = null;
        EMetabolism metabol = null;

        try {
            em.getTransaction().begin();
            m = (EModel) em.createNamedQuery("EModel.findByName").setParameter("name", modelName).getSingleResult();
            metabol = m.getMetabolism();
            r = (EReaction) em.createNamedQuery("EMetabolism.findByIDAndReactionSid").setParameter("id",metabol.getId()).setParameter("sid", reactionSid).getSingleResult();
            em.getTransaction().commit();
        }catch (NoResultException nre){
            return null;
        } finally {
            em.close();
        }
        return r;
    }
}
