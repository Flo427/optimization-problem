package main;

import java.util.ArrayList;
import java.util.List;

public class RuleBasedNeighborhood extends Neighborhood {

	@Override
	public List<Instance> getNeighbors(Instance instance) {
		List<Instance> neighbors = new ArrayList<Instance>();
		neighbors.add(instance);
		for (int i = 0; i < instance.n; ++i) {
			for (int j = i + 1; j < instance.n; ++j) {
				Instance neighbor = new Instance(instance);
				neighbor.swapRectangles(i, j);
				neighbor.generateCoordinatesByPermutation();
				neighbors.add(neighbor);
			}
			for (int j = 0; j < instance.n; ++j) {
				if (Math.abs(i - j) > 1) {
					Instance neighbor = new Instance(instance);
					neighbor.newRectanglePosition(i, j);
					neighbor.generateCoordinatesByPermutation();
					neighbors.add(neighbor);
				}
			}
		}
		System.out.println(neighbors.size());
		return neighbors;
	}

	@Override
	public Instance getBestNeighbor(List<Instance> neighbors) {
		Instance bestNeighbor = null;
		int bestValue = Integer.MAX_VALUE;
		for (Instance neighbor: neighbors) {
			if (neighbor.getObjectiveValue() < bestValue) {
				bestNeighbor = neighbor;
				bestValue = neighbor.getObjectiveValue();
			}
		}		
		return bestNeighbor;
	}

}
