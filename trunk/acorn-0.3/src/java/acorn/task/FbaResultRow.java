package acorn.task;

/**
 * FbaResultRow
 * @author lukasz
 */
public class FbaResultRow {
    private String reactionName;
    private float flux;
    private String formula;
    private float lowerBound;
    private float upperBound;
    
    FbaResultRow() {
    }
    
    FbaResultRow(String reactionName, float flux, String formula, float lowerBound, float upperBound) {
        this.reactionName = reactionName;
        this.flux = flux;
        this.formula = formula;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    public String getReactionName() {
        return reactionName;
    }

    public void setReactionName(String reactionName) {
        this.reactionName = reactionName;
    }

    public float getFlux() {
        return flux;
    }

    public void setFlux(float flux) {
        this.flux = flux;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public float getLowerBound() {
        return lowerBound;
    }

    public void setLowerBound(float lowerBound) {
        this.lowerBound = lowerBound;
    }

    public float getUpperBound() {
        return upperBound;
    }

    public void setUpperBound(float upperBound) {
        this.upperBound = upperBound;
    }

}
