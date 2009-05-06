/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.visualapi;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.dbStructs.NameStruct;

/**
 *
 * @author markos
 * represents Place or Transition in GraphScene
 */
public abstract class VisNode {

    protected String name;
    protected String sid;
    private Point location;
    private String xmlSid;
    /** edges in which this is source i.e.:
    sNode             <----(edge)       this
     *      |_sourceNode                        |_targetNodes
     *          |_this                            |_sNode

     */
    private HashSet<VisNode> sourceNodes;
    /** target nodes in connection with this */
    private HashSet<VisNode> targetNodes;

    public VisNode() {
        sourceNodes = new HashSet<VisNode>(0);
        targetNodes = new HashSet<VisNode>(0);
        this.name = null;
        this.sid = null;
        location = null;
        xmlSid = null;
    }

    public VisNode(String name, String sid, Point location, String xmlSid) {
        this.name = name;
        this.sid = sid;
        this.location = location;
        this.xmlSid = xmlSid;
        sourceNodes = new HashSet<VisNode>(0);
        targetNodes = new HashSet<VisNode>(0);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPlace() {
        return false;
    }

    public boolean isTransition() {
        return false;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getSid() {
        return sid;
    }

    public String getXmlSid() {
        return xmlSid;
    }

    public void setXmlSid(String xmlSid) {
        this.xmlSid = xmlSid;
    }

    public Point getLocation() {
        return location;
    }

    public void setLocation(Point location) {
        this.location = location;
    }

    public void setSourceNodes(HashSet<VisNode> sourceNodes) {
        this.sourceNodes = sourceNodes;
    }

    public void setTargetNodes(HashSet<VisNode> targetNodes) {
        this.targetNodes = targetNodes;
    }

    @Override
    public String toString() {
        return sid;
    }

    public void addTargetNode(VisNode node) {
        targetNodes.add(node);
        node.getSourceNodes().add(this);
    }

    public void addSourceNode(VisNode node) {
        sourceNodes.add(node);
        node.getTargetNodes().add(this);
    }

    public boolean removeTargetNode(VisNode node) {
//        node.getSourceNodes().remove(this);
        return targetNodes.remove(node);
    }

    public boolean removeSourceNode(VisNode node) {
//        node.getTargetNodes().remove(this);
        return sourceNodes.remove(node);
    }

    /**
     * 
     * @param nameStructList this could be named by list of nameStructList
     * @return return only these NameStructs which are not used in other source/targetNodes in context of this
     */
    public List<NameStruct> removeUsedNodes(List<NameStruct> nameStructList) {
        ArrayList<NameStruct> finalStructList = new ArrayList<NameStruct>(nameStructList);
        HashSet<NameStruct> allUsedNameStructs = new HashSet<NameStruct>(0);

        for (VisNode target : targetNodes) {
            allUsedNameStructs.addAll(target.getSourceNodesStruct());
        }
        for (VisNode source : sourceNodes) {
            allUsedNameStructs.addAll(source.getTargetNodesStruct());
        }

        finalStructList.removeAll(allUsedNameStructs);
        if (isConnected() && this.sid != null && nameStructList.contains(this.getStruct()) && !isStructUsed()) {
            finalStructList.add(this.getStruct());
        }
        return finalStructList;
    }

    /**
     *
     * @return true if all source and target nodes do not used the same struct as this, false in other case
     */
    private boolean isStructUsed() {
        NameStruct myStruct = this.getStruct();
        for (VisNode target : targetNodes) {
            ArrayList<NameStruct> sourceList = target.getSourceNodesStruct();
            if (sourceList.indexOf(myStruct) != sourceList.lastIndexOf(myStruct)) {
                return true;
            }
        }
        for (VisNode source : sourceNodes) {
            ArrayList<NameStruct> targetList = source.getTargetNodesStruct();
            if (targetList.indexOf(myStruct) != targetList.lastIndexOf(myStruct)) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @return true if node is connected with other node, false in other case
     */
    private boolean isConnected() {
        return (sourceNodes.size() != 0 || targetNodes.size() != 0);
    }

    public void removeTargetConnections() {
        for (VisNode node : targetNodes) {
            node.removeSourceNode(this);
        }
    }

    /** removes node from nodes that are targets in connections with this
    sNode           <----(edge)       this
     *      |_targetNodes                        |_sourceNodes
     *          |_this                             |_sNode

     */
    public void removeSourceConnections() {
        for (VisNode node : sourceNodes) {
            node.removeTargetNode(this);
        }
    }

    public void removeAllConnections() {
        removeSourceConnections();
        removeTargetConnections();
    }

    public HashSet<VisNode> getSourceNodes() {
        return sourceNodes;
    }

    public HashSet<VisNode> getTargetNodes() {
        return targetNodes;
    }

    public String toStringAll() {
        String sourceNodesString = new String("");
        for (VisNode node : sourceNodes) {
            sourceNodesString += ", " + node.toString();
        }
        String targetNodesString = new String("");
        for (VisNode node : targetNodes) {
            targetNodesString += ", " + node.toString();
        }

        return "================\n Node: " + this.getSid() + "\n Source nodes: " + sourceNodesString + "\n Target nodes: " + targetNodesString;
    }

    /*
     * @return list of sid of nodes that are target nodes in connection with this
     */
    public ArrayList<String> getTargetNodesSid() {
        ArrayList<String> targetSids = new ArrayList<String>(countNoNullNodes(targetNodes));
        for (VisNode node : targetNodes) {
            if (node.getSid() != null) {
                targetSids.add(node.getSid());
            }
        }
        return targetSids;
    }

    public ArrayList<NameStruct> getTargetNodesStruct() {
        ArrayList<NameStruct> targetStructList = new ArrayList<NameStruct>(countNoNullNodes(targetNodes));
        for (VisNode node : targetNodes) {
            if (node.getSid() != null) {
                targetStructList.add(new NameStruct(node.getName(), node.getSid()));
            }
        }
        return targetStructList;
    }

    /*
     * @return sid list of nodes that are target nodes in connection with this
     */
    public ArrayList<String> getSourceNodesSid() {
        ArrayList<String> sourceSids = new ArrayList<String>(countNoNullNodes(sourceNodes));
        for (VisNode node : sourceNodes) {
            if (node.getSid() != null) {
                sourceSids.add(node.getSid());
            }
        }
        return sourceSids;
    }

    public ArrayList<NameStruct> getSourceNodesStruct() {
        ArrayList<NameStruct> sourceStructList = new ArrayList<NameStruct>(countNoNullNodes(sourceNodes));
        for (VisNode node : sourceNodes) {
            if (node.getSid() != null) {
                sourceStructList.add(new NameStruct(node.getName(), node.getSid()));
            }
        }
        return sourceStructList;
    }

    public int countNoNullNodes(HashSet<VisNode> nodes) {
        int i = 0;
        for (VisNode node : nodes) {
            if (node.getSid() != null) {
                i++;
            }
        }
        return i;
    }

    public NameStruct getStruct() {
        return new NameStruct(this.name, this.sid);
    }

    public void removeNullsFromSets(){
        this.targetNodes.remove(null);
        this.sourceNodes.remove(null);
    }
}

