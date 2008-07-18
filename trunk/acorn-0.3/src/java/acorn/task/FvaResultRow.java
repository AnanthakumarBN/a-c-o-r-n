package acorn.task;

/**
 * FvaResultRow
 * @author lukasz
 */
public class FvaResultRow {
    private String reactionName;
    private float minFlux;
    private float maxFlux;
    private String formula;
    
    FvaResultRow() {
    }
    
    FvaResultRow(String reactionName, float minFlux, float maxFlux, String formula) {
        this.reactionName = reactionName;
        this.minFlux = minFlux;
        this.maxFlux = maxFlux;
        this.formula = formula;
    }

    public String getReactionName() {
        return reactionName;
    }

    public float getMinFlux() {
        return minFlux;
    }

    public float getMaxFlux() {
        return maxFlux;
    }

    public String getFormula() {
        return formula;
    }

}
