/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package acorn.worker.method;

import acorn.worker.main.PersistenceManager;
import acorn.worker.amkfba.AmkfbaOutput;
import acorn.worker.amkfba.AdapterAmkfba;
import acorn.worker.amkfba.AmkfbaException;
import acorn.db.EBounds;
import acorn.db.EMetabolism;
import acorn.db.EModel;
import acorn.db.EReaction;
import acorn.db.ERscanData;
import acorn.db.ETask;
import acorn.db.ErscanResultElement;
import acorn.worker.message.MultipleReactionsTaskMessage;
import java.util.Collection;
import java.util.LinkedList;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

/**
 *
 * @author kuba
 */
public class RscanMethod {

    private static void persistResults(ETask task, LinkedList<ErscanResultElement> results) {
        EntityManager em = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
        EntityTransaction et = em.getTransaction();
        EMetabolism met;

        et.begin();
        task = (ETask) em.createNamedQuery("ETask.findById").setHint("toplink.refresh", true).
                setParameter("id", task.getId()).getSingleResult();

        if (task.getStatus().equals(ETask.statusSysError)) {
            et.commit();
            return;
        }

        for (ErscanResultElement result : results) {
            em.persist(result);
        }

        et.commit();
        
        et.begin();
        float allReactions, completedReactions;

        met = task.getModel().getMetabolism();
        allReactions = (float) met.getEReactionCollection().size();

        completedReactions = (float) task.getErscanResultElementCollection().size();
        if (allReactions == completedReactions) {
            task.setStatus(ETask.statusDone);
            task.setInfo("");
        } else {
            task.setInfo(String.format("%.2f %% completed.", 100.0 * completedReactions / allReactions));
            task.setStatus(ETask.statusInProgress);
        }
        et.commit();
        em.close();
    }

    static public void run(ETask task, EModel model, Collection<EBounds> bounds, MultipleReactionsTaskMessage msg) throws AmkfbaException {
        AmkfbaOutput output;
        ERscanData data = (ERscanData) task.getMethodData();
        LinkedList<ErscanResultElement> results = new LinkedList<ErscanResultElement>();


        EReaction reaction;
        EntityManager em = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();

        for (Integer rid : msg.getReactionIds()) {
            reaction = em.find(EReaction.class, rid);

            ErscanResultElement result = new ErscanResultElement();

            output = AdapterAmkfba.getInstance().runKgene(model, bounds, data.getObjFunctionSid(), reaction.getSid(), true);

            result.setGrowthRate(output.getCommonResults().getGrowthRate());
            result.setStatus(output.getCommonResults().getStatus());
            result.setReaction(reaction);
            result.setTask(task);
            results.add(result);
        }
        persistResults(task, results);
    }
}
