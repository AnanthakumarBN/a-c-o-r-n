package acorn.task;

/**
 * RscanResultRow
 * @author lukasz
 */
public class RscanResultRow {
    private String reactionName;
    private String optStatus;
    private float growthRate;
    private String formula;
    
    RscanResultRow() {
    }
    
    RscanResultRow(String reactionName, String optStatus, float growthRate, String formula) {
        this.reactionName = reactionName;
        this.optStatus = optStatus;
        this.growthRate = growthRate;
        this.formula = formula;
    }

    public String getReactionName() {
        return reactionName;
    }

    public void setReactionName(String reactionName) {
        this.reactionName = reactionName;
    }

    public String getOptStatus() {
        return optStatus;
    }

    public void setOptStatus(String optStatus) {
        this.optStatus = optStatus;
    }

    public float getGrowthRate() {
        return growthRate;
    }

    public void setGrowthRate(float growthRate) {
        this.growthRate = growthRate;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }
}
