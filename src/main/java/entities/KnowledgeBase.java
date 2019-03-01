package main.java.entities;

import java.util.ArrayList;
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
}
