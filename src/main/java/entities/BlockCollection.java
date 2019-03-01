package main.java.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BlockCollection {

	private static Long count = 0L;
	private Long id;
	private List<Block> blocks = new ArrayList<>();

	public BlockCollection() {
		this.id = count;
		count++;
	}

	public BlockCollection(List<Block> blocks) {
		this.id = count;
		count++;
		this.blocks = blocks;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<Block> getBlocks() {
		return blocks;
	}

	public void setBlocks(List<Block> blocks) {
		this.blocks = blocks;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		BlockCollection that = (BlockCollection) o;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		return "BlockCollection{" +
				"id=" + id +
				", blocks=" + blocks +
				'}';
	}
}
