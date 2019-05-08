package main;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import org.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;

import olapcube.Dimension;
import olapcube.Hierarchy;
import olapcube.Level;
import olapcube.LevelAttribute;
import olapcube.Measure;
import olapcube.OLAPCube;

public class OLAPCubeHandler {
	
	@SuppressWarnings("resource")
	public OLAPCube make(DataSourceDataTypeFiles dataSourceDataTypeFiles) {
		String userInput;
		int userInputInt;
		Scanner reader = new Scanner(System.in);
		String currDimension, currHierarchy, currLevel;
		Measure tempMeasure = new Measure(null,null,null,0);
		Dimension tempDimension = new Dimension(null);
		Hierarchy tempHierarchy = new Hierarchy(null);
		Level tempLevel = new Level(null);
		LevelAttribute tempLevelAttribute = new LevelAttribute(null,null,0);
		DataSourceDataType tempDataSourceDataType = new DataSourceDataType(0,null,null);

		System.out.println("This CLI program will guide you through the process of making a OLAP cube structure");
		System.out.print("Input the cube's name here: ");
		userInput = reader.nextLine();
		OLAPCube cube = new OLAPCube(userInput);
		
		// Dimensions definitions
		do {
			System.out.println("====== DIMENSION ======");
			System.out.print("Input new Dimension: ");
			reader = new Scanner(System.in);
			userInput = reader.nextLine();
			
			/*
			 * Add the dimension to the cube and
			 * define it's hierarchies if userInput does not equal to "end"
			 */
			if(!userInput.equals("end")) {
				currDimension = userInput;
				tempDimension = new Dimension(currDimension);
				cube.addDimension(userInput, tempDimension);
				
				do {
					System.out.println("====== HIERARCHY ======");
					System.out.print("Input new Hierarchy for dimension <<"+currDimension+">>: ");
					reader = new Scanner(System.in);
					userInput = reader.nextLine();
					
					/*
					 * Add the hierarchy to the cube and
					 * define it's levels if userInput does not equal to "end"
					 */
					if(!userInput.equals("end")) {
						currHierarchy = userInput;
						tempHierarchy = new Hierarchy(currHierarchy);
						cube.getDimensionByName(currDimension).addHierarchy(currHierarchy, tempHierarchy);
						
						boolean isLowestLevel = true;
						do {
							System.out.println("====== LEVEL ======");
							System.out.println("The order of the input is from the lowest level to the highest level (ascending): ");
							System.out.print("Input new Level for hierarchy <<"+currHierarchy+">>: ");
							
							reader = new Scanner(System.in);
							userInput = reader.nextLine();
							
							/*
							 * Add the level to the cube and
							 * define it's level attributes if userInput does not equal to "end"
							 */
							if(!userInput.equals("end")) {
								currLevel = userInput;
								tempLevel = new Level(currLevel);
								cube.getDimensionByName(currDimension).getHierarchyByName(currHierarchy).addLevel(currLevel, tempLevel);
								cube.getDimensionByName(currDimension).getHierarchyByName(currHierarchy).addLevelToList(tempLevel);
								
								if(isLowestLevel) {
									cube.getDimensionByName(currDimension).getHierarchyByName(currHierarchy).setLowestLevel(tempLevel);
									isLowestLevel = false;
								}

								boolean isPrimaryAttribute = true;
								
								do {
									System.out.println("====== LEVEL ATTRIBUTES ======");
									System.out.println("Choose which Level Attributes from the columns below for level <<"+currLevel+">>.");
									System.out.println("The first Level Attributes chosen will be the Primary Attribute (identifier).");
									dataSourceDataTypeFiles.printAll();
									System.out.print("LevelAttribute chosen (input the number): ");
									reader = new Scanner(System.in);
									userInput = reader.nextLine();
									
									if(!userInput.equals("end")) {
//										String tempLevelAttribute = columns[Integer.parseInt(userInput)-1];
//										
//										level.levelAttributes.put(tempLevelAttribute,columnDataTypes.get(tempLevelAttribute));
//										System.out.println(tempLevelAttribute+" "+columnDataTypes.get(tempLevelAttribute));
										userInputInt = Integer.parseInt(userInput);
										
										tempDataSourceDataType = dataSourceDataTypeFiles.getLevelAttributeById(userInputInt);
										tempLevelAttribute = new LevelAttribute(tempDataSourceDataType.getColumnName(),tempDataSourceDataType.getDataType(), userInputInt);
										
										if(isPrimaryAttribute) {
											cube.getDimensionByName(currDimension).getHierarchyByName(currHierarchy).getLevelByName(currLevel).setPrimaryLevelAttribute(tempLevelAttribute);
											isPrimaryAttribute = false;
										}
										cube.getDimensionByName(currDimension).getHierarchyByName(currHierarchy).getLevelByName(currLevel).addLevelAttribute(tempLevelAttribute);
									}
								} while(!userInput.equals("end"));
//								level = new Level(null);
								userInput="";
							}
							
						} while(!userInput.equals("end"));
//						dimension.hierarchies.add(hierarchy);
//						hierarchy = new Hierarchy(null);
						userInput = "";
					}
				} while(!userInput.equals("end"));
//				cube.dimensions.add(dimension);
//				dimensions.add(dimension);
//				dimension = new Dimension(null);
				userInput = "";
			}
		} while(!userInput.equals("end"));
		
		// Measures definition
		do {
			System.out.println("Choose Measure from the columns below (Input \"end\" to end the process): ");
			dataSourceDataTypeFiles.printAll();
			reader = new Scanner(System.in);
			userInput = reader.nextLine();
			
			if(!userInput.equals("end")) {
				userInputInt = Integer.parseInt(userInput);
//				String tempMeasure = columns[Integer.parseInt(userInput)-1];
				System.out.println("Choose AggregateFunction for the Measure "+dataSourceDataTypeFiles.getLevelAttributeById(userInputInt).getColumnName());
				System.out.println("1=sum, 2=avg, 3=count, 4=min, 5=max");
				reader = new Scanner(System.in);
				userInput = reader.nextLine();
				switch (Integer.parseInt(userInput)) {
					case 1:
						tempMeasure = new Measure(dataSourceDataTypeFiles.getLevelAttributeById(userInputInt).getColumnName(),dataSourceDataTypeFiles.getLevelAttributeById(userInputInt).getDataType(),"sum",userInputInt);
						break;
					case 2:
						tempMeasure = new Measure(dataSourceDataTypeFiles.getLevelAttributeById(userInputInt).getColumnName(),dataSourceDataTypeFiles.getLevelAttributeById(userInputInt).getDataType(),"avg",userInputInt);
						break;
					case 3:
						tempMeasure = new Measure(dataSourceDataTypeFiles.getLevelAttributeById(userInputInt).getColumnName(),dataSourceDataTypeFiles.getLevelAttributeById(userInputInt).getDataType(),"count",userInputInt);
						break;
					case 4:
						tempMeasure = new Measure(dataSourceDataTypeFiles.getLevelAttributeById(userInputInt).getColumnName(),dataSourceDataTypeFiles.getLevelAttributeById(userInputInt).getDataType(),"min",userInputInt);
						break;
					case 5:
						tempMeasure = new Measure(dataSourceDataTypeFiles.getLevelAttributeById(userInputInt).getColumnName(),dataSourceDataTypeFiles.getLevelAttributeById(userInputInt).getDataType(),"max",userInputInt);
						break;
				}
				cube.addMeasure(dataSourceDataTypeFiles.getLevelAttributeById(userInputInt).getColumnName(), tempMeasure);

			}
		} while(!userInput.equals("end"));
		
		System.out.println("End of creating cube structure process");
		reader.close();
		
		return cube;
	}
	
