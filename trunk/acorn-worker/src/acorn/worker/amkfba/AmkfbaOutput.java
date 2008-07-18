package acorn.worker.amkfba;

import acorn.db.ECommonResults;
import java.util.LinkedList;

/**
 *
 * @author kuba
 */
public class AmkfbaOutput {

    private ECommonResults commonResults;

    public AmkfbaOutput() {

        commonResults = new ECommonResults();
    }

    public void setGrowthRate(Float growthRate) {
        commonResults.setGrowthRate(growthRate);
    }

    public void setOptimizationStatus(String optimizationStatus) {
        commonResults.setStatus(optimizationStatus);
    }

    public ECommonResults getCommonResults() {
        return commonResults;
    }
}
