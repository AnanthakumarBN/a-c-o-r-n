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
import java.util.Properties;
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
            //Hmm, should I cut it into shorter lines? :)
          /*  return (Collection<EBounds>) (List<EBounds>) (em.createQuery(
            "SELECT b FROM EBOUNDS b WHERE b.model = :parent_model AND b.reaction NOT IN" +
            "(SELECT c.reaction FROM EBOUNDS c WHERE c.model = :curr_model)" +
            "UNION " +
            "SELECT d FROM EBOUNDS d WHERE d.model = :curr_model").
            setParameter("curr_model", model.getId()).
            setParameter("parent_model", model.getParent().getId()).getResultList());
             */
            List<EBounds> res1 = em.createQuery(
                    "SELECT e FROM EBounds e WHERE e.model = :model").
                    setParameter("model", model).
                    getResultList();
            List<EBounds> res2 = em.createQuery(
                    " SELECT b FROM EModel m, EBounds b WHERE " +
                    " m.id = :modelID AND m.parent IS NOT NULL AND " +
                    " b.model = m.parent AND NOT EXISTS (SELECT c " +
                    " FROM EBounds c WHERE c.model = :model AND " +
                    " b.reaction = c.reaction )").
                    setParameter("modelID", model.getId()).
                    setParameter("model", model).
                    getResultList();
            res1.addAll(res2);
            return res1;
        }
    }

    void processTask(ETask task, ObjectMessage msg, EntityManager em) {
        EMethod method;
        EModel model;
        Collection<EBounds> bounds;
        TaskMessage tm;

        System.out.println("TASK found - processing...");

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
            throw new Error("AmkfbaException, aborting...");
        } catch (JMSException e) {
            e.printStackTrace(System.err);
            throw new Error("JMSException, aborting...");
        }

    //output = new AdapterAmkfba().runFba(model, bounds);
    //persistResults(output, task, em);
    }

    private void setupMessageListener() throws javax.jms.JMSException, javax.naming.NamingException, Exception {
           
        Properties p = System.getProperties();
        
        InitialContext ctx = new InitialContext(p);
        
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
               int i = 3;

                System.out.println("Waiting for message...");
                mm = receiver.receive();

                System.out.println("Message received");
     
                if (mm instanceof ObjectMessage) {
                    msg = (ObjectMessage) mm;
                    tm = (TaskMessage) msg.getObject();
                    System.out.println("Found task with id=" + tm.getTaskId());
                    try {
                        task = (ETask) em.createNamedQuery("ETask.findById").setParameter("id", tm.getTaskId()).
                                setHint("toplink.refresh", true).getSingleResult();

                        
                        if (!task.getStatus().equals(ETask.statusSysError)) {
                            processTask(task, msg, em);
                        }

                    } catch (NoResultException e) {
                        //The task has been deleted - ignore the message
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
