package olapcube;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class OLAPCube {
	private String cubeName;
	private Map<String,Dimension> dimensions = new LinkedHashMap<String,Dimension>();
	private Map<String,Measure> measures = new LinkedHashMap<String,Measure>();
	
	public OLAPCube(String cubeName) {
		this.cubeName = cubeName;
	}
	
	public String getCubeName() {
		return cubeName;
	}
	
	public void addDimension(String dimensionName, Dimension dimension) {
		dimensions.put(dimensionName, dimension);
	}
	
	public int getDimensionsCount() {
		return dimensions.size();
	}
	
	public Dimension getDimensionByName(String dimensionName) {
		return dimensions.get(dimensionName);
	}
	
	public Map<String,Dimension> getAllDimensions() {
		return dimensions;
	}
	
	public void addMeasure(String measureName, Measure measure) {
		measures.put(measureName, measure);
	}
	
	public int getMeasuresCount() {
		return measures.size();
	}
	
	public Measure getMeasureByName(String measureName) {
		return measures.get(measureName);
	}
	
	public Map<String,Measure> getAllMeasures() {
		return measures;
	}
	
	public void printCube() {
		System.out.println("=====DIMENSIONS=====");
		for(Map.Entry<String, Dimension> dimension : dimensions.entrySet()) {
			System.out.println("Dimension "+dimension.getValue().getDimensionName());
			for(Map.Entry<String, Hierarchy> hierarchy : dimension.getValue().getAllHierarchies().entrySet()) {
				System.out.println("  Hierarchy "+hierarchy.getValue().getHierarchyName());
				for(Map.Entry<String, Level> level : hierarchy.getValue().getAllLevels().entrySet()) {
					System.out.println("    Level "+level.getValue().getLevelName());
					for(LevelAttribute levelAttribute : level.getValue().getAllLevelAttributes()) {
						System.out.println("      LevelAttribute "+levelAttribute.getLevelAttributeName());
					}
				}
			}
		}
		System.out.println("=====MEASURES=====");
		for(Map.Entry<String, Measure> measure: measures.entrySet()) {
			System.out.println("Measure "+measure.getValue().getMeasureName());
		}
	}
}
