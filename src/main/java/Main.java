import algorithms.EntityResolutionManager;
import algorithms.Metablocking;
import entities.*;
import entities.graph.Graph;

public class Main {

	public static void main(String[] args) {
		KnowledgeBase kb1 = new KnowledgeBase("datasets/final/kb_1.csv");
		KnowledgeBase kb2 = new KnowledgeBase("datasets/final/kb_2.csv");
		EntityResolutionManager manager = new EntityResolutionManager(kb1, kb2, "datasets/final/stopwords.txt");
		BlockCollection bc = manager.tokenBlocking();
		BlockCollection bc2 = manager.attributeClusteringBlocking();

		/*
		Metablocking metablocking = new Metablocking(bc);
		Graph g = metablocking.createGraph();
		metablocking.edgeWeightingJaccard(g);
		metablocking.weightEdgePruning(g, "jaccard");
		*/
	}
}
