package olapcube;

import java.util.ArrayList;
import java.util.List;

public class LevelAttribute {
	private String levelAttributeName;
	private String levelAttributeDataType;
	private List<Integer> dataSourceDataTypeIds = new ArrayList<Integer>();
//	private List<int> dataSourceDataTypeIds = new ArrayList<int>();
//	private int dataSourceDataTypeId;
	
//	public LevelAttribute(String levelAttributeName, String levelAttributeDataType, int dataSourceDataTypeId) {
//		this.levelAttributeName = levelAttributeName;
//		this.levelAttributeDataType = levelAttributeDataType;
//		this.dataSourceDataTypeId = dataSourceDataTypeId; 
//	}
	
	public LevelAttribute(String levelAttributeName, String levelAttributeDataType) {
		this.levelAttributeName = levelAttributeName;
		this.levelAttributeDataType = levelAttributeDataType;
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
	
	public void addDataSourceDataTypeId (int dataSourceDataTypeId) {
		this.dataSourceDataTypeIds.add(dataSourceDataTypeId);
	}
	
	public List<Integer> getAllDataSourceDataTypeIds() {
		return this.dataSourceDataTypeIds;
	}
	
//	public int getDataSourceDataTypeId() {
//		return dataSourceDataTypeId;
//	}
//	
//	public void setDataSourceDataTypeId(int dataSourceDataTypeId) {
//		this.dataSourceDataTypeId = dataSourceDataTypeId;
//	}
}
