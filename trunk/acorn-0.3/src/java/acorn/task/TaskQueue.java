/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package acorn.task;

import acorn.db.EMethod;
import acorn.db.EReaction;
import acorn.db.ETask;
import acorn.worker.message.MultipleReactionsTaskMessage;
import acorn.worker.message.TaskMessage;
import java.util.Collection;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.naming.InitialContext;

/**
 *
 * @author kuba
 */
public class TaskQueue {

    private static TaskQueue singleton = null;
    private static QueueConnection qc;
    private static QueueSession qs;
    private static QueueSender sender;

    private final int singleTaskPriority = 7;
    private final int multipleTaskPriority = 4;


    public static TaskQueue getInstance() throws Exception {

        if (singleton == null) {
            singleton = new TaskQueue();
            InitialContext ic = new InitialContext();
            QueueConnectionFactory factory = (QueueConnectionFactory) ic.lookup("jms/taskQueueFactory");

            Queue queue = (Queue) ic.lookup("jms/taskQueue");

            qc = factory.createQueueConnection();
            qs = qc.createQueueSession(false, Session.CLIENT_ACKNOWLEDGE);

            sender = qs.createSender(queue);
        }
        return singleton;

    }
    /* @Resource(mappedName="jms/taskQueue")
    private static Queue queue;
    @Resource(mappedName="jms/taskQueueFactory")
    private static QueueConnectionFactory factory;*/

    @Override
    protected void finalize() throws Exception {
        qc.close();
        qs.close();
    }

    private synchronized void sendTaskMessage(TaskMessage tm, int priority) throws JMSException {
        sender.send(qs.createObjectMessage(tm), DeliveryMode.PERSISTENT, priority, 0);
    }

    
    /*
     * This method is used to send JMS messages describing a task, in which calculations
     * have to be done for each reaction in the model
     */
    private void enqueueMultipleReactionsTask(ETask task) throws JMSException {
        MultipleReactionsTaskMessage mrtm = new MultipleReactionsTaskMessage(task.getId());
        Collection<EReaction> reactions;

        reactions = task.getModel().getMetabolism().getEReactionCollection();
        int all;
        all = 0;
        for (EReaction r : reactions) {
            mrtm.addReaction(r);
            /* at most 6 reactions are sent in a single message */
            if (mrtm.getReactionIds().size() >= 6) {
                sendTaskMessage(mrtm, multipleTaskPriority);
                all += mrtm.getReactionIds().size();
                mrtm = new MultipleReactionsTaskMessage(task.getId());
            }
        }
        all += mrtm.getReactionIds().size();
        if (mrtm.getReactionIds().size() > 0) {
            sendTaskMessage(mrtm, multipleTaskPriority);
        }
    }

    
    /*
     * This method is used to send JMS messages describing a task, in we perform
     * a single simulation, that is amkfba is run once only.
     */
    private void enqueueSimpleTask(ETask task) throws JMSException {
        TaskMessage tm = new TaskMessage(task.getId());
        sendTaskMessage(tm, singleTaskPriority);
    }

    public void enqueueTask(ETask task) throws JMSException, Exception {
        EMethod method;
        method = task.getMethod();

        if (method.getIdent().equals(EMethod.fba) || method.getIdent().equals(EMethod.kgene)) {
            enqueueSimpleTask(task);
        } else if (method.getIdent().equals(EMethod.fva) || method.getIdent().equals(EMethod.rscan)) {
            enqueueMultipleReactionsTask(task);
        } else {
            throw new Exception("Fatal: unknown method: " + method.getIdent());
        }
    /* IF YOU WANT TO ADD NEW METHOD -> put method.getIdent().equals(EMethod.mtd) (where mtd is new method's identifier)
     * in proper "if" condition
     */
    }
}
