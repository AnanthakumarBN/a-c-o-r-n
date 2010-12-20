package acorn.worker.main;

import acorn.worker.method.FvaMethod;
import acorn.worker.method.KgeneMethod;
import acorn.worker.method.FbaMethod;
import acorn.worker.method.RscanMethod;
import javax.persistence.EntityManager;
import acorn.db.*;
import acorn.worker.amkfba.AmkfbaException;
import acorn.worker.message.MultipleReactionsTaskMessage;
import acorn.worker.message.TaskMessage;
import java.util.Collection;
import java.util.List;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.naming.InitialContext;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;

/**
 *
 * @author kuba
 */
public class WorkerDaemon {

    private QueueConnection connection;
    private MessageConsumer receiver;
    private QueueSession session;

    /**
     * Returns a collection of bounds for reactions included in the given's
     * model metabolism. Note that it has to be a sum of bounds of the parent model
     * (if the given model has a parent) and the bounds directly associated with 
     * current model.
     * @param model
     * @return collection of bounds for the given model
     */
    Collection<EBounds> getBoundsForModel(EModel model, EntityManager em) {

        if (model.getParent() == null) {
            return model.getEBoundsCollection();
        } else {
            /* 
             * There are two sources of bounds for each model.
             * First, we retrieve bounds that have been defined for this model
             */
            List<EBounds> res1 = em.createQuery(
                    "SELECT e FROM EBounds e WHERE e.model = :model").
                    setParameter("model", model).
                    getResultList();

            /*
             * The remaining bounds are taken from the parent model
             */
            List<EBounds> res2 = em.createQuery(
                    " SELECT b FROM EModel m, EBounds b WHERE "
                    + " m.id = :modelID AND m.parent IS NOT NULL AND "
                    + " b.model = m.parent AND NOT EXISTS (SELECT c "
                    + " FROM EBounds c WHERE c.model = :model AND "
                    + " b.reaction = c.reaction )").
                    setParameter("modelID", model.getId()).
                    setParameter("model", model).
                    getResultList();
            res1.addAll(res2);
            return res1;
        }
    }

    void markAsBroken(ETask task, EntityManager em) {
        EntityTransaction et = em.getTransaction();

        et.begin();
        task.setStatus(ETask.statusSysError);
        et.commit();
    }

    void processTask(ETask task, ObjectMessage msg, EntityManager em) {
        EMethod method;
        EModel model;
        Collection<EBounds> bounds;
        TaskMessage tm;

        System.out.println("Processing task [id=" + String.valueOf(task.getId()) + "]...");

        method = task.getMethod();
        model = task.getModel();
        bounds = getBoundsForModel(model, em);

        try {
            tm = (TaskMessage) msg.getObject();
            if (method.getIdent().equals(EMethod.fba)) {
                FbaMethod.run(task, model, bounds);
            } else if (method.getIdent().equals(EMethod.fva)) {
                FvaMethod.run(task, model, bounds, (MultipleReactionsTaskMessage) tm);
            } else if (method.getIdent().equals(EMethod.rscan)) {
                RscanMethod.run(task, model, bounds, (MultipleReactionsTaskMessage) tm);
            } else if (method.getIdent().equals(EMethod.kgene)) {
                KgeneMethod.run(task, model, bounds);
            }
            msg.acknowledge();
        } catch (AmkfbaException e) {
            AcornLogger.logError("AmkfbaException: " + e.getMessage());
            markAsBroken(task, em);
        } catch (JMSException e) {
            e.printStackTrace(System.err);
            throw new Error("JMSException, aborting...");
        }
    }

    private void setupMessageListener() throws javax.jms.JMSException, javax.naming.NamingException, Exception {
        InitialContext ctx = new InitialContext();

        QueueConnectionFactory factory =
                (QueueConnectionFactory) ctx.lookup("jms/taskQueueFactory");
        connection = factory.createQueueConnection();
        session =
                connection.createQueueSession(false, Session.CLIENT_ACKNOWLEDGE);
        Queue queue = (Queue) ctx.lookup("jms/taskQueue");

        receiver = session.createConsumer(queue);
        connection.start();
        ctx.close();
    }

    private void closeReceiver() {
        try {
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace(System.err);
        }
    }

    public void run() throws Exception {
        EntityManagerFactory emf = PersistenceManager.getInstance().getEntityManagerFactory();
        EntityManager em = emf.createEntityManager();

        setupMessageListener();

        ETask task;
        try {
            while (true) {
                ObjectMessage msg;
                TaskMessage tm;
                Message mm;

                System.out.println("Waiting for message...");
                mm = receiver.receive();

                System.out.println("Message received");

                if (mm instanceof ObjectMessage) {
                    msg = (ObjectMessage) mm;
                    tm = (TaskMessage) msg.getObject();
                    try {
                        task = (ETask) em.createNamedQuery("ETask.findById").setParameter("id", tm.getTaskId()).
                                setHint("toplink.refresh", true).getSingleResult();

                        if (!task.getStatus().equals(ETask.statusSysError)) {
                            processTask(task, msg, em);
                        } else {
                            System.out.println("Task marked as broken, omitting.");
                        }

                    } catch (NoResultException e) {
                        System.out.println("Task has been deleted");
                    }
                }
                mm.acknowledge();
            }
        } finally {
            closeReceiver();
            PersistenceManager.getInstance().closeEntityManagerFactory();
        }
    }
}
