/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package acorn.worker.method;

import acorn.worker.amkfba.AdapterAmkfba;
import acorn.worker.amkfba.AmkfbaException;
import acorn.worker.main.PersistenceManager;
import acorn.db.EBounds;
import acorn.db.EFbaData;
import acorn.db.EModel;
import acorn.db.EReaction;
import acorn.db.ETask;
import acorn.db.EfbaResultElement;
import java.util.Collection;
import java.util.Hashtable;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

/**
 *
 * @author kuba
 */
public class FbaMethod {

    private static void persistResults(FbaOutput output, ETask task) {
        EntityManager em = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
        EntityTransaction et;
        EfbaResultElement resultElement;
        Collection<EReaction> reactions;
        Hashtable<String, EReaction> reactionsMap = new Hashtable<String, EReaction>();

        et = em.getTransaction();
        et.begin();
        task = (ETask) em.createNamedQuery("ETask.findById").setHint("toplink.refresh", true).
                setParameter("id", task.getId()).getSingleResult();
        output.getCommonResults().setTask(task);
        em.persist(output.getCommonResults());

        reactions = task.getModel().getMetabolism().getEReactionCollection();
        for (EReaction er : reactions) {
            reactionsMap.put(er.getSid(), er);
        }

        for (FbaOutputLine outputLine : output.getReactions()) {
            resultElement = new EfbaResultElement();
            resultElement.setTask(task);
            resultElement.setFlux(outputLine.getFlux());
            // reaction = (EReaction) em.createNamedQuery("EReaction.findBySid").
            //       setParameter("sid", outputLine.getReactionName()).getSingleResult();
            resultElement.setReaction(reactionsMap.get(outputLine.getReactionName()));
            em.persist(resultElement);
        }

        et.commit();

    }

    public static void run(ETask task, EModel model, Collection<EBounds> bounds) throws AmkfbaException {
        EFbaData data;
        FbaOutput output;
        data = (EFbaData) task.getMethodData();
        
        output = AdapterAmkfba.getInstance().runFba(model, bounds, data.getObjFunctionSid());
        persistResults(output, task);
    }
}
