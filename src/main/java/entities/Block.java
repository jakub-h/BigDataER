package entities;

import java.util.*;

public class Block {

	private static Long count = 0L;
	private Long id;
	private Set<EntityDescription> innerBlock1 = new HashSet<>();
	private Set<EntityDescription> innerBlock2 = new HashSet<>();
	private String token;

	public Block() {
		this.id = count;
		count++;
	}

	public Block(String token) {
		this();
		this.token = token;
	}

	public Block(Set<EntityDescription> innerBlock1, Set<EntityDescription> innerBlock2, String token) {
		this.id = count;
		count++;
		this.innerBlock1 = innerBlock1;
		this.innerBlock2 = innerBlock2;
		this.token = token;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Set<EntityDescription> getInnerBlock1() {
		return innerBlock1;
	}

	public void setInnerBlock1(Set<EntityDescription> innerBlock1) {
		this.innerBlock1 = innerBlock1;
	}

	public Set<EntityDescription> getInnerBlock2() {
		return innerBlock2;
	}

	public void setInnerBlock2(Set<EntityDescription> innerBlock2) {
		this.innerBlock2 = innerBlock2;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Block block = (Block) o;
		return Objects.equals(id, block.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		return "Block{" +
				"id=" + id +
				", token='" + token + '\'' +
				", innerBlock1=" + innerBlock1 +
				", innerBlock2=" + innerBlock2 +
				'}';
	}
}
