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


/**
 * Manager of Entity Resolution task.
 *
 * Offers public methods 'tokenBlocking(int verbose)' and 'attributeClusteringBlocking(int verbose)'.
 * Both return instance of {@link BlockCollection} class.
 *
 * Parameters verbose denote level of verbosity - debugging outputs. Only two levels (0/1) are currently supported.
 *
 * Stopwords attribute holds list of english stopwords used during token parsing.
 */
public class EntityResolutionManager {

    private KnowledgeBase kb1;
    private KnowledgeBase kb2;
    private List<String> stopwords = new ArrayList<>();

    public EntityResolutionManager(KnowledgeBase kb1, KnowledgeBase kb2, String stopwordsFile) {
        this.kb1 = kb1;
        this.kb2 = kb2;
		loadStopwords(stopwordsFile);
    }

    /** Getters and setters **/

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

	/**
	 * Loads stopwords from a file - one word per line.
	 *
	 * @param pathToFile
	 */
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

	/**
	 * Basic token blocking method.
	 *
	 * @return {@link BlockCollection} resulting block collection
	 */
	public BlockCollection tokenBlocking(int verbose) {
    	// Temporal Map<token, block> (will be filtered later)
        Map<String, Block> blocks = new HashMap<>();
		// Create blocks from first KB and fill it's inner blocks
        for (EntityDescription ed : kb1.getEntityDescriptions()) {
        	Set<String> edTokens = parseTokens(ed.getAttrValPairs().values());
	        createAndFillBlocks(blocks, ed, edTokens,1);
        }
        // Fill second inner blocks (by entity descriptions from KB2)
        for (EntityDescription ed : kb2.getEntityDescriptions()) {
        	Set<String> edTokens = parseTokens(ed.getAttrValPairs().values());
        	createAndFillBlocks(blocks, ed, edTokens,2);
        }

        // Filtering blocks with empty inner block 2 (inner block 1 can not be empty - see section above)
	    return filterEmptyInnerBlocks(blocks, verbose);
    }

	/**
	 * Attribute Clustering Blocking method.
	 *
	 * In contrast with basic token blocking, every token has prefix (eg. "0:") denoting index of the respective
	 * attribute cluster.
	 *
	 * @param useTokensForClustering false - set of original values (for each attribute) is used for computing
	 *                                  similarities between attributes
	 *                               true - set of tokens (parsed from original values) is used instead
	 * @param verbose level of verbosity used in inner methods.
	 * @return {@link BlockCollection} resulting block collection
	 */
	public BlockCollection attributeClusteringBlocking(boolean useTokensForClustering, int verbose) {
		// Get index of cluster for each attribute
	    Map<String, Integer> clusterIndexes = getAttributeClusterIndex(
	    		getConnectedComponents(useTokensForClustering, verbose),
			    verbose);
	    // Temporal Map<token, block> (will be filtered later)
	    Map<String, Block> blocks = new HashMap<>();
	    // Create blocks from first KB and fill it's inner blocks
	    for (EntityDescription ed : kb1.getEntityDescriptions()) {
        	Set<String> edTokens = parseTokensWithClusters(ed.getAttrValPairs(), clusterIndexes);
		    createAndFillBlocks(blocks, ed, edTokens, 1);
	    }
	    // Fill second inner blocks (by entity descriptions from KB2)
	    for (EntityDescription ed : kb2.getEntityDescriptions()) {
		    Set<String> edTokens = parseTokensWithClusters(ed.getAttrValPairs(), clusterIndexes);
		    createAndFillBlocks(blocks, ed, edTokens, 2);
	    }
		// Filtering blocks with empty inner block 2 (inner block 1 can not be empty - see section above)
		return filterEmptyInnerBlocks(blocks, verbose);
	}

