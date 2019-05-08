package main;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ArrayList;

public class DataSourceDataTypeFiles {
	private Map<String,ArrayList<DataSourceDataType>> dataSourceDataTypeFiles = new LinkedHashMap<String,ArrayList<DataSourceDataType>>();
	private String folderPath;
	
	
	public void setFolderPath(String folderPath) {
		this.folderPath = folderPath;
	}
	
	public String getFolderPath() {
		return this.folderPath;
	}
	public void addDataSourceDataType(String fileName, ArrayList<DataSourceDataType> listOfDataSourceDataType) {
		dataSourceDataTypeFiles.put(fileName, listOfDataSourceDataType);
		
	}
	
	public Map<String,ArrayList<DataSourceDataType>> getAllDataSourceDataTypes() {
		return this.dataSourceDataTypeFiles;
	}
	
	// Given a fileName, return all of the columns with their data types in a form of ArrayList
	public ArrayList<DataSourceDataType> getDataSourceDataTypes(String fileName){
		return dataSourceDataTypeFiles.get(fileName);
	}
	
	public void printAllDataSourceDataTypes() {
		for(Map.Entry<String, ArrayList<DataSourceDataType>> file : dataSourceDataTypeFiles.entrySet()) {
			System.out.println("File: "+file.getKey()+".csv");
			for(DataSourceDataType columnToDataTypeMapping : file.getValue()) {
				System.out.println("     "+columnToDataTypeMapping.getColumnName()+"|"+columnToDataTypeMapping.getDataType());
			}
		}
	}
	
	public void printAll() {
		for(Entry<String, ArrayList<DataSourceDataType>> file: dataSourceDataTypeFiles.entrySet()) {
			for(DataSourceDataType columnToDataTypeMapping : file.getValue()) {
				System.out.println(columnToDataTypeMapping.getId()+". "+file.getKey()+" | "+columnToDataTypeMapping.getColumnName()+" - "+columnToDataTypeMapping.getDataType());
			}
		}
	}
	
	public DataSourceDataType getLevelAttributeById(int id) {
		DataSourceDataType tempDataSourceDataType = new DataSourceDataType(0,null,null);
		for(Entry<String, ArrayList<DataSourceDataType>> file: dataSourceDataTypeFiles.entrySet()) {
			for(DataSourceDataType columnToDataTypeMapping : file.getValue()) {
				if(columnToDataTypeMapping.getId() == id) {
					tempDataSourceDataType = columnToDataTypeMapping;
				}
			}
		}
		
		return tempDataSourceDataType;
	}
	
	public String getFileNameFromId(int id) {
		String fileName = null;
		for(Entry<String, ArrayList<DataSourceDataType>> file: dataSourceDataTypeFiles.entrySet()) {
			for(DataSourceDataType columnToDataTypeMapping : file.getValue()) {
				if(columnToDataTypeMapping.getId() == id) {
					fileName = file.getKey();
				}
			}
		}
		return fileName;
	}
}
