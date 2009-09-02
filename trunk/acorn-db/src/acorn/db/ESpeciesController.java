package acorn.db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

/**
 * ESpeciesController
 * @author lukasz
 */
public class ESpeciesController extends EntityController {

    /**
     * Adds @species.
     */
    public void addSpecies(ESpecies species) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(species);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public List<ESpecies> getSpecies(int modelId) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            EModel model = em.find(EModel.class, modelId);
            List<ESpecies> species = em.createQuery("SELECT s FROM ESpecies s, EModel m WHERE s.compartment.metabolism.id = m.metabolism.id AND m.id = :modelId").
                    setParameter("modelId", model.getId()).
                    getResultList();
            em.getTransaction().commit();
            return species;
        } finally {
            em.close();
        }
    }

    public List<ESpecies> getSpecies(String modelName) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            List<ESpecies> species = em.createNamedQuery("ESpecies.findByModelName").
                    setParameter("modelName", modelName).getResultList();
            em.getTransaction().commit();
            return species;
        } finally {
            em.close();
        }
    }

    public ESpecies getBySidName(String modelName, String sid) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            ESpecies species = (ESpecies) em.createNamedQuery("ESpecies.findByModelNameAndSid").
                    setParameter("modelName", modelName).setParameter("sid", sid).getSingleResult();
            return species;
        } finally {
            em.close();
        }
    }

    public ESpecies getByModelIdAndSidName(int modelId, String sid) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            EModel model = em.find(EModel.class, modelId);
            if(model == null){
                return null;
            }
            ESpecies species = (ESpecies) em.createNamedQuery("ESpecies.findByModelAndSid").
                    setParameter("model", model).setParameter("sid", sid).getSingleResult();
            return species;
        } finally {
            em.close();
        }
    }
}