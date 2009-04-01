/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package acorn.webservice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.dbStructs.NameStruct;

/**
 *
 * @author markos
 */
public class ListSorter {

    static List<NameStruct> sortNameStructList(List<NameStruct> list){
        NameStruct[] array = new NameStruct[list.size()];
        int i = 0;
        for(NameStruct elem : list){
            array[i++] = elem;
        }
        Arrays.sort(array);
        List<NameStruct> sortedList = new ArrayList<NameStruct>(array.length);
        i = 0;
        for(NameStruct elem : array){
            sortedList.add(i++, elem);
        }
        return sortedList;
    }

    static List<String> sortStringList(List<String> list){
        String[] array = new String[list.size()];
        int i = 0;
        for(String elem : list){
            array[i++] = elem;
        }
        Arrays.sort(array);
        List<String> sortedList = new ArrayList<String>(array.length);
        i = 0;
        for(String elem : array){
            sortedList.add(i++, elem);
        }
        return sortedList;
    }

}
