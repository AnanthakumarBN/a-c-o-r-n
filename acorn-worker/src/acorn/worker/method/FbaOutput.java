/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package acorn.worker.method;

import acorn.worker.amkfba.AmkfbaOutput;
import java.util.LinkedList;

/**
 *
 * @author kuba
 */
public class FbaOutput extends AmkfbaOutput {

    private LinkedList<FbaOutputLine> reactions;

    public FbaOutput() {
        super();
        reactions = new LinkedList<FbaOutputLine>();
    }

    public void addLine(String reactionName, Float flux) {
        getReactions().add(new FbaOutputLine(reactionName, flux));
    }

    public LinkedList<FbaOutputLine> getReactions() {
        return reactions;
    }
}
