package main;

import java.io.FileWriter;
import java.util.Map;

import olapcube.OLAPCube;
import olapcube.Dimension;
import olapcube.Hierarchy;
import olapcube.Level;
import olapcube.LevelAttribute;

@SuppressWarnings("unused")
public class OBDAMaker {
	public void make(OLAPCube cube, DataSourceDataTypeFiles dataSourceDataTypeFiles, String folderPath) {
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
				for(Level level : hierarchy.getLevelsList()) {
					// LevelMembers definition
					fw.write("mappingId\tMAPID-"+dimension.getDimensionName()+"Dimension-"+hierarchy.getHierarchyName()+"Hierarchy-"+level.getLevelName()+"Level-LevelMembers\n");
					fw.write("target\t\tdata:"+level.getLevelName()+"-{"+level.getPrimaryAttribute().getLevelAttributeName()+"} " + "qb4o:memberOf schema:"+level.getLevelName()+" .\n");
					fw.write("source\t\tselect distinct "+level.getLevelName()+" from "+dataSourceDataTypeFiles.getFileNameFromId(level.getPrimaryAttribute().getDataSourceDataTypeId())+"\n");
					fw.write("\n");
					
					// LevelAttribute definition
					for(LevelAttribute levelAttribute : level.getAllLevelAttributes()) {
						fw.write("mappingId\tMAPID-"+dimension.getDimensionName()+"Dimension-"+hierarchy.getHierarchyName()+"Hierarchy-"+level.getLevelName()+"Level-"+levelAttribute.getLevelAttributeName()+"LevelAttribute\n");
						fw.write("target\t\tdata:"+level.getLevelName()+"-{"+level.getPrimaryAttribute().getLevelAttributeName()+"} "
								+ "property:"+levelAttribute.getLevelAttributeName()+"Property {"+levelAttribute.getLevelAttributeName()+"}^^xsd:"+levelAttribute.getLevelAttributeDataType()+" .\n");
						fw.write("source\t\tselect distinct "+levelAttribute.getLevelAttributeName()+" from "+dataSourceDataTypeFiles.getFileNameFromId(levelAttribute.getDataSourceDataTypeId())+"\n");
						fw.write("\n");
					}
					
					// RollupProperty definition
					// If this is not the last element, then add the next level as the upper level
					if(level != hierarchy.getLastLevel()) {
						Level nextLevel = hierarchy.getLevelsList().get(hierarchy.getLevelsList().indexOf(level)+1);
						fw.write("mappingId\tMAPID-"+dimension.getDimensionName()+"Dimension-"+hierarchy.getHierarchyName()+"Hierarchy-"+level.getLevelName()+"To"+nextLevel.getLevelName()+"-RollupProperty\n");
						fw.write("target\t\tdata:"+level.getLevelName()+"-{"+level.getPrimaryAttribute().getLevelAttributeName()+"} " + "schema:"+dimension.getDimensionName()+"Dimension-"+hierarchy.getHierarchyName()+"Hierarchy-Rollup"+j+" data:"+nextLevel.getLevelName()+"-{"+nextLevel.getLevelName()+"} .\n");
						fw.write("source\t\tselect distinct "+level.getLevelName()+", "+nextLevel.getLevelName()+" from "++"\n");
						fw.write("\n");
					}
				}
			}
		}
		
		// Observation definition
		
		
		for(int i=0;i<dimensions.size();i++) {
			for(int j=0;j<dimensions.get(i).hierarchies.size();j++) {
				for(int k=0;k<dimensions.get(i).hierarchies.get(j).levels.size();k++) {
					tempLevel = dimensions.get(i).hierarchies.get(j).levels.get(k);
					// LevelMembers definition
					fw.write("mappingId\tMAPID-"+dimensions.get(i).dimensionName+"Dimension-"+dimensions.get(i).hierarchies.get(j).hierarchyName+"Hierarchy-"+tempLevel.levelName+"Level-LevelMembers\n");
					fw.write("target\t\tdata:"+tempLevel.levelName+"-{"+tempLevel.getPrimaryAttribute()+"} " + "qb4o:memberOf schema:"+tempLevel.levelName+" .\n");
					fw.write("source\t\tselect distinct "+tempLevel.levelName+" from "+fileName+"\n");
					fw.write("\n");
					
					// LevelAttribute definition
					for(Map.Entry<String, String> levelAttribute : dimensions.get(i).hierarchies.get(j).levels.get(k).levelAttributes.entrySet()) {
						fw.write("mappingId\tMAPID-"+dimensions.get(i).dimensionName+"Dimension-"+dimensions.get(i).hierarchies.get(j).hierarchyName+"Hierarchy-"+tempLevel.levelName+"Level-"+levelAttribute.getKey()+"LevelAttribute\n");
						fw.write("target\t\tdata:"+tempLevel.levelName+"-{"+tempLevel.getPrimaryAttribute()+"} "
								+ "property:"+levelAttribute.getKey()+"Property {"+levelAttribute.getKey()+"}^^xsd:"+levelAttribute.getValue()+" .\n");
						fw.write("source\t\tselect distinct "+levelAttribute.getKey()+" from "+fileName+"\n");
						fw.write("\n");
					}
					
					// RollupProperty definition
					// If this is not the last element, then add the next level as the upper level
					if(k!=dimensions.get(i).hierarchies.get(j).levels.size()-1) {
						nextLevel = dimensions.get(i).hierarchies.get(j).levels.get(k+1);
						fw.write("mappingId\tMAPID-"+dimensions.get(i).dimensionName+"Dimension-"+dimensions.get(i).hierarchies.get(j).hierarchyName+"Hierarchy-"+tempLevel.levelName+"To"+nextLevel.levelName+"-RollupProperty\n");
						fw.write("target\t\tdata:"+tempLevel.levelName+"-{"+tempLevel.getPrimaryAttribute()+"} " + "schema:"+dimensions.get(i).dimensionName+"Dimension-"+dimensions.get(i).hierarchies.get(j).hierarchyName+"Hierarchy-Rollup"+j+" data:"+nextLevel.levelName+"-{"+nextLevel.levelName+"} .\n");
						fw.write("source\t\tselect distinct "+tempLevel.levelName+", "+nextLevel.levelName+" from "+fileName+"\n");
						fw.write("\n");
					}
					nextLevel = new Level(null);
//					dimensions.get(i).hierarchies.get(j).levels.get(k).getPrimaryAttribute()
				}
				tempLevel = new Level(null);
			}
		}
		// Observation definition
		String tempTarget = "target\t\tdata:obs-";
		String tempSource = "source\t\tselect distinct ";
