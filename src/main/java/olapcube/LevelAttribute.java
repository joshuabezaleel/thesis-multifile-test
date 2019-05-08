package olapcube;

public class LevelAttribute {
	private String levelAttributeName;
	private String levelAttributeDataType;
	private int dataSourceDataTypeId;
	
	public LevelAttribute(String levelAttributeName, String levelAttributeDataType, int dataSourceDataTypeId) {
		this.levelAttributeName = levelAttributeName;
		this.levelAttributeDataType = levelAttributeDataType;
		this.dataSourceDataTypeId = dataSourceDataTypeId; 
	}
	
	public String getLevelAttributeName() {
		return levelAttributeName;
	}
	
	public void setLevelAttributeName(String levelAttributeName) {
		this.levelAttributeName = levelAttributeName;
	}
	
	public String getLevelAttributeDataType() {
		return levelAttributeDataType;
	}
	
	public void setLevelAttributeDataType(String levelAttributeDataType) {
		this.levelAttributeDataType = levelAttributeDataType;
	}
	
	public int getDataSourceDataTypeId() {
		return dataSourceDataTypeId;
	}
	
	public void setDataSourceDataTypeId(int dataSourceDataTypeId) {
		this.dataSourceDataTypeId = dataSourceDataTypeId;
	}
}
