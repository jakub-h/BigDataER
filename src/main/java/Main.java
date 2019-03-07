import algorithms.EntityResolutionManager;
import algorithms.Metablocking;
import entities.*;
import entities.graph.Graph;

public class Main {

	public static void main(String[] args) {
		KnowledgeBase kb1 = new KnowledgeBase("datasets/final/kb_1.csv");
		KnowledgeBase kb2 = new KnowledgeBase("datasets/final/kb_2.csv");
		EntityResolutionManager manager = new EntityResolutionManager(kb1, kb2, "datasets/final/stopwords.txt");

		//BlockCollection bc = manager.tokenBlocking(1);
		//bc.niceOutput()

		BlockCollection bc = manager.attributeClusteringBlocking(true, 1);

		Metablocking metablocking = new Metablocking(bc);
		metablocking.createGraph();
		metablocking.edgeWeightingJaccard();
		metablocking.weightEdgePruning( "jaccard");

		/*
		Metablocking metablocking2 = new Metablocking(bc);
		metablocking2.createGraph();
		metablocking2.cardinalityNodePruning("common_blocks");
		*/
	}
}
