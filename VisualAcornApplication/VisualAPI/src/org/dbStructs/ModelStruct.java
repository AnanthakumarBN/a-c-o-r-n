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
public class ModelStruct implements Comparable, Serializable {

    private int id;
    private String name;

    public ModelStruct() {
        id = -1;
        name = null;
    }


    public ModelStruct(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

        @Override
    public String toString() {
        return name;
    }

    public int compareTo(Object o) {
        if (o instanceof ModelStruct) {
            return this.name.compareTo(((ModelStruct) o).getName());
        }
        return 0;
    }

    @Override
    public boolean equals(Object struct) {
        if (struct instanceof ModelStruct) {
            return (this.id == ((ModelStruct) struct).getId());
        }
        return false;

    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }

}
