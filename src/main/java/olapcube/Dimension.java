package olapcube;

import java.util.LinkedHashMap;
import java.util.Map;

public class Dimension {
	private String dimensionName;
	private Map<String,Hierarchy> hierarchies = new LinkedHashMap<String,Hierarchy>();
	
	public Dimension(String dimensionName) {
		this.dimensionName = dimensionName;
	}
	
	public void addHierarchy(String hierarchyName, Hierarchy hierarchy) {
		hierarchies.put(hierarchyName, hierarchy);
	}
	
	public int getHierarchiesCount() {
		return hierarchies.size();
	}
	
	public Hierarchy getHierarchyByName(String hierarchyName) {
		return hierarchies.get(hierarchyName);
	}
	
	public Map<String,Hierarchy> getAllHierarchies() {
		return hierarchies;
	}
	
	public String getDimensionName() {
		return dimensionName;
	}
}