	public void encodeToJSON(OLAPCube cube,String folderPath) {
		JSONObject cubeJSON = new JSONObject();
		cubeJSON.put("cubeName", cube.getCubeName());
		
		JSONArray dimensionsJSON = new JSONArray();
		
		for(Dimension dimension : cube.getAllDimensions().values()) {
			JSONObject dimensionJSON = new JSONObject();
			dimensionJSON.put("dimensionName", dimension.getDimensionName());
			
			JSONArray hierarchiesJSON = new JSONArray();
			
			for(Hierarchy hierarchy : dimension.getAllHierarchies().values()) {
				JSONObject hierarchyJSON = new JSONObject();
				hierarchyJSON.put("hierarchyName", hierarchy.getHierarchyName());
				
				// Lowest Level
				hierarchyJSON.put("lowestLevelName", hierarchy.getLowestLevel().getLevelName());
				
				JSONArray levelsJSON = new JSONArray();
				
				for(Level level : hierarchy.getAllLevels().values()) {
					JSONObject levelJSON = new JSONObject();
					levelJSON.put("levelName", level.getLevelName());
					
					// Primary Level Attribute
					levelJSON.put("primaryLevelAttribute", level.getPrimaryAttribute().getLevelAttributeName());
					
					// Level Attributes
					JSONArray levelAttributesJSON = new JSONArray();
					for(LevelAttribute levelAttribute : level.getAllLevelAttributes()) {
						JSONObject levelAttributeJSON = new JSONObject();
						levelAttributeJSON.put("levelAttributeName", levelAttribute.getLevelAttributeName());
						levelAttributeJSON.put("levelAttributeDataType", levelAttribute.getLevelAttributeDataType());
						levelAttributeJSON.put("dataSourceDataTypeId", levelAttribute.getDataSourceDataTypeId());
						levelAttributesJSON.put(levelAttributeJSON);
					}
					
					levelJSON.put("levelAttributes", levelAttributesJSON);
					
					levelsJSON.put(levelJSON);
				}
				
				hierarchyJSON.put("levels", levelsJSON);
				
				hierarchiesJSON.put(hierarchyJSON);
			}
			
			dimensionJSON.put("hierarchies", hierarchiesJSON);
			
			dimensionsJSON.put(dimensionJSON);
		}
		
		JSONArray measuresJSON = new JSONArray();
		
		for(Measure measure : cube.getAllMeasures().values()) {
			JSONObject measureJSON = new JSONObject();
			measureJSON.put("measureName", measure.getMeasureName());
			measureJSON.put("measureDataType", measure.getMeasureDataType());
			measureJSON.put("measureAggregateFunction", measure.getMeasureAggregateFunction());
			measuresJSON.put(measureJSON);
		}
		
		
		cubeJSON.put("dimensions", dimensionsJSON);
		cubeJSON.put("measures", measuresJSON);
		
		PrintWriter pw;
		try {
			pw = new PrintWriter(folderPath+"/output/olapCube.json");
			pw.write(cubeJSON.toString(2));
			pw.flush();
			pw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public OLAPCube decodeFromJSON(String folderPath) throws IOException {
		// Temporary variables
		String currDimension, currHierarchy, currLevel, lowestLevel, primaryLevelAttribute;
		Measure tempMeasure = new Measure(null,null,null,0);
		Dimension tempDimension = new Dimension(null);
		Hierarchy tempHierarchy = new Hierarchy(null);
		Level tempLevel = new Level(null);
		LevelAttribute tempLevelAttribute = new LevelAttribute(null,null,0);
		
		File file = new File(folderPath+"/output/olapCube.json");
		String fileContent = FileUtils.readFileToString(file, "utf-8");
		
		JSONObject cubeJSON = new JSONObject(fileContent);
		OLAPCube cube = new OLAPCube(cubeJSON.getString("cubeName"));
		
		// Dimension
		JSONArray dimensionsJSON = cubeJSON.getJSONArray("dimensions");
		for(Object dimension : dimensionsJSON) {
			JSONObject dimensionJSON = (JSONObject) dimension;
			
			currDimension = dimensionJSON.getString("dimensionName");
			tempDimension = new Dimension(currDimension);
			cube.addDimension(currDimension, tempDimension);
			
			JSONArray hierarchiesJSON = dimensionJSON.getJSONArray("hierarchies");
			for(Object hierarchy : hierarchiesJSON) {
				JSONObject hierarchyJSON = (JSONObject) hierarchy;
				
				currHierarchy = hierarchyJSON.getString("hierarchyName");
				tempHierarchy = new Hierarchy(currHierarchy);
				cube.getDimensionByName(currDimension).addHierarchy(currHierarchy, tempHierarchy);
				
				lowestLevel = hierarchyJSON.getString("lowestLevelName");
				
				JSONArray levelsJSON = hierarchyJSON.getJSONArray("levels");
				for(Object level : levelsJSON) {
					JSONObject levelJSON = (JSONObject) level;
					
					currLevel = levelJSON.getString("levelName");
					tempLevel = new Level(currLevel);
					cube.getDimensionByName(currDimension).getHierarchyByName(currHierarchy).addLevel(currLevel, tempLevel);
					cube.getDimensionByName(currDimension).getHierarchyByName(currHierarchy).addLevelToList(tempLevel);
					
					// Lowest Level
					if(currLevel.equals(lowestLevel)) {
						cube.getDimensionByName(currDimension).getHierarchyByName(currHierarchy).setLowestLevel(tempLevel);
					}
					
					primaryLevelAttribute = levelJSON.getString("primaryLevelAttributeName");
					
					JSONArray levelAttributesJSON = levelJSON.getJSONArray("levelAttributes");
					for(Object levelAttribute : levelAttributesJSON) {
						JSONObject levelAttributeJSON = (JSONObject) levelAttribute;
						
						tempLevelAttribute = new LevelAttribute(levelAttributeJSON.getString("levelAttributeName"),levelAttributeJSON.getString("levelAttributeDataType"),levelAttributeJSON.getInt("dataSourceDataTypeId"));
						cube.getDimensionByName(currDimension).getHierarchyByName(currHierarchy).getLevelByName(currLevel).addLevelAttribute(tempLevelAttribute);
						
						// Primary Level Attribute
						if(levelAttributeJSON.getString("levelAttributeName").equals(primaryLevelAttribute)) {
							cube.getDimensionByName(currDimension).getHierarchyByName(currHierarchy).getLevelByName(currLevel).setPrimaryLevelAttribute(tempLevelAttribute);
						}
						
					}
				}
			}
		}
		
		// Measure
		JSONArray measuresJSON = cubeJSON.getJSONArray("measures");
		for(Object measure : measuresJSON) {
			JSONObject measureJSON = (JSONObject) measure;
			
			tempMeasure = new Measure(measureJSON.getString("measureName"),measureJSON.getString("measureDataType"),measureJSON.getString("measureAggregateFunction"),measureJSON.getInt("dataSourceDataTypeId"));
			cube.addMeasure(measureJSON.getString("measureName"), tempMeasure);
		}
		
		return cube;
	}
}
