/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package acorn.model;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.component.UIComponent;
import javax.faces.validator.ValidatorException;
import java.lang.IllegalArgumentException;
import acorn.db.*;
import java.lang.Comparable;
import acorn.errorHandling.ErrorBean;
import java.util.Map;
import java.util.concurrent.Semaphore;

/**
 *
 * @author dl236088
 */
public class Condition implements Comparable {
    
    private EReaction reaction;
    private Map<Integer, EBounds> bounds;
    private Integer defaultBoundsId;
    private Integer lastBoundsId;
    private String index;
    private String reactionFormula;
    private String oneLineReactionFormula;
    private String geneFormula;
    private Semaphore boundsMutex;
    private Semaphore validatorMutex;
    private Double lower;
    private Double upper;
    
    Condition (Map<Integer,EBounds> boundsMap, Integer defaultBoundsId, EReaction reaction, int index, String reactionFormula, String oneLineReactionFormula, String geneFormula)
    {
        super();
        this.reaction = reaction;
        this.bounds = boundsMap;
        this.defaultBoundsId = defaultBoundsId;
        this.lastBoundsId = defaultBoundsId;
        this.index = Integer.toString(index);
        this.reactionFormula = reactionFormula;
        this.oneLineReactionFormula = oneLineReactionFormula;
        this.geneFormula = geneFormula;
        this.boundsMutex = new Semaphore(1);
        this.validatorMutex = new Semaphore(1);
    }
    
    public int compareTo (Object c) {
        return reaction.getId().compareTo(((Condition)c).reaction.getId());
    }
    
    public String getReactionName() {
        return reaction.getName();
    }
    
    public String getReactionFormula() {
        return reactionFormula;
    }

    public String getReactionFormulaOneLine() {
        return oneLineReactionFormula;
    }

    public EReaction getReaction() {
        return reaction;
    }
    
    public EBounds getBounds() {
        return bounds.get(defaultBoundsId);
    }
    
    public Map<Integer, EBounds> getBoundsMap() {
        return bounds;
    }

    public String getLowerBound() {
        try {
            return Float.toString(bounds.get(lastBoundsId).getLowerBound());
        } catch (Exception e) { // bounds.get(lastBoundsId) moze == null
            ErrorBean.printStackTrace(e);
            return "error";
        }
    }

    public void setLowerBound(String lowerBound) {
        try { 
            this.bounds.get(lastBoundsId).setLowerBound(Float.parseFloat(lowerBound)); 
        } catch (Exception e) { ErrorBean.printStackTrace(e); }
    }

    public String getUpperBound() {
        try {
            return Float.toString(bounds.get(lastBoundsId).getUpperBound());
        } catch (Exception e) { // bounds.get(lastBoundsId == null
            ErrorBean.printStackTrace(e);
            return "error";
        }
    }

    public void setUpperBound(String upperBound) {
        try { 
            this.bounds.get(lastBoundsId).setUpperBound(Float.parseFloat(upperBound)); 
        } catch (Exception e) { ErrorBean.printStackTrace(e); }
    }
    
    public Integer getPreHidden() {
        return defaultBoundsId;
    }
    
    public void setPreHidden(Integer bId) {
        boundsMutex.acquireUninterruptibly();
        lastBoundsId = bId;
    }
    
    public Integer getPostHidden() {
        return defaultBoundsId;
    }
    
    public void setPostHidden(Integer bId) {
        try {
            if(!bId.equals(lastBoundsId)) ErrorBean.printStackTrace(new Exception());
        } finally {
            lastBoundsId = defaultBoundsId;
            boundsMutex.release();
        }
    }
    

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }
    
    public boolean equals(Condition cond){
        try {
            return this.bounds.get(defaultBoundsId).getLowerBound() == cond.getBounds().getLowerBound() && 
                this.bounds.get(defaultBoundsId).getUpperBound() == cond.getBounds().getUpperBound();
        } catch (Exception e) { ErrorBean.printStackTrace(e); return false; }
    }
    
    
    public Condition copy(Map<Integer, EBounds> bounds_copy) {
        return new Condition(bounds_copy, defaultBoundsId, reaction, Integer.parseInt(index), reactionFormula, oneLineReactionFormula, geneFormula);
    }
    
    public void lowerHiddenPreValidator(FacesContext context, UIComponent toValidate, Object value)
    {
        validatorMutex.acquireUninterruptibly();
        try {
            Integer i;
            if(value instanceof String) i = Integer.parseInt((String) value);
            else if(value instanceof Integer) i = (Integer) value;
            else throw new IllegalArgumentException();
            lastBoundsId = i;
        } catch (Exception e) {
            ErrorBean.printStackTrace(e);
        }
    }
    
    public void upperHiddenPostValidator(FacesContext context, UIComponent toValidate, Object value)
    {
        try {
            Integer i;
            if(value instanceof String) i = Integer.parseInt((String) value);
            else if(value instanceof Integer) i = (Integer) value;
            else throw new IllegalArgumentException();
            if(!lastBoundsId.equals(i)) ErrorBean.printStackTrace(new Exception());
        } finally {
            validatorMutex.release();
        }
    }

    public void lowerBoundValidator(FacesContext context, UIComponent toValidate, Object value) {
        try {
            if(value instanceof String) lower = Double.parseDouble((String) value);
            else if(value instanceof Double) lower = (Double) lower;
            else ErrorBean.printStackTrace(new IllegalArgumentException());
        } catch (NumberFormatException e) {            
            context.addMessage(toValidate.getClientId(context),
                    new FacesMessage("Please specify a valid number between -999999.0 and 999999.0"));
            throw new ValidatorException(new FacesMessage());
        }   
        if (!((-999999.0) <= lower && lower <= 999999.0)) {
            context.addMessage(toValidate.getClientId(context),
                new FacesMessage("Please specify a valid number between -999999.0 and 999999.0"));
            throw new ValidatorException(new FacesMessage());
        }
    }
    
    public void upperBoundValidator(FacesContext context, UIComponent toValidate, Object value) {
        try { 
            if(value instanceof String) upper = Double.parseDouble((String) value);
            else if(value instanceof Double) upper = (Double) upper;
            else ErrorBean.printStackTrace(new IllegalArgumentException()); 
        } catch (NumberFormatException e) {
            context.addMessage(toValidate.getClientId(context),
                    new FacesMessage("Please specify a valid number between -999999.0 and 999999.0"));
            throw new ValidatorException(new FacesMessage());
        }   
        if (!((-999999.0) <= upper && upper <= 999999.0)) {
            context.addMessage(toValidate.getClientId(context),
                new FacesMessage("Please specify a valid number between -999999.0 and 999999.0"));
            throw new ValidatorException(new FacesMessage());
        }        
        if (lower != null) {
            if(upper < lower) {                
                context.addMessage(toValidate.getClientId(context),
                    new FacesMessage("Upper bound has to be greater than lower bound or equal"));
                throw new ValidatorException(new FacesMessage());
            }            
        }
    }
    
    public Boolean getValid()
    {
        try {
            return bounds.get(lastBoundsId).getLowerBound() < bounds.get(lastBoundsId).getUpperBound();
        } catch (Exception e) {
            ErrorBean.printStackTrace(e);
            return false;
        }
    }
    
    public String getStyleClass() {
        return getValid()? "":"error";
    }

    public String getGeneFormula() {
        return geneFormula;
    }

    public void setGeneFormula(String geneFormula) {
        this.geneFormula = geneFormula;
    }
}
