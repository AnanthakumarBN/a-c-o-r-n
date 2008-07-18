/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package acorn.worker.amkfba;

/**
 *
 * @author kuba
 */
public class AmkfbaException extends Exception {

    /**
     * Creates a new instance of <code>AmkfbaException</code> without detail message.
     */
    public AmkfbaException() {
    }


    /**
     * Constructs an instance of <code>AmkfbaException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public AmkfbaException(String msg) {
        super(msg);
    }
}
