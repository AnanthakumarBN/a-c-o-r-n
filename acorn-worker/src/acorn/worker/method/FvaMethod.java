/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package acorn.worker.method;

import acorn.worker.main.*;
import acorn.worker.amkfba.AmkfbaOutput;
import acorn.worker.amkfba.AdapterAmkfba;
import acorn.worker.amkfba.AmkfbaException;
import acorn.db.EBounds;
import acorn.db.EMetabolism;
import acorn.db.EModel;
import acorn.db.EReaction;
import acorn.db.ETask;
import acorn.db.EfvaResultElement;
import acorn.worker.message.MultipleReactionsTaskMessage;
import java.util.Collection;
import java.util.LinkedList;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

/**
 *
 * @author kuba
 */
public class FvaMethod {

    private static void persistResults(ETask task, LinkedList<EfvaResultElement> results) {
        EntityManager em = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
        EntityTransaction et = em.getTransaction();
        EMetabolism met;

        et.begin();

        task = (ETask) em.createNamedQuery("ETask.findById").setHint("toplink.refresh", true).
                setParameter("id", task.getId()).getSingleResult();

        if (task.getStatus().equals(ETask.statusSysError)) {
            et.commit();
            em.close();
            return;
        }

        for (EfvaResultElement result : results) {
            em.persist(result);
        }

        et.commit();
        em.close();

    }

    public static void run(ETask task, EModel model, Collection<EBounds> bounds, MultipleReactionsTaskMessage msg) throws AmkfbaException {
        AmkfbaOutput output;

        LinkedList<EfvaResultElement> results = new LinkedList<EfvaResultElement>();

        EReaction reaction;
        EntityManager em = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();

        for (Integer rid : msg.getReactionIds()) {
            reaction = em.find(EReaction.class, rid);

            EfvaResultElement result = new EfvaResultElement();

            output = AdapterAmkfba.getInstance().runObjstat(model, bounds, reaction.getSid(), true, true);

            result.setMaxFlux(output.getCommonResults().getGrowthRate());

            output = AdapterAmkfba.getInstance().runObjstat(model, bounds, reaction.getSid(), true, false);

            result.setMinFlux(output.getCommonResults().getGrowthRate());


            result.setReaction(reaction);
            result.setTask(task);
            results.add(result);

        }
        persistResults(task, results);

    }
}