	/**
	 * Clusters attributes based on the similarity of their set of values.
	 *
	 * Jaccard similarity function is used.
	 *
	 * @param useTokens see {@link #attributeClusteringBlocking(boolean, int)}
	 * @param verbose level of verbosity
	 * @return names of attributes in clusters - list of sets of attribute names
	 */
	private List<Set<String>> getConnectedComponents(boolean useTokens, int verbose) {
    	List<Set<String>> clusters = new ArrayList<>();
    	// Associate each attribute with corresponding set of values
    	Map<String, Set<String>> kb1AttrValPairs = reorganizeAttributeValuePairs(kb1);
    	Map<String, Set<String>> kb2AttrValPairs = reorganizeAttributeValuePairs(kb2);
    	// Find links between attributes
    	Map<String, String> links = new HashMap<>();
	    findLinks(kb1AttrValPairs, kb2AttrValPairs, links, useTokens, verbose);
	    findLinks(kb2AttrValPairs, kb1AttrValPairs, links, useTokens, verbose);
	    List<String> keys = new ArrayList<>(links.keySet());
	    // Create glue cluster
	    Set<String> glue = new HashSet<>();
	    for (String key : kb1AttrValPairs.keySet()) {
	    	if (!keys.contains(key)) {
	    		glue.add(key);
		    }
	    }
	    for (String key : kb2AttrValPairs.keySet()) {
	    	if (!keys.contains(key)) {
	    		glue.add(key);
		    }
	    }
	    if (verbose > 0) {
		    System.out.println("\n==== Creating Attribute Clusters ====");
		    System.out.println("GLUE: " + glue);
	    }
	    // Find connected components
	    int numOfComponents = 0;
		if (verbose > 0) {
			System.out.println("BEFORE");
			System.out.println("-links: " + links);
			System.out.println("-keys: " + keys);
			System.out.println("-clusters: " + clusters);
		}
	    while (!links.isEmpty() || !keys.isEmpty()) {
	    	String from = keys.remove(0);
	    	clusters.add(new HashSet<>());
	    	clusters.get(numOfComponents).add(from);
			String to = links.remove(from);
			keys.remove(to);
			while (to != null) {
				clusters.get(numOfComponents).add(to);
				to = links.remove(to);
				keys.remove(to);
			}
			if (verbose > 0) {
				System.out.println("AFTER STEP: " + numOfComponents);
				System.out.println("-links: " + links);
				System.out.println("-keys: " + keys);
				System.out.println("-clusters: " + clusters);
			}
		    numOfComponents++;
	    }
	    // Add glue cluster to the result
	    if (!glue.isEmpty()) {
		    clusters.add(glue);
	    }
	    if (verbose > 0) {
		    System.out.println("FINAL CLUSTERS: " + clusters);
	    }
	    return clusters;
    }

	/**
	 * Helper method for changing data structure of attribute-values pairs.
	 *
	 * @param kb Knowledge base to be transformed
	 * @return map - key is an attribute name
	 *             - value is a set of all values belonging to the key attribute
	 */
	private Map<String, Set<String>> reorganizeAttributeValuePairs(KnowledgeBase kb) {
		// Initialize map with all attributes from knowledge base
		Map<String, Set<String>> attrValPairs = new HashMap<>();
		Set<String> attributes = kb.getEntityDescriptions().get(0).getAttrValPairs().keySet();
		for (String attribute : attributes) {
			attrValPairs.put(attribute, new HashSet<>());
		}
		// Fill the map
		for (EntityDescription ed : kb.getEntityDescriptions()) {
			for (String attribute : attributes) {
				if (ed.getAttrValPairs().containsKey(attribute)) {
					attrValPairs.get(attribute).add(ed.getAttrValPairs().get(attribute));
				}
			}
		}
		return attrValPairs;
	}

	/**
	 * Fills given map of links between most similar attribute names.
	 *
	 * @param first attribute-value pairs from {@link #reorganizeAttributeValuePairs(KnowledgeBase)}
	 * @param second attribute-value pairs from {@link #reorganizeAttributeValuePairs(KnowledgeBase)}
	 * @param links set of pairs of similar attributes (Map<String, String>)
	 * @param useTokens see {@link #attributeClusteringBlocking(boolean, int)}
	 */
	private void findLinks(Map<String, Set<String>> first, Map<String, Set<String>> second, Map<String, String> links,
	                       boolean useTokens, int verbose) {
		if (verbose > 0) {
			System.out.println("\n==== Jaccard Similarity of attributes ====");
		}
		for (String attribute : first.keySet()) {
			Pair<String, Double> mostSimilarAttr = findMostSimilarAttribute(
					first.get(attribute), second, useTokens, attribute, verbose);
			if (mostSimilarAttr.getValue() > 0) {
				links.put(attribute, mostSimilarAttr.getKey());
			}
		}
	}

