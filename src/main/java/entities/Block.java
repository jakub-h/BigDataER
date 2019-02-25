package entities;

import java.util.ArrayList;
import java.util.List;

public class Block {

	private Long id;
	private List<EntityDescription> innerBlock1 = new ArrayList<>();
	private List<EntityDescription> innerBlock2 = new ArrayList<>();
	private String token;
}
