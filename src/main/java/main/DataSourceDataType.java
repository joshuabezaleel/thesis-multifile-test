package main;

public class DataSourceDataType {
	private int id;
	private String columnName;
	private String dataType;
	
	public DataSourceDataType(int id, String columnName, String dataType) {
		this.id = id;
		this.columnName = columnName;
		this.dataType = dataType;
	}
	
	public int getId() {
		return id;
	}
	
	public String getColumnName() {
		return columnName;
	}
	
	public String getDataType() {
		return dataType;
	}
	
}
