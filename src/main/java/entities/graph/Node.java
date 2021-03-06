/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities.graph;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mariastr
 */
public class Node {
    
    private Long id;
    private String title;
    List<Edge> edges;
    
    
    public Node(Long id, String title){
        this.id = id;
        this.title = title;
        edges = new ArrayList<>();
    }
    
    
    public void addEdge(Edge e){
        edges.add(e);
    }
    
    
    
    public String toString(){
        String s = "";
        s = s + id + "[";
        for(Edge e : edges){
            s = s + e.toString() + " ";
        }
        s = s + "]";
        return s;
    }

    public List<Edge> getEdges() {
        return edges;
    }
    
    public void removeEdge(String s){
        int i = 0;
        for(Edge e : edges){
            if(s.equalsIgnoreCase(e.getId())){
                break;
            }
            i++;
        }
        if(i < edges.size()){
            edges.remove(i);
        }
    }
    
    public void keepEdges(List<String> keep){
        List<Edge> rem = new ArrayList<>();
        
        for(Edge e : edges){
            if(!keep.contains(e.getId())){
                rem.add(e);
            }
        }
        
        edges.removeAll(rem);
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
    
    
}
