/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MetaBlocking;

import Blocking.Block;
import Blocking.BlockCollection;
import Graph.Edge;
import Graph.Graph;
import Graph.Node;
import entities.EntityDescription;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author mariastr
 */
public class Metablocking {

    BlockCollection bc;
    Map<Long, List<Block>> entityIndex;
    Map<Long, EntityDescription> entityEntry;
    Graph gr;
    int k = 0;

    public Metablocking(BlockCollection bc) {
        this.bc = bc;
        entityIndex = new HashMap<>();
        entityEntry = new HashMap<>();
        createEntityIndex();
    }

    private void createEntityIndex() {
        List<Block> list = bc.getList();
        for (Block b : list) {
            List<EntityDescription> ent = new ArrayList<>();
            ent.addAll(b.getInnerBlock1());
            ent.addAll(b.getInnerBlock2());
            for (EntityDescription ed : ent) {
                Long id = ed.getId();
                if (!entityEntry.containsKey(id)) {
                    entityEntry.put(id, ed);
                }
                List<Block> bList;
                if (entityIndex.containsKey(id)) {
                    bList = entityIndex.get(id);
                } else {
                    bList = new ArrayList<>();
                    entityIndex.put(id, bList);
                }
                bList.add(b);
            }
        }
//        
//        for(Long edId : entityIndex.keySet()){
//            System.out.println(edId);
//            String str = "[ ";
//            List<Block> bList = entityIndex.get(edId);
//            for(Block b : bList){
//                str = str + b.getBlockId() + " ";
//            }
//            str = str + " ]";
//            System.out.println(str);
//        }
    }

    public Graph createGraph() {
        gr = new Graph();
        List<Block> list = bc.getList();
        double local = 0;
        int KB1 = bc.getKB1();
        int KB2 = bc.getKB2();
        int size = KB1 + KB2;
        
        for (Block b : list) {
            List<EntityDescription> inner1 = b.getInnerBlock1();
            List<EntityDescription> inner2 = b.getInnerBlock2();
            //int size = ent.size();
            local = local + (double)(inner1.size() + inner2.size()) / (double)size;
            for (EntityDescription in1 : inner1) {
                gr.addNode(in1.getId());
                for (EntityDescription in2 : inner2) {
                    gr.addNode(in2.getId());
                    gr.addEdge(in1.getId(), in2.getId());
                }
            }
        }
        k = (int)Math.floor(local);
        System.out.println(gr.toSrting());
        return gr;
    }

    
    /**
     * Done during creation of entityIndex
     * @param g
     * @return 
     */
    public Graph edgeWeightinhCommonBlocks(Graph g) {

        List<Block> list = bc.getList();

//        for(Block b : list){
//            List<EntityDescription> ent = b.getEnt();
//            int size = ent.size();
//            
//        }
        Map<String, Edge> edges = gr.getEdges();
        for (String s : edges.keySet()) {
            System.out.println(s + " " + edges.get(s).getCommonBlocks());
        }
        return gr;
    }

    public Graph edgeWeightingJaccard(Graph g) {
        Map<String, Edge> edges = gr.getEdges();
        for (String s : edges.keySet()) {
            String nodes[] = s.split("_");
            Long id1 = Long.parseLong(nodes[0]);
            Long id2 = Long.parseLong(nodes[1]);
            List<Block> list1 = new ArrayList<>();
            list1.addAll(entityIndex.get(id1));
            List<Block> list2 = new ArrayList<>();
            list2.addAll(entityIndex.get(id2));
            int size1 = list1.size();
            int size2 = list2.size();
            list1.retainAll(list2);
            double weight = (double) list1.size() / (size1 + size2);
            Edge e = edges.get(s);
            weight = round(weight, 3);
            e.setJaccard(weight);
            System.out.println("For edge: " + s + "     original size1: " + size1 + "   size2: " + size2 + "     common: " + list1.size() + "     wieght: " + weight);
        }
        return gr;
    }

    private double round(double number, int decimalPlace) {
        BigDecimal bd = new BigDecimal(number);
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.doubleValue();
    }

    public Graph weightEdgePruning(Graph g, String similarity) {
        double threshold = findThreshold(g, similarity);
        System.out.println("Threshold: " + threshold);
        Map<String, Edge> edges = g.getEdges();
        for (Iterator<Map.Entry<String, Edge>> it = edges.entrySet().iterator(); it.hasNext();) {
            Map.Entry<String, Edge> entry = it.next();
            Edge e = entry.getValue();
            if(similarity.equalsIgnoreCase("jaccard")){
                if(e.getJaccard()< threshold){
                    it.remove();
                }
            }else{
                if(e.getCommonBlocks()< threshold){
                    it.remove();
                }
            }
        }
        g.setEdges(edges);
        System.out.println("Prunned Graph:");
        g.printEdges();
        return g;
    }

    private double findThreshold(Graph g, String sim) {
        double th = 0;
        double weights = 0;
        Map<String, Edge> edges = g.getEdges();
        double count = (double) edges.size();

        if (sim.equalsIgnoreCase("jaccard")) {
            for (String s : edges.keySet()) {
                Edge e = edges.get(s);
                weights += e.getJaccard();
            }
        } else {
            for (String s : edges.keySet()) {
                Edge e = edges.get(s);
                weights += (double) e.getCommonBlocks();
            }
        }

        th = weights / count;
        return th;
    }
    
    
    public Graph cardinalityNodePruning(Graph g, String similarity){
        
        System.out.println("k: " + k);
        //k=2;
        List<Node> nodes = g.getNodes();
        for(Node n : nodes){
            List<Edge> edges = n.getEdges();
            if(edges.size() <= k){
                continue;
            }
            LinkedHashMap<String,Double> ed = new LinkedHashMap<>();
            if(similarity.equalsIgnoreCase("jaccard")){
                for(Edge e : edges){
                    ed.put(e.getId(), e.getJaccard());
                }
            }else{
                for(Edge e : edges){
                    ed.put(e.getId(), (double)e.getCommonBlocks());
                }
            }
            ed = sortByValues(ed);
            List<String> keys = new ArrayList<>();
            keys.addAll(ed.keySet());
            List<String> keep = new ArrayList<>();
            for(int i=0;i<k;i++){
                keep.add(keys.get(i));
            }
            
            n.removeEdges(keep);
            
        }
        System.out.println("Graph");
        System.out.println(g.toSrting());
        return g;
    }
    
        private static LinkedHashMap sortByValues(LinkedHashMap map) {
        List list = new LinkedList(map.entrySet());
        // Defined Custom Comparator here
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o2)).getValue())
                        .compareTo(((Map.Entry) (o1)).getValue());
            }
        });

        // Here I am copying the sorted list in HashMap
        // using LinkedHashMap to preserve the insertion order
        LinkedHashMap sortedHashMap = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }
        return sortedHashMap;
    }
}
