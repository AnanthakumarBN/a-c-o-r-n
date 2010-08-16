/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.dbStructs;

import java.io.Serializable;

/**
 *
 * @author jsroka
 */
public class VisStruct implements Serializable {
    private boolean shared;
    private boolean canModify;

    public VisStruct() {
    }

    public VisStruct(boolean shared, boolean canModify) {
        this.shared = shared;
        this.canModify = canModify;
    }

    public boolean isCanModify() {
        return canModify;
    }

    public void setCanModify(boolean canModify) {
        this.canModify = canModify;
    }

    public boolean isShared() {
        return shared;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
    }

}
