package olapcube;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;

public class Hierarchy {
	private String hierarchyName;
	private Map<String, Level> levels = new LinkedHashMap<String, Level>();
	private List<Level> levelsList= new ArrayList<Level>();
	private Level lowestLevel;
	
	public void addLevelToList(Level level) {
		levelsList.add(level);
	}
	
	public List<Level> getLevelsList() {
		return levelsList;
	}
	
	public Hierarchy(String hierarchyName) {
		this.hierarchyName = hierarchyName;
	}

	public String getHierarchyName() {
		return hierarchyName;
	}
	
	public void setLowestLevel(Level level) {
		this.lowestLevel = level;
	}
	public Level getLowestLevel() {
		return this.lowestLevel;
	}
	
	public void addLevel(String levelName, Level level) {
		levels.put(levelName, level);
	}
	
	public int getLevelsCount() {
		return levels.size();
	}
	
	public Level getLevelByName(String levelName) {
		return levels.get(levelName);
	}
	
	public Map<String, Level> getAllLevels() {
		return levels;
	}
	
	public Level getLastLevel() {
		return levelsList.get(levelsList.size()-1);
	}
}
