package algorithms;

import entities.BlockCollection;
import entities.EntityDescription;
import entities.KnowledgeBase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EntityResolutionManager {

	private KnowledgeBase kb1;
	private KnowledgeBase kb2;

	public EntityResolutionManager() {}

	public EntityResolutionManager(KnowledgeBase kb1, KnowledgeBase kb2) {
		this.kb1 = kb1;
		this.kb2 = kb2;
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

	public BlockCollection tokenBlocking(List<String> fillings, int verbose) {
		BlockCollection bc = new BlockCollection(kb1.getEntityDescriptions().size(), kb2.getEntityDescriptions().size());
		// Create set of tokens
		Set<String> tokens1 = generateTokens(kb1, fillings);
		Set<String> tokens2 = generateTokens(kb2, fillings);
		Set<String> tokens = new HashSet<>();
		tokens.addAll(tokens1);
		tokens.addAll(tokens2);

		if (verbose > 0) {
			System.out.println("kb1 - tokens: " + tokens1.size());
			System.out.println("kb2 - tokens: " + tokens2.size());
			System.out.println("merged: " + tokens.size());
			int only1 = 0;
			int only2 = 0;
			for (String token : tokens) {
				if (!tokens1.contains(token)) {
					only2++;
				}
				if (!tokens2.contains(token)) {
					only1++;
				}
			}
			System.out.println("only 1: " + only1);
			System.out.println("only 2: " + only2);
		}

		// TODO continue here - create blocks and inner blocks (based on the set of tokens) and fill them with entityDescriptions
		return bc;
	}

	private Set<String> generateTokens(KnowledgeBase kb, List<String> fillings) {
		Set<String> tokens = new HashSet<>();
		for (EntityDescription ed : kb.getEntityDescriptions()) {
			for (String value : ed.getAttrValPairs().values()) {
				String[] splittedValue = value.split(" ");
				for (String token : splittedValue) {
					if (token.length() > 2 && !fillings.contains(token.toLowerCase())) {
						tokens.add(token);
					}
				}
			}
		}
		return tokens;
	}

	public BlockCollection attributeClusteringBlocking() {
		throw new RuntimeException("Method not implemented yet!");
	}


}