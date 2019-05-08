package olapcube;

public class Measure {
	private String measureName;
	private String measureDataType;
	private String measureAggregateFunction;
	private int dataSourceDataTypeId;
	
	public Measure(String measureName, String measureDataType, String measureAggregateFunction, int dataSourceDataTypeId) {
		this.measureName = measureName;
		this.measureDataType = measureDataType;
		this.measureAggregateFunction = measureAggregateFunction;
		this.dataSourceDataTypeId = dataSourceDataTypeId;
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
	
	public int getDataSourceDataTypeId() {
		return this.dataSourceDataTypeId;
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
	
	public void setDataSourceDataTypeId(int dataSourceDataTypeId) {
		this.dataSourceDataTypeId = dataSourceDataTypeId;
	}
}
