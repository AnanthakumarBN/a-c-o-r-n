/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dbStructs;

import java.io.Serializable;

/**
 *
 * @author markos
 */
public class NameStruct implements Comparable, Serializable {

    private String name;
    private String sid;

    public NameStruct() {
        this.name = null;
        this.sid = null;
    }

    public NameStruct(String name, String sid) {
        this.name = name;
        this.sid = sid;
    }

    @Override
    public String toString() {
        return sid;
    }

    public int compareTo(Object o) {
        if (o instanceof NameStruct) {
            return this.sid.compareTo(((NameStruct) o).getSid());
        }
        return 0;
    }

    @Override
    public boolean equals(Object struct) {
        if (struct instanceof NameStruct) {
            return this.getSid().equals(((NameStruct) struct).getSid());
        }
        return false;

    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.sid != null ? this.sid.hashCode() : 0);
        return hash;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }
}