	/**
	 * Finds most similar attribute for given set of values and returns it's name and Jaccard similarity.
	 *
	 * @param values values of the attribute from one knowledge base
	 * @param otherAttrVals attribute-value pairs (in form of {@link #reorganizeAttributeValuePairs(KnowledgeBase)})
	 *                         from second knowledge base
	 * @param useTokens see {@link #attributeClusteringBlocking(boolean, int)}
	 * @return pair of the most similar attribute's name and their Jaccard similarity
	 */
	private Pair<String, Double> findMostSimilarAttribute(Set<String> values, Map<String, Set<String>> otherAttrVals,
	                                                      boolean useTokens, String attributeName, int verbose) {
		// Calculate Jaccard similarity for every attribute
		Map<String, Double> tmp = new HashMap<>();
		for (Map.Entry<String, Set<String>> other : otherAttrVals.entrySet()) {
			if (useTokens) {
				tmp.put(other.getKey(),
						jaccard(parseTokens(values), parseTokens(other.getValue())));
			} else {
				tmp.put(other.getKey(), jaccard(values, other.getValue()));
			}
		}
		// Sort the map by the Jaccard similarity
		final Map<String, Double> sortedJaccards = tmp.entrySet()
				.stream()
				.sorted((Map.Entry.<String, Double>comparingByValue().reversed()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
		List<String> attributes = new ArrayList<>(sortedJaccards.keySet());
		List<Double> jaccards = new ArrayList<>(sortedJaccards.values());
		if (verbose > 0) {
			System.out.println(attributeName + ": " + sortedJaccards);
		}
		// Return the most similar one
		return new Pair<>(attributes.get(0), jaccards.get(0));
	}

	/**
	 * Jaccard similarity function on two sets of Strings.
	 *
	 * @param first
	 * @param second
	 * @return
	 */
	private Double jaccard(final Set<String> first, final Set<String> second) {
    	final Set<String> intersection = new HashSet<>(first);
    	intersection.retainAll(second);
    	final Set<String> union = new HashSet<>(first);
    	union.addAll(second);
		return (double) intersection.size() / union.size();
    }

	/**
	 * Reorganizes clusters of attributes from list of clusters into map of attributes and corresponding cluster indexes.
	 *
	 * @param clusters list of attribute clusters
	 * @param verbose level of verbosity
	 * @return map of attributes and their corresponding cluster index
	 */
	private Map<String, Integer> getAttributeClusterIndex(List<Set<String>> clusters, int verbose) {
    	Map<String, Integer> result = new HashMap<>();
    	for (int i = 0; i < clusters.size(); ++i) {
    		for (String attribute : clusters.get(i)) {
    			result.put(attribute, i);
		    }
	    }
    	if (verbose > 0) {
		    System.out.println("\n==== Attribute clusters index ====");
		    System.out.println(result);
	    }
    	return result;
    }

	/**
	 * Puts given entity description into given map of blocks (mapped by their token) and creates a new block if needed.
	 *
	 * @param blocks set of token-block pairs
	 * @param ed entity description
	 * @param edTokens set of tokens of given entity description
	 */
	private void createAndFillBlocks(Map<String, Block> blocks, EntityDescription ed, Set<String> edTokens, int innerBlock) {
		for (String token : edTokens) {
			if (!blocks.containsKey(token)) {
				blocks.put(token, new Block(token));
			}
			if (innerBlock == 1) {
				blocks.get(token).getInnerBlock1().add(ed);
			} else if (innerBlock == 2) {
				blocks.get(token).getInnerBlock2().add(ed);
			}
		}
	}

	/**
	 * Filters given block collection.
	 *
	 * Removes every block with an empty inner block
	 * @param blocks {@link BlockCollection} to be filtered
	 * @return filtered {@link BlockCollection}
	 */
	private BlockCollection filterEmptyInnerBlocks(Map<String, Block> blocks, int verbose) {
		BlockCollection result = new BlockCollection(kb1.getEntityDescriptions().size(), kb2.getEntityDescriptions().size());
		for (Block block : blocks.values()) {
			if (!block.getInnerBlock1().isEmpty() && !block.getInnerBlock2().isEmpty()) {
				result.getBlocks().add(block);
			}
		}
		if (verbose > 0) {
			System.out.println("\n==== Filtering Block Collection ====");
			System.out.println("before: " + blocks.values().size());
			System.out.println("after: " + result.getBlocks().size());
		}
		return result;
	}

	/**
	 * Parses given set of strings into set of tokens.
	 *
	 * @param values set of strings to be parsed
	 * @return set of tokens
	 */
	private Set<String> parseTokens(Collection<String> values) {
		Set<String> result = new HashSet<>();
		for (String value : values) {
			String[] tokens = value.split(" ");
			for (String token : tokens) {
				if (token.length() > 2 && !stopwords.contains(token.toLowerCase())) {
					result.add(token);
				}
			}
		}
		return result;
	}

	/**
	 * Parses given set of strings into set of tokens with respect to given attribute clusters.
	 *
	 * @param attrValPairs map of values to be parsed (mapped by their attribute names)
	 * @param clusterIndexes map of attributes names with their corresponding cluster indexes
	 * @return set of tokens - every token is composed from an attribute cluster's index and the token itself
	 *                          for example: "2:Leone" - attribute 'director' belongs to cluster with index 2
	 */
	private Set<String> parseTokensWithClusters(Map<String, String> attrValPairs, Map<String, Integer> clusterIndexes) {
		Set<String> result = new HashSet<>();
		for (Map.Entry<String, String> pair : attrValPairs.entrySet()) {
			String[] tokens = pair.getValue().split(" ");
			for (String token : tokens) {
				if (token.length() > 2 && !stopwords.contains(token.toLowerCase())) {
					result.add(clusterIndexes.get(pair.getKey()) + ":" + token);
				}
			}
		}
		return result;
	}
}
