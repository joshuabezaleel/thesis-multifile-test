package olapcube;

import java.util.List;
import java.util.ArrayList;

public class Level {
	private String levelName;
	private List<LevelAttribute> levelAttributes = new ArrayList<LevelAttribute>();
	private LevelAttribute primaryLevelAttribute;
	
	public Level(String levelName){
		this.levelName = levelName;
	}
	
	public void setPrimaryLevelAttribute(LevelAttribute primaryLevelAttribute) {
		this.primaryLevelAttribute = primaryLevelAttribute;
	}
	
	public LevelAttribute getPrimaryAttribute() {
		return primaryLevelAttribute;
	}
	
	public void addLevelAttribute(LevelAttribute levelAttribute) {
		levelAttributes.add(levelAttribute);
	}
	
	public List<LevelAttribute> getAllLevelAttributes() {
		return levelAttributes;
	}
	
	public String getLevelName() {
		return levelName;
	}
}