//		List<String> primaryAttributes = new ArrayList<String>();
		for(int i=0;i<dimensions.size();i++) {
			for(int j=0;j<dimensions.get(i).hierarchies.size();j++) {
				tempTarget = tempTarget.concat("{"+dimensions.get(i).hierarchies.get(j).getLowestLevel().getPrimaryAttribute()+"}.");
				tempSource = tempSource.concat(dimensions.get(i).hierarchies.get(j).getLowestLevel().getPrimaryAttribute()+", ");
			}
		}
		// Delete the last "."
		tempTarget = tempTarget.substring(0, tempTarget.length()-1);
		// Delete the last ", "
		tempSource = tempSource.substring(0, tempSource.length()-2);
		
		fw.write("mappingId\tMAPID-Observation\n");
		fw.write(tempTarget+" a qb:Observation .\n");
		fw.write(tempSource+"  from "+fileName+"\n");
		fw.write("\n");
		
		// Observation CubeDataSet definition
		fw.write("mappingId\tMAPID-Observation-CubeDataSetDefinition\n");
		fw.write(tempTarget+" qb:dataSet data:"+fileName+"CubeDataSet .\n");
		fw.write(tempSource+"  from "+fileName+"\n");
		fw.write("\n");
		
		// Observation with LevelAttributes definition
		for(int i=0;i<dimensions.size();i++) {
			for(int j=0;j<dimensions.get(i).hierarchies.size();j++) {
				fw.write("mappingId\tMAPID-Observation-"+dimensions.get(i).hierarchies.get(j).getLowestLevel().getPrimaryAttribute()+"Level\n");
				fw.write(tempTarget+" schema:"+dimensions.get(i).hierarchies.get(j).getLowestLevel().getPrimaryAttribute()+" data:"+dimensions.get(i).hierarchies.get(j).getLowestLevel().getPrimaryAttribute()+"-{"+dimensions.get(i).hierarchies.get(j).getLowestLevel().getPrimaryAttribute()+"} .\n");
				fw.write(tempSource+" from "+fileName+"\n");
				fw.write("\n");
			}
		}
		
		// Observation with Measures definition
		for(int i=0;i<measures.size();i++) {
			fw.write("mappingId\tMAPID-Observation-"+measures.get(i).measureName+"Measure\n");
			fw.write(tempTarget+" schema:"+measures.get(i).measureName+" {"+measures.get(i).measureName+"}^^xsd:"+measures.get(i).measureDataType+" .\n");
			fw.write(tempSource+", "+measures.get(i).measureName+" from "+fileName+"\n");
			fw.write("\n");
		}
		
		fw.write("]]");
		fw.close();
	}
	
}
