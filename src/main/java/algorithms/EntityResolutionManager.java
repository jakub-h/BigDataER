package algorithms;

import entities.Block;
import entities.BlockCollection;
import entities.EntityDescription;
import entities.KnowledgeBase;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EntityResolutionManager {

    private KnowledgeBase kb1;
    private KnowledgeBase kb2;
    private List<String> stopwords = new ArrayList<>();

    public EntityResolutionManager() {}

    public EntityResolutionManager(KnowledgeBase kb1, KnowledgeBase kb2, String stopwordsFile) {
        this.kb1 = kb1;
        this.kb2 = kb2;
		loadStopwords(stopwordsFile);
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

    private void loadStopwords(String pathToFile) {
        try (BufferedReader br = new BufferedReader(new FileReader(pathToFile))) {
        	String line;
        	while ((line = br.readLine()) != null) {
        		stopwords.add(line);
	        }
        } catch (IOException e) {
	        e.printStackTrace();
        }
    }

    public BlockCollection tokenBlocking() {
    	// Temporal block collection (will be filtered later)
        Map<String, Block> blocks = new HashMap<>();
		// Fill first inner blocks (by entity descriptions from KB1)
        for (EntityDescription ed : kb1.getEntityDescriptions()) {
        	Set<String> edTokens = ed.getTokens(stopwords);
        	for (String token : edTokens) {
        		if (!blocks.containsKey(token)) {
        			blocks.put(token, new Block(token));
		        }
		        blocks.get(token).getInnerBlock1().add(ed);
	        }
        }
        // Fill second inner blocks (by entity descriptions from KB2)
        for (EntityDescription ed : kb2.getEntityDescriptions()) {
        	Set<String> edTokens = ed.getTokens(stopwords);
        	for (String token : edTokens) {
        		// If the block was not created yet (doesn't contain any entity from KB1), it will be pruned anyway.
        		if (blocks.containsKey(token)) {
			        blocks.get(token).getInnerBlock2().add(ed);
		        }
	        }
        }

        // Filtering blocks with empty inner block 2 (inner block 1 can not be empty - see section above)
        BlockCollection result = new BlockCollection(kb1.getEntityDescriptions().size(), kb2.getEntityDescriptions().size());
        for (Block block : blocks.values()) {
            if (!block.getInnerBlock2().isEmpty()) {
                result.getBlocks().add(block);
            }
        }
        return result;
    }

    public BlockCollection attributeClusteringBlocking() {
        throw new RuntimeException("Method not implemented yet!");
    }

}
