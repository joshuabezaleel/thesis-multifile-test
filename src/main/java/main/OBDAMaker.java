package main;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import olapcube.OLAPCube;
import olapcube.Dimension;
import olapcube.Hierarchy;
import olapcube.Level;
import olapcube.LevelAttribute;
import olapcube.Measure;

@SuppressWarnings("unused")
public class OBDAMaker {
	public void make(OLAPCube cube, DataSourceDataTypeFiles dataSourceDataTypeFiles, String folderPath) throws IOException {
		// Variable definition
		String targetString = "";
		String sourceString = "";
		Measure tempMeasure = new Measure(null,null,null);
		
		FileWriter fw = new FileWriter(folderPath+"/"+cube.getCubeName()+".obda");
		fw.write("[PrefixDeclaration]\n");
		fw.write(":\t\t\t#\n");
		fw.write("owl:\t\thttp://www.w3.org/2002/07/owl#\n");
		fw.write("xml:\t\thttp://www.w3.org/XML/1998/namespace\n");
		fw.write("xsd:\t\thttp://www.w3.org/2001/XMLSchema#\n");
		fw.write("obda:\t\thttps://w3id.org/obda/vocabulary#\n");
		fw.write("qb:\t\t\thttp://purl.org/linked-data/cube#\n");
		fw.write("qb4o:\t\thttp://purl.org/qb4olap/cubes#\n");
		fw.write("rdfs:\t\thttp://www.w3.org/2000/01/rdf-schema#\n");
		fw.write("schema:\t\thttp://example.com/schema/\n");
		fw.write("property:\thttp://example.com/property/\n");	
		fw.write("data:\t\thttp://example.com/data/\n");
		fw.write("quest:\t\thttp://obda.org/quest#\n");
		fw.write("\n");
		fw.write("[MappingDeclaration] @collection [[\n");
		
//		Level tempLevel = new Level(null);
//		Level nextLevel = new Level(null);
		// For each of the level members we will define
		for(Dimension dimension : cube.getAllDimensions().values()) {
			for(Hierarchy hierarchy : dimension.getAllHierarchies().values()) {
				int RollupIter = 0;
				for(Level level : hierarchy.getLevelsList()) {
					// LevelMembers definition
					// Level could have LevelMembers in more than one file, that is why we need to iterate the dataSourceDataTypeId of its primary level attributes
					for(int dataSourceDataTypeId : level.getPrimaryAttribute().getAllDataSourceDataTypeIds()) {
						String fileName = dataSourceDataTypeFiles.getFileNameFromId(dataSourceDataTypeId);
						fw.write("mappingId\tMAPID-"+dimension.getDimensionName()+"Dimension-"+hierarchy.getHierarchyName()+"Hierarchy-"+level.getLevelName()+"Level-LevelMembersDefinition-FromFile"+fileName+"\n");
						fw.write("target\t\tdata:"+level.getLevelName()+"-{"+level.getPrimaryAttribute().getLevelAttributeName()+"} " + "qb4o:memberOf schema:"+level.getLevelName()+" .\n");
						fw.write("source\t\tselect distinct "+level.getPrimaryAttribute().getLevelAttributeName()+" from "+fileName+"\n");
						fw.write("\n");
					}
					
					// DELETE CANDIDATE
//					fw.write("mappingId\tMAPID-"+dimension.getDimensionName()+"Dimension-"+hierarchy.getHierarchyName()+"Hierarchy-"+level.getLevelName()+"Level-LevelMembers\n");
//					fw.write("target\t\tdata:"+level.getLevelName()+"-{"+level.getPrimaryAttribute().getLevelAttributeName()+"} " + "qb4o:memberOf schema:"+level.getLevelName()+" .\n");
//					fw.write("source\t\tselect distinct "+level.getLevelName()+" from "+dataSourceDataTypeFiles.getFileNameFromId(level.getPrimaryAttribute().getDataSourceDataTypeId())+"\n");
//					fw.write("\n");
					
					// LevelAttribute definition
					for(LevelAttribute levelAttribute : level.getAllLevelAttributes()) {
						// CASE 1 : the levelAttribute is the primaryLevelAttribute
						if(levelAttribute.getLevelAttributeName().equals(level.getPrimaryAttribute().getLevelAttributeName())) {
							for(int dataSourceDataTypeId : levelAttribute.getAllDataSourceDataTypeIds()) {
								String fileName = dataSourceDataTypeFiles.getFileNameFromId(dataSourceDataTypeId);
								fw.write("mappingId\tMAPID-"+dimension.getDimensionName()+"Dimension-"+hierarchy.getHierarchyName()+"Hierarchy-"+level.getLevelName()+"Level-"+levelAttribute.getLevelAttributeName()+"PrimaryLevelAttribute-fromFile"+fileName+"\n");
								fw.write("target\t\tdata:"+level.getLevelName()+"-{"+level.getPrimaryAttribute().getLevelAttributeName()+"} "
										+ "property:"+levelAttribute.getLevelAttributeName()+" {"+levelAttribute.getLevelAttributeName()+"}^^xsd:"+levelAttribute.getLevelAttributeDataType()+" .\n");
								fw.write("source\t\tselect distinct "+levelAttribute.getLevelAttributeName()+" from "+fileName+"\n");
								fw.write("\n");
							}
						} else {
							for(int dataSourceDataTypeId : levelAttribute.getAllDataSourceDataTypeIds()) {
								// Compare the primaryLevelAttribute with the file that contains the designated levelAttribute
								for(int dataSourceDataTypeIdPrimaryAttr : level.getPrimaryAttribute().getAllDataSourceDataTypeIds()) {
									String fileName = dataSourceDataTypeFiles.getFileNameFromId(dataSourceDataTypeId);
									String primaryAttrFile = dataSourceDataTypeFiles.getFileNameFromId(dataSourceDataTypeIdPrimaryAttr);
									
									// CASE 2: If the levelAttribute and the primaryLevelAttribute are from the same file.
									// Then only need to use one file source
									if(fileName.equals(primaryAttrFile)) {
										fw.write("mappingId\tMAPID-"+dimension.getDimensionName()+"Dimension-"+hierarchy.getHierarchyName()+"Hierarchy-"+level.getLevelName()+"Level-"+levelAttribute.getLevelAttributeName()+"LevelAttribute-fromFile"+fileName+"primaryAttrFile-"+primaryAttrFile+"\n");
										fw.write("target\t\tdata:"+level.getLevelName()+"-{"+level.getPrimaryAttribute().getLevelAttributeName()+"} "
												+ "property:"+levelAttribute.getLevelAttributeName()+" {"+levelAttribute.getLevelAttributeName()+"}^^xsd:"+levelAttribute.getLevelAttributeDataType()+" .\n");
										fw.write("source\t\tselect distinct "+level.getPrimaryAttribute().getLevelAttributeName()+", "+levelAttribute.getLevelAttributeName()+" from "+fileName+"\n");
										fw.write("\n");
									}
									// CASE 3 : The levelAttribute and the primaryLevelAttribute are from different files.
									else {
										fw.write("mappingId\tMAPID-"+dimension.getDimensionName()+"Dimension-"+hierarchy.getHierarchyName()+"Hierarchy-"+level.getLevelName()+"Level-"+levelAttribute.getLevelAttributeName()+"LevelAttribute-fromFile"+fileName+"primaryAttrFile-"+primaryAttrFile+"\n");
										fw.write("target\t\tdata:"+level.getLevelName()+"-{"+level.getPrimaryAttribute().getLevelAttributeName()+"} "
												+ "property:"+levelAttribute.getLevelAttributeName()+" {"+levelAttribute.getLevelAttributeName()+"}^^xsd:"+levelAttribute.getLevelAttributeDataType()+" .\n");
										fw.write("source\t\tselect distinct "+primaryAttrFile+"."+level.getPrimaryAttribute().getLevelAttributeName()+", "+fileName+"."+levelAttribute.getLevelAttributeName()+" from "+fileName+", "+primaryAttrFile+" where "+fileName+"."+level.getPrimaryAttribute().getLevelAttributeName()+"="+primaryAttrFile+"."+level.getPrimaryAttribute().getLevelAttributeName()+"\n");
										fw.write("\n");
									}
								}
							}
						}
					}
					
					// TODO Revisit this later
					// RollupProperty definition
					// If this is not the last element, then add the next level as the upper level
					if(level != hierarchy.getLastLevel()) {
						Level nextLevel = hierarchy.getLevelsList().get(hierarchy.getLevelsList().indexOf(level)+1);
//						String fileName = dataSourceDataTypeFiles.getFileNameFromId(level.getPrimaryAttribute().getAllDataSourceDataTypeIds().get(0));
						for(int dataSourceDataTypeId : nextLevel.getPrimaryAttribute().getAllDataSourceDataTypeIds()) {
							String nextLevelFile = dataSourceDataTypeFiles.getFileNameFromId(dataSourceDataTypeId);
							fw.write("mappingId\tMAPID-"+dimension.getDimensionName()+"Dimension-"+hierarchy.getHierarchyName()+"Hierarchy-"+level.getLevelName()+"To"+nextLevel.getLevelName()+"-RollupProperty-FromFile"+nextLevelFile+"\n");
							fw.write("target\t\tdata:"+level.getLevelName()+"-{"+level.getPrimaryAttribute().getLevelAttributeName()+"} " + "schema:"+hierarchy.getRollupRelationshipByIndex(RollupIter)+" data:"+nextLevel.getLevelName()+"-{"+nextLevel.getPrimaryAttribute().getLevelAttributeName()+"} .\n");
							fw.write("source\t\tselect distinct "+level.getPrimaryAttribute().getLevelAttributeName()+", "+nextLevel.getPrimaryAttribute().getLevelAttributeName()+" from "+nextLevelFile+"\n");
							fw.write("\n");
						}
						RollupIter++;
					}
				}
			}
		}
		
		/*
		 * Defining the template for target and source
		 */
		targetString = "target\t\tdata:obs-";
		sourceString = "source\t\tselect ";
		
		for(Dimension dimension : cube.getAllDimensions().values()) {
			Hierarchy hierarchy = dimension.getAllHierarchies().get(dimension.getAllHierarchies().keySet().toArray()[0]);
			Level lowestLevel = hierarchy.getLowestLevel();
			
			targetString = targetString.concat("{"+lowestLevel.getPrimaryAttribute().getLevelAttributeName()+"}.");
			sourceString = sourceString.concat(lowestLevel.getPrimaryAttribute().getLevelAttributeName()+", ");
		}
		
		// delete the last "." at tempTarget
		// tempTarget should already be "data:obs-{primAttrLowestLevel1}.{primAttrLowestLevel2}.{primAttrLowestLevel3}"
		targetString = targetString.substring(0, targetString.length()-1);
		// delete the last ", " at tempSource and add " "
		// tempSource should already be "select primAttrLowestLevel1, primAttrLowestLevel2, primAttrLowestLevel3"
		sourceString = sourceString.substring(0, sourceString.length()-2);
		
		// Observation identifier definition
		tempMeasure = cube.getAllMeasures().get(cube.getAllMeasures().keySet().toArray()[0]);
		for(int dataSourceDataTypeId : tempMeasure.getAllDataSourceDataTypeIds()) {
			String fileName = dataSourceDataTypeFiles.getFileNameFromId(dataSourceDataTypeId);
			fw.write("mappingId\tMAPID-ObservationDefinition-FromFile"+fileName+"\n");
			fw.write(targetString+" a qb:Observation .\n");
			fw.write(sourceString+"  from "+fileName+"\n");
			fw.write("\n");
		}
		
		// Observation CubeDataSet definition
		tempMeasure = cube.getAllMeasures().get(cube.getAllMeasures().keySet().toArray()[0]);
		for(int dataSourceDataTypeId : tempMeasure.getAllDataSourceDataTypeIds()) {
			String fileName = dataSourceDataTypeFiles.getFileNameFromId(dataSourceDataTypeId);
			fw.write("mappingId\tMAPID-ObservationCubeDataSetDefinition-FromFile"+fileName+"\n");
			fw.write(targetString+" qb:dataSet data:"+cube.getCubeName()+"CubeDataSet .\n");
			fw.write(sourceString+"  from "+fileName+"\n");
			fw.write("\n");
		}
		
		/*
		 * Observation with lowestLevel triple
		 */
		for(Dimension dimension : cube.getAllDimensions().values()) {
			String tempTarget = targetString;
			String tempSource = sourceString;
			
			Hierarchy hierarchy = dimension.getAllHierarchies().get(dimension.getAllHierarchies().keySet().toArray()[0]);
			Level lowestLevel = hierarchy.getLowestLevel();
			
			tempMeasure = cube.getAllMeasures().get(cube.getAllMeasures().keySet().toArray()[0]);
			for(int dataSourceDataTypeId : tempMeasure.getAllDataSourceDataTypeIds()) {
				String fileName = dataSourceDataTypeFiles.getFileNameFromId(dataSourceDataTypeId);
				fw.write("mappingId\tMAPID-Observation-"+lowestLevel.getLevelName()+"Level-FromFile"+fileName+"\n");
				fw.write(tempTarget+" schema:"+lowestLevel.getLevelName()+" data:"+lowestLevel.getLevelName()+"-{"+lowestLevel.getPrimaryAttribute().getLevelAttributeName()+"} .\n");
				fw.write(tempSource+"  from "+fileName+"\n");
				fw.write("\n");
			}
		}
		
		/*
		 * Observation with all Measures
		 */
		for(Measure measure : cube.getAllMeasures().values()) {
			String tempTarget = targetString;
			String tempSource = sourceString;
			
			for(int dataSourceDataTypeId : measure.getAllDataSourceDataTypeIds()) {
				String fileName = dataSourceDataTypeFiles.getFileNameFromId(dataSourceDataTypeId);
				fw.write("mappingId\tMAPID-Observation-"+measure.getMeasureName()+"Measure-FromFile"+fileName+"\n");
				fw.write(tempTarget+" schema:"+measure.getMeasureName()+" {"+measure.getMeasureName()+"}^^xsd:"+measure.getMeasureDataType()+" .\n");
				fw.write(tempSource+", "+measure.getMeasureName()+" from "+fileName+"\n");
				fw.write("\n");
			}
		}
		
//		// Observation identifier definition
//		String tempTarget = "target\t\tdata:obs-";
//		String tempSource = "source\t\tselect * from ";
//		
//		int dimensionIter = 1;
//		for(Dimension dimension : cube.getAllDimensions().values()) {
//			// You only need to take one hierarchy from each of the dimension because it would result
//			// in the same Level for the lowestLevel.
//			Hierarchy hierarchy = dimension.getAllHierarchies().get(dimension.getAllHierarchies().keySet().toArray()[0]);
//			Level lowestLevel = hierarchy.getLowestLevel();
//			
//			tempTarget = tempTarget.concat("{"+lowestLevel.getPrimaryAttribute().getLevelAttributeName()+"}.");
//			tempSource = tempSource.concat("(");
//			
//			int fileNum = 0;
//			for(int dataSourceDataTypeId : lowestLevel.getPrimaryAttribute().getAllDataSourceDataTypeIds()) {
//				String fileName = dataSourceDataTypeFiles.getFileNameFromId(dataSourceDataTypeId);
//				tempSource = tempSource.concat("select "+lowestLevel.getPrimaryAttribute().getLevelAttributeName()+" from "+fileName);
//			}
//			tempSource = tempSource.concat(") as a"+dimensionIter+", ");
//		}
//		
//		// TODO
//		// delete the last "." at tempTarget
//		tempTarget = tempTarget.substring(0, tempTarget.length()-1);
//		// delete the last "," at tempSource
//		tempSource = tempSource.substring(0, tempSource.length()-2);
		
//		List<String> primaryAttributes = new ArrayList<String>();
//		for(int i=0;i<dimensions.size();i++) {
//			for(int j=0;j<dimensions.get(i).hierarchies.size();j++) {
//				tempTarget = tempTarget.concat("{"+dimensions.get(i).hierarchies.get(j).getLowestLevel().getPrimaryAttribute()+"}.");
//				tempSource = tempSource.concat(dimensions.get(i).hierarchies.get(j).getLowestLevel().getPrimaryAttribute()+", ");
//			}
//		}
//		// Delete the last "."
//		tempTarget = tempTarget.substring(0, tempTarget.length()-1);
//		// Delete the last ", "
//		tempSource = tempSource.substring(0, tempSource.length()-2);
//		
//		fw.write("mappingId\tMAPID-Observation\n");
//		fw.write(tempTarget+" a qb:Observation .\n");
//		fw.write(tempSource+"  from "+fileName+"\n");
//		fw.write("\n");
		
		// Observation CubeDataSet definition
		
		// Observation lowestLevel definition
		
		// Observation Measures definition
		
		fw.write("]]");
		fw.close();
		// DELETE CANDIDATE FROM THE LAST THESIS-1 PROJECT
//		for(int i=0;i<dimensions.size();i++) {
//			for(int j=0;j<dimensions.get(i).hierarchies.size();j++) {
//				for(int k=0;k<dimensions.get(i).hierarchies.get(j).levels.size();k++) {
//					tempLevel = dimensions.get(i).hierarchies.get(j).levels.get(k);
//					// LevelMembers definition
//					fw.write("mappingId\tMAPID-"+dimensions.get(i).dimensionName+"Dimension-"+dimensions.get(i).hierarchies.get(j).hierarchyName+"Hierarchy-"+tempLevel.levelName+"Level-LevelMembers\n");
//					fw.write("target\t\tdata:"+tempLevel.levelName+"-{"+tempLevel.getPrimaryAttribute()+"} " + "qb4o:memberOf schema:"+tempLevel.levelName+" .\n");
//					fw.write("source\t\tselect distinct "+tempLevel.levelName+" from "+fileName+"\n");
//					fw.write("\n");
//					
//					// LevelAttribute definition
//					for(Map.Entry<String, String> levelAttribute : dimensions.get(i).hierarchies.get(j).levels.get(k).levelAttributes.entrySet()) {
//						fw.write("mappingId\tMAPID-"+dimensions.get(i).dimensionName+"Dimension-"+dimensions.get(i).hierarchies.get(j).hierarchyName+"Hierarchy-"+tempLevel.levelName+"Level-"+levelAttribute.getKey()+"LevelAttribute\n");
//						fw.write("target\t\tdata:"+tempLevel.levelName+"-{"+tempLevel.getPrimaryAttribute()+"} "
//								+ "property:"+levelAttribute.getKey()+"Property {"+levelAttribute.getKey()+"}^^xsd:"+levelAttribute.getValue()+" .\n");
//						fw.write("source\t\tselect distinct "+levelAttribute.getKey()+" from "+fileName+"\n");
//						fw.write("\n");
//					}
//					
//					// RollupProperty definition
//					// If this is not the last element, then add the next level as the upper level
//					if(k!=dimensions.get(i).hierarchies.get(j).levels.size()-1) {
//						nextLevel = dimensions.get(i).hierarchies.get(j).levels.get(k+1);
//						fw.write("mappingId\tMAPID-"+dimensions.get(i).dimensionName+"Dimension-"+dimensions.get(i).hierarchies.get(j).hierarchyName+"Hierarchy-"+tempLevel.levelName+"To"+nextLevel.levelName+"-RollupProperty\n");
//						fw.write("target\t\tdata:"+tempLevel.levelName+"-{"+tempLevel.getPrimaryAttribute()+"} " + "schema:"+dimensions.get(i).dimensionName+"Dimension-"+dimensions.get(i).hierarchies.get(j).hierarchyName+"Hierarchy-Rollup"+j+" data:"+nextLevel.levelName+"-{"+nextLevel.levelName+"} .\n");
//						fw.write("source\t\tselect distinct "+tempLevel.levelName+", "+nextLevel.levelName+" from "+fileName+"\n");
//						fw.write("\n");
//					}
//					nextLevel = new Level(null);
////					dimensions.get(i).hierarchies.get(j).levels.get(k).getPrimaryAttribute()
//				}
//				tempLevel = new Level(null);
//			}
//		}
//		// Observation definition
//		String tempTarget = "target\t\tdata:obs-";
//		String tempSource = "source\t\tselect distinct ";
////		List<String> primaryAttributes = new ArrayList<String>();
//		for(int i=0;i<dimensions.size();i++) {
//			for(int j=0;j<dimensions.get(i).hierarchies.size();j++) {
//				tempTarget = tempTarget.concat("{"+dimensions.get(i).hierarchies.get(j).getLowestLevel().getPrimaryAttribute()+"}.");
//				tempSource = tempSource.concat(dimensions.get(i).hierarchies.get(j).getLowestLevel().getPrimaryAttribute()+", ");
//			}
//		}
//		// Delete the last "."
//		tempTarget = tempTarget.substring(0, tempTarget.length()-1);
//		// Delete the last ", "
//		tempSource = tempSource.substring(0, tempSource.length()-2);
//		
//		fw.write("mappingId\tMAPID-Observation\n");
//		fw.write(tempTarget+" a qb:Observation .\n");
//		fw.write(tempSource+"  from "+fileName+"\n");
//		fw.write("\n");
//		
//		// Observation CubeDataSet definition
//		fw.write("mappingId\tMAPID-Observation-CubeDataSetDefinition\n");
//		fw.write(tempTarget+" qb:dataSet data:"+fileName+"CubeDataSet .\n");
//		fw.write(tempSource+"  from "+fileName+"\n");
//		fw.write("\n");
//		
//		// Observation with LevelAttributes definition
//		for(int i=0;i<dimensions.size();i++) {
//			for(int j=0;j<dimensions.get(i).hierarchies.size();j++) {
//				fw.write("mappingId\tMAPID-Observation-"+dimensions.get(i).hierarchies.get(j).getLowestLevel().getPrimaryAttribute()+"Level\n");
//				fw.write(tempTarget+" schema:"+dimensions.get(i).hierarchies.get(j).getLowestLevel().getPrimaryAttribute()+" data:"+dimensions.get(i).hierarchies.get(j).getLowestLevel().getPrimaryAttribute()+"-{"+dimensions.get(i).hierarchies.get(j).getLowestLevel().getPrimaryAttribute()+"} .\n");
//				fw.write(tempSource+" from "+fileName+"\n");
//				fw.write("\n");
//			}
//		}
//		
//		// Observation with Measures definition
//		for(int i=0;i<measures.size();i++) {
//			fw.write("mappingId\tMAPID-Observation-"+measures.get(i).measureName+"Measure\n");
//			fw.write(tempTarget+" schema:"+measures.get(i).measureName+" {"+measures.get(i).measureName+"}^^xsd:"+measures.get(i).measureDataType+" .\n");
//			fw.write(tempSource+", "+measures.get(i).measureName+" from "+fileName+"\n");
//			fw.write("\n");
//		}
		
	}
	
}
