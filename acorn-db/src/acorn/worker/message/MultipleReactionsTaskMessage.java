/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package acorn.worker.message;

import acorn.db.EReaction;
import java.util.LinkedList;

/**
 *
 * @author kuba
 */
public class MultipleReactionsTaskMessage extends TaskMessage {
    private LinkedList<Integer> reactionIds;

    public MultipleReactionsTaskMessage(int id)
    {
        super(id);
        reactionIds = new LinkedList<Integer>();
    }
    public LinkedList<Integer> getReactionIds() {
        return reactionIds;
    }

    public void addReaction(EReaction reaction)
    {
        reactionIds.add(new Integer(reaction.getId()));
    }
}
