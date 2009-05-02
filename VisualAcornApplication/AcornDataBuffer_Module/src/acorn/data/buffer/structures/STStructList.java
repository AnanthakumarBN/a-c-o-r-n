/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package acorn.data.buffer.structures;

import java.util.List;
import org.dbStructs.NameStruct;

/**
 *
 * @author markos
 */
public class STStructList {

    private List<NameStruct> sourceList;
    private List<NameStruct> targetList;

    public STStructList() {
        sourceList = null;
        targetList = null;
    }

    public STStructList(List<NameStruct> sourceList, List<NameStruct> targetList) {
        this.sourceList = sourceList;
        this.targetList = targetList;
    }

    public List<NameStruct> getSourceList() {
        return sourceList;
    }

    public void setSourceList(List<NameStruct> sourceList) {
        this.sourceList = sourceList;
    }

    public List<NameStruct> getTargetList() {
        return targetList;
    }

    public void setTargetList(List<NameStruct> targetList) {
        this.targetList = targetList;
    }

    @Override
    public String toString() {
        String str = "Source List: \n";
        if (this.sourceList == null) {
            str.concat("\n NULL");
        } else {
            for (NameStruct struct : sourceList) {
                str = str.concat(struct.toString() + "\n");
            }
        }
        if (this.targetList == null) {
            str.concat("\n NULL");
        } else {
            str = str.concat("Target List: \n");
            for (NameStruct struct : targetList) {
                str = str.concat(struct.toString() + "\n");
            }
        }
        return str;
    }
}
