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
import acorn.db.ECommonResults;
import acorn.db.EKgeneData;
import acorn.db.EModel;
import acorn.db.ETask;
import java.util.Collection;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

/**
 *
 * @author kuba
 */
public class KgeneMethod {

    private static void persistResults(ETask task, AmkfbaOutput output) {
        EntityManager em = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
        EntityTransaction et = em.getTransaction();

        ECommonResults results;

        results = output.getCommonResults();
        et.begin();
        task = (ETask) em.createNamedQuery("ETask.findById").setHint("toplink.refresh", true).
                setParameter("id", task.getId()).getSingleResult();
        results.setTask(task);
        task.setStatus(ETask.statusDone);
        task.setCommonResults(results);
        et.commit();
    }

    static public void run(ETask task, EModel model, Collection<EBounds> bounds) throws AmkfbaException {
        EKgeneData data = (EKgeneData) task.getMethodData();
        String objectiveFunction = data.getObjFunctionSid();
        AmkfbaOutput output;

        output = AdapterAmkfba.getInstance().runKgene(model, bounds, objectiveFunction, data.getGene(), false);
        persistResults(task, output);

    }
}
