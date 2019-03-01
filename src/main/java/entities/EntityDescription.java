package main.java.entities;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EntityDescription {

	private static Long count = 0L;
	private Long id;
	private Long realWorldEntityId;
	private Map<String, String> attrValPairs = new HashMap<>();

	public EntityDescription() {
		this.id = count;
		count++;
	}

	public EntityDescription(Long realWorldEntityId, Map<String, String> attrValPairs) {
		this.id = count;
		count++;
		this.realWorldEntityId = realWorldEntityId;
		this.attrValPairs = attrValPairs;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getRealWorldEntityId() {
		return realWorldEntityId;
	}

	public void setRealWorldEntityId(Long realWorldEntityId) {
		this.realWorldEntityId = realWorldEntityId;
	}

	public Map<String, String> getAttrValPairs() {
		return attrValPairs;
	}

	public void setAttrValPairs(Map<String, String> attrValPairs) {
		this.attrValPairs = attrValPairs;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		EntityDescription that = (EntityDescription) o;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		return "EntityDescription{" +
				"id=" + id +
				", realWorldEntityId=" + realWorldEntityId +
				", attrValPairs=" + attrValPairs +
				'}';
	}
}
