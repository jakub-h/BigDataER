package algorithms;

import entities.Block;
import entities.BlockCollection;
import entities.EntityDescription;
import entities.KnowledgeBase;
import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.*;
import java.util.stream.Collectors;

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
    	// Temporal map<token, block> (will be filtered later)
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
        createAttributeClusters();
        return null;
    }

    private List<List<String>> createAttributeClusters() {
    	List<List<String>> clusters = new ArrayList<>();
    	// Associate each attribute with corresponding set of values
    	Map<String, Set<String>> kb1AttrValPairs = reorganizeAttributeValuePairs(kb1);
    	Map<String, Set<String>> kb2AttrValPairs = reorganizeAttributeValuePairs(kb2);
    	// Find links between attributes
    	Map<String, String> links = new HashMap<>();
	    findLinks(kb1AttrValPairs, kb2AttrValPairs, links);
	    findLinks(kb2AttrValPairs, kb1AttrValPairs, links);
	    // Find connected components
	    // TODO - continue here - find connected components (symetric/transitive links) + glue forever alones
	    return clusters;
    }

	private Map<String, Set<String>> reorganizeAttributeValuePairs(KnowledgeBase kb) {
		Map<String, Set<String>> attrValPairs = new HashMap<>();
		Set<String> attributes = kb.getEntityDescriptions().get(0).getAttrValPairs().keySet();
		for (String attribute : attributes) {
			attrValPairs.put(attribute, new HashSet<>());
		}
		for (EntityDescription ed : kb.getEntityDescriptions()) {
			for (String attribute : attributes) {
				if (ed.getAttrValPairs().containsKey(attribute)) {
					attrValPairs.get(attribute).add(ed.getAttrValPairs().get(attribute));
				}
			}
		}
		return attrValPairs;
	}

	private void findLinks(Map<String, Set<String>> first, Map<String, Set<String>> second, Map<String, String> links) {
		for (String attribute : first.keySet()) {
			Pair<String, Double> mostSimilarAttr = findMostSimilarAttribute(first.get(attribute), second);
			if (mostSimilarAttr.getValue() > 0) {
				links.put(attribute, mostSimilarAttr.getKey());
			}
		}
	}

	private Pair<String, Double> findMostSimilarAttribute(Set<String> values, Map<String, Set<String>> otherAttrVals) {
		Map<String, Double> tmp = new HashMap<>();
		for (Map.Entry<String, Set<String>> other : otherAttrVals.entrySet()) {
			tmp.put(other.getKey(), jaccard(values, other.getValue()));
		}
		final Map<String, Double> sortedJaccards = tmp.entrySet()
				.stream()
				.sorted((Map.Entry.<String, Double>comparingByValue().reversed()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
		List<String> attributes = new ArrayList<>(sortedJaccards.keySet());
		List<Double> jaccards = new ArrayList<>(sortedJaccards.values());
		return new Pair<>(attributes.get(0), jaccards.get(0));
	}

	private Double jaccard(final Set<String> first, final Set<String> second) {
    	final Set<String> intersection = new HashSet<>(first);
    	intersection.retainAll(second);
    	final Set<String> union = new HashSet<>(first);
    	union.addAll(second);
		return (double) intersection.size() / union.size();
    }
}
