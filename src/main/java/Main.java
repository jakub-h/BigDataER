import algorithms.EntityResolutionManager;
import entities.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

	public static void main(String[] args) {
		KnowledgeBase kb1 = new KnowledgeBase("datasets/final/kb_1.csv");
		KnowledgeBase kb2 = new KnowledgeBase("datasets/final/kb_2.csv");
		EntityResolutionManager manager = new EntityResolutionManager(kb1, kb2);
		String[] array = {"an", "the", "by", "of", "at", "on", "and"};
		List<String> fillings = new ArrayList<>(Arrays.asList(array));
		BlockCollection bc = manager.tokenBlocking(fillings, 0);
		System.out.println(bc);
	}
}
