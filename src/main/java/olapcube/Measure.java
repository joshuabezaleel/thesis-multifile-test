package olapcube;

import java.util.ArrayList;
import java.util.List;

public class Measure {
	private String measureName;
	private String measureDataType;
	private String measureAggregateFunction;
	private List<Integer> dataSourceDataTypeIds = new ArrayList<Integer>();
//	private int dataSourceDataTypeId;
	
	public Measure(String measureName, String measureDataType, String measureAggregateFunction) {
		this.measureName = measureName;
		this.measureDataType = measureDataType;
		this.measureAggregateFunction = measureAggregateFunction;
	}
	
	public String getMeasureName() {
		return this.measureName;
	}
	
	public String getMeasureDataType() {
		return this.measureDataType;
	}
	
	public String getMeasureAggregateFunction() {
		return this.measureAggregateFunction;
	}
	
	public void setMeasureName(String measureName) {
		this.measureName = measureName;
	}
	
	public void setMeasureDataType(String measureDataType) {
		this.measureDataType = measureDataType;
	}
	
	public void setMeasureAggregateFunction(String measureAggregateFunction) {
		this.measureAggregateFunction = measureAggregateFunction;
	}
	
	public void addDataSourceDataTypeId(int dataSourceDataTypeId) {
		this.dataSourceDataTypeIds.add(dataSourceDataTypeId);
	}
	
	public List<Integer> getAllDataSourceDataTypeIds() {
		return this.dataSourceDataTypeIds;
	}
	
}
