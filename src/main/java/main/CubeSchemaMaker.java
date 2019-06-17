package main;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;

import olapcube.Dimension;
import olapcube.Hierarchy;
import olapcube.Level;
import olapcube.LevelAttribute;
import olapcube.Measure;
import olapcube.OLAPCube;

@SuppressWarnings("unused")
public class CubeSchemaMaker {
	
	public void make(OLAPCube cube, String folderPath) throws FileNotFoundException {
		String cubeName = cube.getCubeName();
		ValueFactory vf = SimpleValueFactory.getInstance();
		BNode component = vf.createBNode();
		
		
		// Setting up
		ModelBuilder builder = new ModelBuilder();
		FileOutputStream out = new FileOutputStream(folderPath+"/"+cubeName+".ttl");
		
		// Setting namespaces
		builder.setNamespace("data","http://example.com/data/");
		builder.setNamespace("schema","http://example.com/schema/");
		builder.setNamespace("property","http://example.com/property/");
		builder.setNamespace(RDF.NS);
		builder.setNamespace(RDFS.NS);
		builder.setNamespace("xsd","http://www.w3.org/2001/XMLSchema#");
		builder.setNamespace("qb","http://purl.org/linked-data/cube#");
		builder.setNamespace("qb4o","http://purl.org/qb4olap/cubes#");

		// CubeDataSet
		builder.subject("data:"+cubeName+"CubeDataSet")
			.add(RDF.TYPE, "qb:DataSet")
			.add("qb:structure", "schema:"+cubeName+"CubeDataStructureDefinition");
		
		// CubeDataStructureDefinition
		builder.subject("schema:"+cubeName+"CubeDataStructureDefinition")
			.add(RDF.TYPE, "qb:DataStructureDefinition");
		
		// CubeComponents
		// Dimension
		for(Dimension dimension : cube.getAllDimensions().values()) {
			for(Hierarchy hierarchy : dimension.getAllHierarchies().values()) {
				String lowestLevelName = hierarchy.getLowestLevel().getLevelName();
				component = vf.createBNode();
				builder.subject("schema:"+cubeName+"CubeDataStructureDefinition")
				.add("qb:component", component)
			.subject(component)
				.add("qb:level","schema:"+lowestLevelName)
				.add("qb4o:cardinality", "qb4o:ManyToOne");
			}
		}
//		for(int i=0; i<dimensions.size();i++) {
//			for(int j=0;j<dimensions.get(i).hierarchies.size();j++) {
//				String lowestLevel = dimensions.get(i).hierarchies.get(j).getLowestLevel().levelName;
//				BNode component = vf.createBNode();
//				builder.subject("schema:"+fileName+"CubeDataStructureDefinition")
//					.add("qb:component", component)
//				.subject(component)
//					.add("qb:level","schema:"+lowestLevel)
//					.add("qb4o:cardinality", "qb4o:ManyToOne");
//			}
//		}
		
//		// Measure
		for(Measure measure : cube.getAllMeasures().values()) {
			component = vf.createBNode();
			builder.subject("schema:"+cubeName+"CubeDataStructureDefinition")
			.add("qb:component", component)
		.subject(component)
			.add("qb:measure", "schema:"+measure.getMeasureName())
			.add("qb4o:aggregateFunction", "qb4o:"+measure.getMeasureAggregateFunction());
		}
//		for(int i=0;i<measures.size();i++) {
//			BNode component = vf.createBNode();
//			builder.subject("schema:"+fileName+"CubeDataStructureDefinition")
//				.add("qb:component", component)
//			.subject(component)
//				.add("qb:measure", "schema:"+measures.get(i).measureName)
//				.add("qb4o:aggregateFunction", "qb4o:"+measures.get(i).measureAggregateFunction);
//		}
//		
//		// Measures definition (measureProperty)
		for(Measure measure : cube.getAllMeasures().values()) {
			builder.subject("schema:"+measure.getMeasureName())
				.add(RDF.TYPE, "qb:MeasureProperty")
				.add(RDFS.RANGE, "xsd:"+measure.getMeasureDataType());
		}
//		for(int i=0; i<measures.size(); i++) {
//			builder.subject("schema:"+measures.get(i).measureName)
//				.add(RDF.TYPE, "qb:MeasureProperty")
//				.add(RDFS.RANGE, "xsd:"+measures.get(i).measureDataType);
//		}
//		
//		// Dimension definition (dimensionProperty)
		for(Dimension dimension : cube.getAllDimensions().values()) {
			// DimensionProperty
			builder.add("schema:"+dimension.getDimensionName()+"Dimension",RDF.TYPE, "qb:DimensionProperty");
			for(Hierarchy hierarchy : dimension.getAllHierarchies().values()) {
				// hasHierarchy
				builder.add("schema:"+dimension.getDimensionName()+"Dimension", "qb4o:hasHierarchy","schema:"+hierarchy.getHierarchyName()+"Hierarchy");
				// Hierarchy
				builder.subject("schema:"+hierarchy.getHierarchyName()+"Hierarchy")
				.add(RDF.TYPE, "qb4o:Hierarchy")
				// inDimension
				.add("qb4o:inDimension", "schema:"+dimension.getDimensionName()+"Dimension");
				for(Level level : hierarchy.getAllLevels().values()) {
					// hasLevel
					builder.add("schema:"+hierarchy.getHierarchyName()+"Hierarchy","qb4o:hasLevel","schema:"+level.getLevelName());
					// LevelProperty
					builder.add("schema:"+level.getLevelName(),RDF.TYPE, "qb4o:LevelProperty");
					for(LevelAttribute levelAttribute : level.getAllLevelAttributes()) {
						// hasAttribute
						builder.add("schema:"+level.getLevelName(), "qb4o:hasAttribute","property:"+levelAttribute.getLevelAttributeName());
						// LevelAttribute
						builder.add("property:"+levelAttribute.getLevelAttributeName(),RDF.TYPE,"qb4o:LevelAttribute");
						// rdfs:range
						builder.add("property:"+levelAttribute.getLevelAttributeName(),RDFS.RANGE,"xsd:"+levelAttribute.getLevelAttributeDataType());
					}
				}
			}
			
			// RollupProperty and HierarchyStep
			for(Hierarchy hierarchy : dimension.getAllHierarchies().values()) {
				List<Level> levelsList = hierarchy.getLevelsList();
				for(int rollUpIt=0; rollUpIt<levelsList.size()-1; rollUpIt++) {
					// RollupProperty
					builder.add("schema:"+hierarchy.getRollupRelationshipByIndex(rollUpIt),RDF.TYPE,"qb4o:RollupProperty");
//					builder.add("schema:"+dimension.getDimensionName()+"Dimension-"+hierarchy.getHierarchyName()+"Hierarchy-Rollup"+rollUpIt,RDF.TYPE,"qb4o:RollupProperty");
					// HierarchyStep
					builder.subject("schema:"+dimension.getDimensionName()+"Dimension-"+hierarchy.getHierarchyName()+"Hierarchy-Step"+rollUpIt)
						.add(RDF.TYPE, "qb4o:HierarchyStep")
						.add("qb4o:inHierarchy", "schema:"+hierarchy.getHierarchyName()+"Hierarchy")
						.add("qb4o:pcCardinality", "qb4o:ManyToOne")
						.add("qb4o:rollup","schema:"+hierarchy.getRollupRelationshipByIndex(rollUpIt))
//					// childLevel and parentLevel
						.add("qb4o:childLevel", "schema:"+levelsList.get(rollUpIt).getLevelName())
						.add("qb4o:parentLevel", "schema:"+levelsList.get(rollUpIt+1).getLevelName());
				}
			}
		}
//		for(int i=0;i<dimensions.size();i++) {
////			System.out.println("Dimension = "+dimensions.get(i).dimensionName);
//			// DimensionProperty
//			builder.add("schema:"+dimensions.get(i).dimensionName+"Dimension",RDF.TYPE, "qb:DimensionProperty");
//			for(int j=0;j<dimensions.get(i).hierarchies.size();j++) {
//				// hasHierarchy
//				builder.add("schema:"+dimensions.get(i).dimensionName+"Dimension", "qb4o:hasHierarchy","schema:"+dimensions.get(i).hierarchies.get(j).hierarchyName+"Hierarchy");
//				// Hierarchy
//				builder.subject("schema:"+dimensions.get(i).hierarchies.get(j).hierarchyName+"Hierarchy")
//					.add(RDF.TYPE, "qb4o:Hierarchy")
//					// inDimension
//					.add("qb4o:inDimension", "schema:"+dimensions.get(i).dimensionName+"Dimension");
//				for(int k=0;k<dimensions.get(i).hierarchies.get(j).levels.size();k++) {
//					// hasLevel
//					builder.add("schema:"+dimensions.get(i).hierarchies.get(j).hierarchyName+"Hierarchy","qb4o:hasLevel","schema:"+dimensions.get(i).hierarchies.get(j).levels.get(k).levelName);
//					// LevelProperty
//					builder.add("schema:"+dimensions.get(i).hierarchies.get(j).levels.get(k).levelName,RDF.TYPE, "qb4o:LevelProperty");
//					for(Map.Entry<String, String> levelAttribute : dimensions.get(i).hierarchies.get(j).levels.get(k).levelAttributes.entrySet()) {
//						// hasAttribute
//						builder.add("schema:"+dimensions.get(i).hierarchies.get(j).levels.get(k).levelName, "qb4o:hasAttribute","property:"+levelAttribute.getKey()+"Property");
//						// LevelAttribute
//						builder.add("property:"+levelAttribute.getKey()+"Property",RDF.TYPE,"qb4o:LevelAttribute");
//						// rdfs:range
//						builder.add("property:"+levelAttribute.getKey()+"Property",RDFS.RANGE,"xsd:"+levelAttribute.getValue());
//					}
//				}
//				
//			}
//			
//			// RollupProperty and HierarchyStep
//			System.out.println(dimensions.get(i).dimensionName+" Dimension Hierarchies size = " + Integer.toString(dimensions.get(i).hierarchies.size()-1));
//			for(int j=0;j<dimensions.get(i).hierarchies.size();j++) {
////				System.out.println("j = " + j);
////				System.out.println("dimensions.get(i).hierarchies.size()-1 = " + Integer.toString(dimensions.get(i).hierarchies.size()-1));
//				for(int k=0;k<dimensions.get(i).hierarchies.get(j).levels.size()-1;k++) {
//					System.out.println("Dimension = "+dimensions.get(i).dimensionName);
//					System.out.println("Hierarchy = "+dimensions.get(i).hierarchies.get(j).hierarchyName);
//					System.out.println("Level = "+dimensions.get(i).hierarchies.get(j).levels.get(k).levelName);
//					System.out.println("k = " + k);
//					System.out.println("dimensions.get(i).hierarchies.get(j).levels.size()-1 = " + Integer.toString(dimensions.get(i).hierarchies.get(j).levels.size()-1));
//					// RollupProperty
//					builder.add("schema:"+dimensions.get(i).dimensionName+"Dimension-"+dimensions.get(i).hierarchies.get(j).hierarchyName+"Hierarchy-Rollup"+j,RDF.TYPE,"qb4o:RollupProperty");
//					// HierarchyStep
//					builder.subject("schema:"+dimensions.get(i).dimensionName+"Dimension-"+dimensions.get(i).hierarchies.get(j).hierarchyName+"Hierarchy-Step"+j)
//						.add(RDF.TYPE, "qb4o:HierarchyStep")
//						.add("qb4o:inHierarchy", "schema:"+dimensions.get(i).hierarchies.get(j).hierarchyName+"Hierarchy")
//						.add("qb4o:pcCardinality", "qb4o:ManyToOne")
//						.add("qb4o:rollup","schema:"+dimensions.get(i).dimensionName+"Dimension-"+dimensions.get(i).hierarchies.get(j).hierarchyName+"Hierarchy-Rollup"+j)
//					// childLevel and parentLevel
//						.add("qb4o:childLevel", "schema:"+dimensions.get(i).hierarchies.get(j).levels.get(k).levelName)
//						.add("qb4o:parentLevel", "schema:"+dimensions.get(i).hierarchies.get(j).levels.get(k+1).levelName);
//				}
//			}
//		}
//		
		// Writing to the file
		org.eclipse.rdf4j.model.Model model = builder.build();
		try {
			Rio.write(model, out, RDFFormat.TURTLE);
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
