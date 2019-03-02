/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author mariastr
 */
public class Graph {
    List<Node> nodes;
    
    Map<Long,Node> nodeEntry;
    
    Map<String, Edge> edges;
    
    public Graph(){
        nodes = new ArrayList<>();
        nodeEntry = new HashMap<>();
        edges = new HashMap<>();
    }
    
    
    public void addNode(Long id){
        
        if(nodeEntry.containsKey(id)){
            return;
        }
        Node n = new Node(id);
        nodes.add(n);
        nodeEntry.put(id, n);
    }
    
    
    public void addEdge(Long id1 ,Long id2){
        String edgeId = getEdgeId(id1,id2);
        if("".equals(edgeId)){
            System.out.println("Trying to create edge to itself");
            return;
        }
        if(edges.containsKey(edgeId)){
            System.out.println("Edge already exists: " + edgeId);
            Edge e = edges.get(edgeId);
            e.addCommonBlock();
            return;
        }
        Edge e = new Edge(edgeId);
        edges.put(edgeId, e);
        Node n = nodeEntry.get(id1);
        Node m = nodeEntry.get(id2);
        n.addEdge(e);
        m.addEdge(e);
    }

    private String getEdgeId(Long id1, Long id2) {
        if(id1 > id2){
            return id2 + "_" + id1;
        }else if(id2 > id1){
            return id1 + "_" + id2;
        }else{
            return "";
        }
    }
    
    
    public String toSrting(){
        String s = "";
        for(Node n : nodeEntry.values()){
            s = s + n.toString() + "\n";
        }
        
        return s;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public Map<Long, Node> getNodeEntry() {
        return nodeEntry;
    }

    public Map<String, Edge> getEdges() {
        return edges;
    }

    public void setEdges(Map<String, Edge> edges) {
        this.edges = edges;
    }
    
    public void printEdges(){
        for(String s : edges.keySet()){
            System.out.println(s);
        }
    }
        
}
