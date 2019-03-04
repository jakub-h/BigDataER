package entities;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class KnowledgeBase {

	private static Long count = 0L;
	private Long id;
	private List<EntityDescription> entityDescriptions = new ArrayList<>();

	public KnowledgeBase() {
		this.id = count;
		count++;
	}

	public KnowledgeBase(String pathToFile) {
		this.id = count;
		count++;
		loadDataset(pathToFile);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<EntityDescription> getEntityDescriptions() {
		return entityDescriptions;
	}

	public void setEntityDescriptions(List<EntityDescription> entityDescriptions) {
		this.entityDescriptions = entityDescriptions;
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
				", entityDescriptions=" + entityDescriptions +
				'}';
	}

	private void loadDataset(String pathToFile) {
		List<String> header = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(pathToFile))) {
			String line;
			boolean firstLine = true;
			Long realEntityId = 0L;
			while ((line = br.readLine()) != null) {
				String[] parsedLine = line.split(";");
				// Load header
				if (firstLine) {
					header.addAll(Arrays.asList(parsedLine));
					firstLine = false;
				// Load rest
				} else {
					Map<String, String> attributes = new HashMap<>();
					for (int i = 0; i < header.size(); ++i) {
						if (!parsedLine[i].isEmpty()) {
							attributes.put(header.get(i), parsedLine[i]);
						}
					}
					entityDescriptions.add(new EntityDescription(realEntityId, attributes));
					realEntityId++;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
