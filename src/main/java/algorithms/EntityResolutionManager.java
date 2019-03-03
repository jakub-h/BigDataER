package algorithms;

import entities.Block;
import entities.BlockCollection;
import entities.EntityDescription;
import entities.KnowledgeBase;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EntityResolutionManager {

    private KnowledgeBase kb1;
    private KnowledgeBase kb2;
    private List<String> stopwords;

    public EntityResolutionManager() {
    }

    public EntityResolutionManager(KnowledgeBase kb1, KnowledgeBase kb2) {
        this.kb1 = kb1;
        this.kb2 = kb2;
        stopwords = new ArrayList<>();

    }

    public KnowledgeBase getKb1() {
        return kb1;
    }

    public void setKb1(KnowledgeBase kb1) {
        this.kb1 = kb1;
    }

    public KnowledgeBase getKb2() {
        return kb2;
    }

    public void setKb2(KnowledgeBase kb2) {
        this.kb2 = kb2;
    }

    public List<String> readStopwords(String file) {
        int i = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(file));
            String line;
            

            while ((line = in.readLine()) != null) {
                i++;
                line = line.trim();
                if (line.equalsIgnoreCase("")) {
                    continue;
                }
                stopwords.add(line);
            }
            in.close();
//            System.out.println("#lines: " + i);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(EntityResolutionManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(EntityResolutionManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (java.lang.OutOfMemoryError ex) {
            System.out.println("lines: " + i + " ");
        }
        return stopwords;
    }

    public BlockCollection tokenBlocking(List<String> fillings, int verbose) {
        BlockCollection tmpBC = new BlockCollection();
        // Create set of tokens
//        Set<String> tokens1 = generateTokens(kb1, fillings);
//        Set<String> tokens2 = generateTokens(kb2, fillings);
//        Set<String> tokens = new HashSet<>();
//        tokens.addAll(tokens1);
//        tokens.addAll(tokens2);

//        if (verbose > 0) {
//            System.out.println("kb1 - tokens: " + tokens1.size());
//            System.out.println("kb2 - tokens: " + tokens2.size());
//            System.out.println("merged: " + tokens.size());
//            int only1 = 0;
//            int only2 = 0;
//            for (String token : tokens) {
//                if (!tokens1.contains(token)) {
//                    only2++;
//                }
//                if (!tokens2.contains(token)) {
//                    only1++;
//                }
//            }
//            System.out.println("only 1: " + only1);
//            System.out.println("only 2: " + only2);
//        }

        //tmpBC.createTokens(tokens);
        Map<String, Block> blockMap = new HashMap<>();
        for (EntityDescription ed : kb1.getEntityDescriptions()) {
            Set<String> edTokens = ed.getTokens(fillings);
            //for (Block block : tmpBC.getBlocks()) {
                for (String token : edTokens) {
                    Block b;
                    if(blockMap.containsKey(token)){
                        b = blockMap.get(token);
                    }else{
                        b = new Block(token);
                        blockMap.put(token, b);
                        tmpBC.addBlock(b);
                    }
                    
                    b.getInnerBlock1().add(ed);
                }
            //}
        }

        for (EntityDescription ed : kb2.getEntityDescriptions()) {
            Set<String> edTokens = ed.getTokens(fillings);
            //for (Block block : tmpBC.getBlocks()) {
                for (String token : edTokens) {
                    Block b;
                    if(blockMap.containsKey(token)){
                        b = blockMap.get(token);
                    }else{
                        b = new Block(token);
                        blockMap.put(token, b);
                        tmpBC.addBlock(b);
                    }
                    
                    b.getInnerBlock2().add(ed);
                }
            //}
        }

        BlockCollection result = new BlockCollection(kb1.getEntityDescriptions().size(), kb2.getEntityDescriptions().size());
        for (Block block : tmpBC.getBlocks()) {
            if (!block.getInnerBlock1().isEmpty() && !block.getInnerBlock2().isEmpty()) {
                result.getBlocks().add(block);
            }
        }
        System.out.println(tmpBC.getBlocks().size());
        System.out.println(result.getBlocks().size());

        return result;
    }

    private Set<String> generateTokens(KnowledgeBase kb, List<String> fillings) {
        Set<String> tokens = new HashSet<>();
        for (EntityDescription ed : kb.getEntityDescriptions()) {
            Set<String> edTokens = ed.getTokens(fillings);
            tokens.addAll(edTokens);
        }
        return tokens;
    }

    public BlockCollection attributeClusteringBlocking() {
        throw new RuntimeException("Method not implemented yet!");
    }

}
