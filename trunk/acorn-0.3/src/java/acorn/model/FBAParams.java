/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package acorn.model;

/**
 *
 * @author sg236027
 */
public class FBAParams extends Params{
    
    private boolean reactionTarget;
    private boolean speciesTarget;
    public String target;
        
    public FBAParams(){
        reactionTarget = false;
        speciesTarget = false;
    }
    
    @Override
    public void setReactionTarget(String val){
        target = val;
        speciesTarget = false;
        reactionTarget = true;
    }
    
    @Override
    public void setSpeciesTarget(String val){
        target = val;
        reactionTarget = false;
        speciesTarget = true;
    }
    
    public boolean isSetTarget(){
        return (reactionTarget || speciesTarget);
    }
    
    public boolean isReactionTarget(){
        return reactionTarget;
    }
    
    public boolean isSpeciesTarget(){
        return speciesTarget;
    }
    
    @Override
    public boolean isEmpty(){
        return !(this.isSetTarget());
    }
    
    public int getTargetIndex(){
        if (this.isReactionTarget()){
            return Integer.decode(target);
        }
        else{
            //species have negative ( < 0) index
            return -(Integer.decode(target));
        }
    }
    
    public String getTarget() {
        return target;
    }

}
