/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package acorn.webservice;

import acorn.db.EReaction;
import acorn.db.ESpecies;
import java.util.Arrays;
import java.util.List;
import org.dbStructs.NameStruct;

/**
 *
 * @author markos
 */
public class StructArrayGen {

    public static NameStruct[] getSortedSpeciesArray(List<ESpecies> list){
        NameStruct[] structArray = new NameStruct[list.size()];
        int i = 0;
        for(ESpecies spec: list){
            structArray[i++] = new NameStruct(spec.getName(),spec.getSid());
        }
        Arrays.sort(structArray);
        return structArray;
    }

    public static NameStruct[] getSortedReactionsArray(List<EReaction> list, String modelName){
        NameStruct[] structArray = new NameStruct[list.size()];
        int i = 0;
        for(EReaction spec: list){
            structArray[i++] = new NameStruct(spec.getName(),spec.getSid());
        }
        Arrays.sort(structArray);
        return structArray;
    }
}
