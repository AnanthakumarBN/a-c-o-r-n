/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package acorn.data.buffer.structures;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import org.dbStructs.NameStruct;

/**
 *
 * @author markos
 ** stores species and transaction data (sid and name of transaciton, species) that is downloaded from server */
public class SpeciesReactionBuffer {

    /**             -----sourceSet -> {reaction1, reaction2, ...}
     * species1 ---|_____targetList -> {reaction11, reaction21, ...}
     *
     * species2 ...
     *
     * i.e.                 /---> reaction21
     * reaction1 ---> species1 ----> reaction11
     *                 /
     *   reaction2--->/
     */
    private HashMap<NameStruct, STStructList> reactionsForSpecies;
    private HashMap<NameStruct, STStructList> speciesForReaction;

    public SpeciesReactionBuffer() {
        reactionsForSpecies = new HashMap<NameStruct, STStructList>();
        speciesForReaction = new HashMap<NameStruct, STStructList>();
    }

    /**
     * @param speciesKey NameStruct of species
     * @param isSource source/target reactions for speciesKey species
     * @return List of reactions which are source/target reactions for speciesKey
     */
    public List<NameStruct> getListOfReactions(NameStruct speciesKey, boolean isSource) {
        if (!reactionsForSpecies.containsKey(speciesKey)) {
            return null;
        }
        if (isSource) {
            return reactionsForSpecies.get(speciesKey).getSourceList();
        } else {
            return reactionsForSpecies.get(speciesKey).getTargetList();
        }
    }

    /**
     * @param reactionKey
     * @param isSource
     * @return List of species which are source/target species for reactionKey reaction
     */
    public List<NameStruct> getListOfSpecies(NameStruct reactionKey, boolean isSource){
        if(!speciesForReaction.containsKey(reactionKey)){
            return null;
        }
        if(isSource){
            return speciesForReaction.get(reactionKey).getSourceList();
        }else{
            return speciesForReaction.get(reactionKey).getTargetList();
        }
    }

    /** returns null if List1 and List2 is null. returrns List1 if List2 is nullor
     */
    private List intersect(List List1, List List2) {
        if (List1 == null) {
            if (List2 == null) {
                return null;
            } else {
                return new ArrayList(List2);
            }
        } else {
            ArrayList hList1 = new ArrayList(List1);
            if (List2 != null) {
                hList1.retainAll(List2);
            }
            return hList1;
        }
    }

    /** adds or update STSruct for reactionsForSpecies map or SpeciesForReaction map */
    public void addNameStructList(NameStruct key, List<NameStruct> strList, boolean reactionKey, boolean isSource) {
        HashMap<NameStruct, STStructList> map = null;
        if (reactionKey) {
            map = speciesForReaction;
        } else {
            map = reactionsForSpecies;
        }

        if (map.containsKey(key)) {
            if (isSource) {
                map.get(key).setSourceList(strList);
            } else {
                map.get(key).setTargetList(strList);
            }

        } else {
            STStructList list = null;
            if (isSource) {
                list = new STStructList(strList, null);
            } else {
                list = new STStructList(null, strList);
            }

            map.put(key, list);
        }
    }

    /**
     * @param keys
     * @param reactionKeys
     * @param isSourceList
     * @return list of NameStruct which are not download  - aren't in buffer structure
     */
    public List<NameStruct> getStructsToDownload(List<NameStruct> keys, boolean reactionKeys, boolean isSourceList) {
        List<NameStruct> downloadList = new ArrayList<NameStruct>(0);
        HashMap<NameStruct, STStructList> map = null;
        if (reactionKeys) {
            map = this.speciesForReaction;
        } else {
            map = this.reactionsForSpecies;
        }
        for (NameStruct key : keys) {
            if (!map.containsKey(key)) {
                downloadList.add(key);
                continue;
            } else {
                if (isSourceList && map.get(key).getSourceList() == null) {
                    downloadList.add(key);
                } else if (!isSourceList && map.get(key).getTargetList() == null) {
                    downloadList.add(key);
                }
            }
        }
        return downloadList;
    }

    public List<NameStruct> getDownloadedStructs(List<NameStruct> keys, boolean reactionKeys, boolean isSource) {
        List<NameStruct> downloadedList = new ArrayList<NameStruct>(0);
        HashMap<NameStruct, STStructList> map = null;
        if (reactionKeys) {
            map = this.speciesForReaction;
        } else {
            map = this.reactionsForSpecies;
        }
        for (NameStruct key : keys) {
            if (map.containsKey(key)) {
                if (isSource && map.get(key).getSourceList() != null) {
                    downloadedList.add(key);
                } else if (!isSource && map.get(key).getTargetList() != null) {
                    downloadedList.add(key);
                }
            }
        }
        return downloadedList;
    }

    /**checks if in speciesForReaction/reactionsForSpecies hashMap under key source/target List contains values collection
     *
     * @param key
     * @param values
     * @param isReactionMap
     * @param isSource
     * @return true if values are in List in proper HashMap under key, if not false
     */
    public boolean containsAll(NameStruct key, Collection<NameStruct> values, boolean isReactionMap, boolean isSource) {
        HashMap<NameStruct, STStructList> map = null;
        if (isReactionMap) {
            map = speciesForReaction;
        } else {
            map = reactionsForSpecies;
        }
        if (!map.containsKey(key)) {
            return false;
        }
        STStructList stsStructList = map.get(key);
        if (isSource) {
            return stsStructList.getSourceList().containsAll(values);
        } else {
            return stsStructList.getTargetList().containsAll(values);
        }
    }

    public boolean containsKey(NameStruct key, boolean isReactionMap, boolean isSource){
        HashMap<NameStruct, STStructList> map = null;
        if (isReactionMap) {
            map = speciesForReaction;
        } else {
            map = reactionsForSpecies;
        }
        if (!map.containsKey(key)) {
            return false;
        }
        return true;
    }

    public List<NameStruct> getNotValidNameStructs(NameStruct key, Collection<NameStruct> values, boolean isReactionMap, boolean isSource) throws BadKeyInBufferStruct{
        HashMap<NameStruct, STStructList> map = null;
        ArrayList<NameStruct> badNameStructs = new ArrayList<NameStruct>(values);
        if (isReactionMap) {
            map = speciesForReaction;
        } else {
            map = reactionsForSpecies;
        }
        if (!map.containsKey(key)) {
            if(isReactionMap){
            throw new BadKeyInBufferStruct(key.toString() + "is not valid reaction.");
            }else{
                throw new BadKeyInBufferStruct(key.toString()+ "is not valid species.");
            }

        }
        STStructList stsStructList = map.get(key);
        if (isSource) {
            badNameStructs.removeAll(stsStructList.getSourceList());
        } else {
            badNameStructs.removeAll(stsStructList.getTargetList());
        }
        return badNameStructs;
    }

    @Override
    public String toString() {
        String string = "SPECIES REACTION BUFFER:\n : speciesForReaction";
        for (NameStruct reaction : speciesForReaction.keySet()) {
            string = string.concat("========\nREACTION: " + reaction.toString() + "\n");
            string = string.concat(speciesForReaction.get(reaction).toString());
        }
        string = string.concat("\n =============\nreactionsForSpecies \n ============");
        for (NameStruct species : reactionsForSpecies.keySet()) {
            string = string.concat("========\nSPECIES: " + species.toString() + "\n");
            string = string.concat(reactionsForSpecies.get(species).toString());
        }
        return string;
    }
}