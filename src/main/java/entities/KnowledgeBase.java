package main.java.entities;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class KnowledgeBase {

	private static Long count = 0L;
	private Long id;
	private List<EntityDescription> entities = new ArrayList<>();

	public KnowledgeBase() {
		this.id = count;
		count++;
	}

	public KnowledgeBase(List<EntityDescription> entities) {
		this.id = count;
		count++;
		this.entities = entities;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<EntityDescription> getEntities() {
		return entities;
	}

	public void setEntities(List<EntityDescription> entities) {
		this.entities = entities;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		KnowledgeBase that = (KnowledgeBase) o;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		return "KnowledgeBase{" +
				"id=" + id +
				", entities=" + entities +
				'}';
	}

	public void loadDataset(String pathToFile) {
		List<String> header = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(pathToFile))) {
			String line;
			boolean firstLine = true;
			while ((line = br.readLine()) != null) {
				String[] parsedLine = line.split(";");
				if (firstLine) {
					header.addAll(Arrays.asList(parsedLine));
					firstLine = false;
				} else {
					break;
				}
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		System.out.println(header);

	}
}
