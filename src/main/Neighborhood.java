package main;

import java.util.List;

public abstract class Neighborhood {
	
	public abstract List<Instance> getNeighbors(Instance instance);
	
	public abstract Instance getBestNeighbor(List<Instance> neighbors);

}
