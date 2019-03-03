package entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class BlockCollection {

	private static Long count = 0L;
	private Long id;
	private List<Block> blocks = new ArrayList<>();
	private int sizeKB1;
	private int sizeKB2;

	public BlockCollection() {
		this.id = count;
		count++;
	}

	public BlockCollection(int sizeKB1, int sizeKB2) {
		this.id = count;
		count++;
		this.sizeKB1 = sizeKB1;
		this.sizeKB2 = sizeKB2;
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

	public int getSizeKB1() {
		return sizeKB1;
	}

	public void setSizeKB1(int sizeKB1) {
		this.sizeKB1 = sizeKB1;
	}

	public int getSizeKB2() {
		return sizeKB2;
	}

	public void setSizeKB2(int sizeKB2) {
		this.sizeKB2 = sizeKB2;
	}

	public void createTokens(Set<String> tokens) {
		for (String token : tokens) {
			blocks.add(new Block(token));
		}
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
