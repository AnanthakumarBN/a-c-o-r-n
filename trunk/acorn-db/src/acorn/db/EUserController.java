package acorn.db;

import java.util.List;
import javax.persistence.EntityManager;

/**
 * EUserController
 * @author lukasz
 */
public class EUserController extends EntityController {
    
    /**
     * Returns list of all users from database.
     * @return - list of all users from database
     */
    public List<EUser> getUsers() {
        EntityManager em = getEntityManager();
        try {
            return (List<EUser>) em.createQuery("SELECT u FROM EUser AS u").getResultList();
        } finally {
            em.close();
        }
    }
    
    /**
     * Finds and returns @user.
     * @return - @user
     */
    public EUser getUser(EUser user) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            EUser userx = em.find(EUser.class, user.getId());
            em.getTransaction().commit();
            return userx;
        } finally {
            em.close();
        }  
    }
    
    /**
     * Finds and returns @user.
     * @return - @user
     */
    public EUser getUser(Integer userId) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            EUser userx = em.find(EUser.class, userId);
            em.getTransaction().commit();
            return userx;
        } finally {
            em.close();
        }  
    }
    
    /**
     * Adds @user.
     */
    public void addUser(EUser user) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();
        } finally {
            em.close();
        }  
    }

    /**
     * Merges @user.
     */
    public void mergeUser(EUser user) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(user);
            em.getTransaction().commit();
        } finally {
            em.close();
        }  
    }
    
    /**
     * Removes @user from database.
     */
    public void removeUser(EUser user) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            EUser userx = em.find(EUser.class, user.getId());
            em.remove(userx);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
    
    /**
     * Removes @userId from database.
     */
    public void removeUser(Integer userId) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            EUser userx = em.find(EUser.class, userId);
            em.remove(userx);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
    
    /**
     * Finds and returns user whose login is @login.
     * @return - user whose login is @login
     */
    public EUser findUserByLogin(String login) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            EUser userx = (EUser) em.createNamedQuery("EUser.findByLogin").
                            setParameter("login", login).
                            getSingleResult();
            em.getTransaction().commit();
            return userx;
        } finally {
            em.close();
        }  
    } 
}
