package main.java;


import main.java.entities.*;

public class Main {

	public static void main(String[] args) {
		KnowledgeBase kb = new KnowledgeBase();
		kb.loadDataset("datasets/final/kb_1.csv");
	}
}
