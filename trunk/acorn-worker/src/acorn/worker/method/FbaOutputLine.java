
package acorn.worker.method;

/**
 *
 * @author kuba
 */
public class FbaOutputLine {
    private String reactionName;
    private Float flux;
    public FbaOutputLine(String reactionName, Float flux)
    {
        this.reactionName = reactionName;
        this.flux = flux;
    }

    public String getReactionName() {
        return reactionName;
    }

    public Float getFlux() {
        return flux;
    }
    
}
