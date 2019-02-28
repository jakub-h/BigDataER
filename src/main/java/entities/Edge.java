/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Graph;

/**
 *
 * @author mariastr
 */
public class Edge {
    private String id;
    private int commonBlocks = 1;
    private double jaccard;

    
    public Edge(String id){
        this.id = id;
    }
    
    
    public String getId() {
        return id;
    }

    public int getCommonBlocks() {
        return commonBlocks;
    }

    public double getJaccard() {
        return jaccard;
    }
    
    
    public String toString(){
        String s = id;
        return s;
    }
    
    public void addCommonBlock(){
        commonBlocks++;
    }

    public void setJaccard(double jaccard) {
        this.jaccard = jaccard;
    }
}
