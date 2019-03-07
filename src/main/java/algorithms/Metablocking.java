package algorithms;

import entities.*;
import entities.graph.Edge;
import entities.graph.Graph;
import entities.graph.Node;

import java.math.BigDecimal;
import java.util.*;

/**
 *
 * @author mariastr
 */
public class Metablocking {

    private BlockCollection bc;
    private Map<Long, List<Block>> entityIndex;
    private Map<Long, EntityDescription> entityEntry;
    private Graph gr;
    private int k = 0;

    public Metablocking(BlockCollection bc) {
        this.bc = bc;
        entityIndex = new HashMap<>();
        entityEntry = new HashMap<>();
        createEntityIndex();
    }

    private void createEntityIndex() {
        Set<Block> blocks = bc.getBlocks();
        for (Block b : blocks) {
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
    }

    public void createGraph() {
        gr = new Graph();
        Set<Block> blocks = bc.getBlocks();
        double local = 0;
        int KB1 = bc.getSizeKB1();
        int KB2 = bc.getSizeKB2();
        int size = KB1 + KB2;
        
        for (Block b : blocks) {
            Set<EntityDescription> inner1 = b.getInnerBlock1();
            Set<EntityDescription> inner2 = b.getInnerBlock2();
            //int size = ent.size();
            local = local + (double) (inner1.size() + inner2.size()) / (double) size;
            for (EntityDescription in1 : inner1) {
                gr.addNode(in1.getId(),in1.getAttrValPairs().get("title"));
                for (EntityDescription in2 : inner2) {
                    gr.addNode(in2.getId(),in1.getAttrValPairs().get("name"));
                    gr.addEdge(in1.getId(), in2.getId());
                }
            }
        }
        k = (int) Math.floor(local);
    }

    public Graph edgeWeightingJaccard() {
        Map<String, Edge> edges = gr.getEdges();
        for (String s : edges.keySet()) {
            String[] nodes = s.split("_");
            Long id1 = Long.parseLong(nodes[0]);
            Long id2 = Long.parseLong(nodes[1]);
            List<Block> list1 = new ArrayList<>(entityIndex.get(id1));
            List<Block> list2 = new ArrayList<>(entityIndex.get(id2));
            int size1 = list1.size();
            int size2 = list2.size();
            list1.retainAll(list2);
            double weight = (double) list1.size() / (size1 + size2);
            Edge e = edges.get(s);
            weight = round(weight, 3);
            e.setJaccard(weight);
        }
        return gr;
    }

    private double round(double number, int decimalPlace) {
        BigDecimal bd = new BigDecimal(number);
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.doubleValue();
    }

    public void weightEdgePruning(String similarity) {
        double threshold = findThreshold(similarity);
        System.out.println("\n==== Weight Edge Pruning ====");
        System.out.println("Threshold: " + threshold + " (" + similarity + ")\n");
        Map<String, Edge> edges = gr.getEdges();
        for (Iterator<Map.Entry<String, Edge>> it = edges.entrySet().iterator(); it.hasNext();) {
            Map.Entry<String, Edge> entry = it.next();
            Edge e = entry.getValue();
            if (similarity.equalsIgnoreCase("jaccard")) {
                if (e.getJaccard() < threshold) {
                    it.remove();
                }
            } else {
                if (e.getCommonBlocks() < threshold) {
                    it.remove();
                }
            }
        }
        gr.setEdges(edges);
        printWeightEdges();
    }

    private double findThreshold(String sim) {
        double weights = 0;
        Map<String, Edge> edges = gr.getEdges();
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
        return weights / count;
    }

    public void cardinalityNodePruning(String similarity) {
        System.out.println("\n==== Cardinality Node Pruning (" + similarity + ") ====");
        System.out.println("k: " + k + "\n");
        List<Node> nodes = gr.getNodes();
        for (Node n : nodes) {
            List<Edge> edges = n.getEdges();
            if (edges.size() <= k) {
                continue;
            }
            LinkedHashMap<String, Double> ed = new LinkedHashMap<>();
            if (similarity.equalsIgnoreCase("jaccard")) {
                for (Edge e : edges) {
                    ed.put(e.getId(), e.getJaccard());
                }
            } else {
                for (Edge e : edges) {
                    ed.put(e.getId(), (double) e.getCommonBlocks());
                }
            }
            ed = sortByValues(ed);
            List<String> keys = new ArrayList<>(ed.keySet());
            List<String> keep = new ArrayList<>();
            for (int i = 0; i < k; i++) {
                keep.add(keys.get(i));
            }
            n.keepEdges(keep);
        }
        printCardinalityEdge();
    }

    private void printCardinalityEdge(){
        Set<String> prunnedEdge = new HashSet<>(gr.getPrunnedEdges());
        int all = prunnedEdge.size();
        int count = 0;
        for(String s : prunnedEdge){
            String [] el = s.split("_");
            EntityDescription e1 = entityEntry.get(Long.parseLong(el[0]));
            EntityDescription e2 = entityEntry.get(Long.parseLong(el[1]));
            Map<String,String> e1Att = e1.getAttrValPairs();
            Map<String,String> e2Att = e2.getAttrValPairs();
            String e1Title,e2Title;
            if(e1Att.containsKey("title")){
                e1Title = e1Att.get("title");
            }else{
                e1Title = e1Att.get("name");
            }
            if(e2Att.containsKey("title")){
                e2Title = e2Att.get("title");
            }else{
                e2Title = e2Att.get("name");
            }
            if(e1Title.equalsIgnoreCase(e2Title)){
                count++;
                System.out.println(e1Title + " -> " + e2Title);
            }
        }
        System.out.println("\nTrue Pairs: " + count + " of " + bc.getSizeKB1());
        System.out.println("All comparisons: " + all);
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
        for (Object o : list) {
            Map.Entry entry = (Map.Entry) o;
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }
        return sortedHashMap;
    }

    private void printWeightEdges() {
        Map<String,Edge> edges = gr.getEdges();
        int count = 0;
        List<String> doub = new ArrayList<>();
        int all = edges.size();
        for(String s : edges.keySet()){
            String [] el = s.split("_");
            EntityDescription e1 = entityEntry.get(Long.parseLong(el[0]));
            EntityDescription e2 = entityEntry.get(Long.parseLong(el[1]));
            Map<String,String> e1Att = e1.getAttrValPairs();
            Map<String,String> e2Att = e2.getAttrValPairs();
            String e1Title,e2Title;
            if(e1Att.containsKey("title")){
                e1Title = e1Att.get("title");
            }else{
                e1Title = e1Att.get("name");
            }
            if(e2Att.containsKey("title")){
                e2Title = e2Att.get("title");
            }else{
                e2Title = e2Att.get("name");
            }
            if(e1Title.equalsIgnoreCase(e2Title)){
                if(doub.contains(e1Title)){
                    System.out.println("-------->" + e1Title);
                }
                doub.add(e1Title);
                count++;
                System.out.println(e1Title + " -> " + e2Title);
            }
        }
        System.out.println("\nTrue Pairs: " + count + " of " + bc.getSizeKB1());
        System.out.println("All comparisons: " + all);
    }
}
