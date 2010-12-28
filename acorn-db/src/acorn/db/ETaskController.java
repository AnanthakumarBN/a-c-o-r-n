package acorn.db;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

/**
 * ETaskController
 * @author lukasz
 */
public class ETaskController extends EntityController {

    /**
     * Returns list of all tasks from database.
     * @return - list of all tasks from database
     */
    public List<ETask> getTasks() {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            List<ETask> res = (List<ETask>) em.createQuery("SELECT t FROM ETask AS t").getResultList();
            em.getTransaction().commit();
            return res;
        } finally {
            em.close();
        }
    }

    /**
     * Returns list of all tasks owned by @user.
     * @return - list of all tasks owned by @user
     */
    public List<ETask> getTasks(EUser user) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            List<ETask> res = (List<ETask>) em.createNamedQuery("ETask.findByUserID").
                    setParameter("id", user).
                    setHint("toplink.refresh", true).
                    getResultList();
            em.getTransaction().commit();
            return res;
        } finally {
            em.close();
        }
    }

    /**
     * Returns list of all shared tasks.
     * @return - list of all shared tasks
     */
    public List<ETask> getSharedTasks() {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            List<ETask> res = (List<ETask>) em.createQuery("SELECT e FROM ETask e WHERE e.shared = :shared").
                    setParameter("shared", true).
                    setHint("toplink.refresh", true).
                    getResultList();
            em.getTransaction().commit();
            return res;
        } finally {
            em.close();
        }
    }

    /**
     * Finds and returns @task.
     * @return - @task
     */
    public ETask getTask(ETask task) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            ETask taskx = (ETask) em.createNamedQuery("ETask.findById").
                    setParameter("id", task.getId()).
                    setHint("toplink.refresh", true).
                    getSingleResult();
            em.getTransaction().commit();
            return taskx;
        } finally {
            em.close();
        }
    }

    /**
     * Finds and returns @taskId.
     * @return - @taskId
     */
    public ETask getTask(Integer taskId) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            ETask taskx = (ETask) em.createNamedQuery("ETask.findById").
                    setParameter("id", taskId).
                    setHint("toplink.refresh", true).
                    getSingleResult();
            em.getTransaction().commit();
            return taskx;
        } finally {
            em.close();
        }
    }

    /**
     * Merges @task.
     */
    public void mergeTask(ETask task) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(task);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    /**
     * Removes @task from database.
     */
    public void removeTask(ETask task) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            ETask taskx = em.find(ETask.class, task.getId());
            em.remove(taskx);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    /**
     * Removes @taskId from database.
     */
    public void removeTask(Integer taskId) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            ETask taskx = em.find(ETask.class, taskId);
            em.remove(taskx);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }


    /* @params reactionSid - sid of reaction for which flux is returned
     *
     *
     * @returns flux for pointed reaction and task
     */
    public float getFlux(ETask task, String reactionSid) {
        Float flux = new Float(0);
        EntityManager em = getEntityManager();

        try {
            em.getTransaction().begin();
            flux = (Float) em.createNamedQuery("ETask.getFlux").setParameter("reactionSid", reactionSid).setParameter("task", task).getSingleResult();
            em.getTransaction().commit();
            return flux;
        } catch (NoResultException ex) {
            return flux;
        } finally {
            em.close();
        }
//        List<EfbaResultElement> fbaList = (List<EfbaResultElement>) task.getEfbaResultElementCollection();
//
//        if (fbaList == null) {
//            return flux;
//        }
//        for (EfbaResultElement fba : fbaList) {
//            EReaction react = fba.getReaction();
//
//            if (react.getSid().equals(reactionSid)) {
//                return fba.getFlux();
//            }
//        }
//        return flux;
    }

    public String getFluxFVA(ETask task, String reactionSid) {
        EntityManager em = getEntityManager();

        try {
            em.getTransaction().begin();
            Object[] res = (Object[]) em.createNamedQuery("ETask.getFluxFVA").setParameter("reactionSid", reactionSid).setParameter("task", task).getSingleResult();
            em.getTransaction().commit();
            return ("" + (Float) res[0] + ".." + (Float) res[1] + "");
        } catch (NoResultException ex) {
            return "0..0";
        } finally {
            em.close();
        }
    }

    public ETask getTask(EModel model) {
        EntityManager em = getEntityManager();

        try {
            ETask task = (ETask) em.createNamedQuery("ETask.findByModel").setParameter("model", model).getSingleResult();
            return task;
        } catch (NoResultException ex) {
            return null;
        } finally {
            em.close();
        }
    }
}
