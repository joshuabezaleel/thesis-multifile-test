package main;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;

import org.teiid.adminapi.Admin;
import org.teiid.adminapi.Model;
import org.teiid.adminapi.VDB;
import org.teiid.adminapi.VDB.ConnectionType;
import org.teiid.adminapi.impl.ModelMetaData;
import org.teiid.adminapi.impl.VDBMetaData;
import org.teiid.adminapi.impl.VDBMetadataParser;
import org.teiid.adminapi.jboss.AdminFactory;
import org.teiid.jdbc.TeiidDriver;

import main.AdminUtil;
import olapcube.Dimension;
import olapcube.Hierarchy;
import olapcube.Level;
import olapcube.Measure;
import olapcube.OLAPCube;

public class Main {
	private static Admin admin;
	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {
		
		// Variables definition
		String folderPath = "";
		Scanner reader = new Scanner(System.in);
		Util util = new Util();
		OLAPCube cube = new OLAPCube(null);
		DataSourceDataTypeFiles dataSourceDataTypeFiles = new DataSourceDataTypeFiles();
		
		// Handler initialization
		DataSourceHandler dataSourceHandler = new DataSourceHandler();
		OLAPCubeHandler olapCubeHandler = new OLAPCubeHandler();
		CubeSchemaMaker cubeSchemaMaker = new CubeSchemaMaker();
		OBDAMaker obdaMaker = new OBDAMaker();
		PropertyMaker propertyMaker = new PropertyMaker();
		VDBHandler vdbHandler = new VDBHandler();
		
		// Starting the teiid Admin
		admin = AdminFactory.getInstance().createAdmin("localhost", AdminUtil.MANAGEMENT_PORT , "admin", "admin".toCharArray());
//		
//		// Deleting all the previous data sources and deployed VDB
		Collection<String> dataSourceNames = new ArrayList<String>();
		dataSourceNames = admin.getDataSourceNames();
		for(String dataSourceName : dataSourceNames) {
			if(!dataSourceName.equals("teiid-olingo-odata4.DefaultDataSource")) {
				admin.deleteDataSource(dataSourceName);
			}
		}
//		
		List<String> deployments = admin.getDeployments();
		for(String deployment : deployments) {
			if(!deployment.equals("teiid-olingo-odata4.war")) {
				admin.undeploy(deployment);
			}
			System.out.println(deployment);
		}
//		
		admin.restart();
		
		/*
		 *  Input folder containing CSV files
		 */
		System.out.print("Is this the new cube and data source files that you want to make? (YES/NO): ");
		reader = new Scanner(System.in);
		String userInput = reader.nextLine();
		
		if(userInput.equals("NO")) {
			// Means that they already had defined cube and its data source files
			System.out.print("Input the pat to the folder containig the predefined JSON file for cube and datasources: ");
			reader = new Scanner(System.in);
			folderPath = reader.nextLine();
			
			dataSourceDataTypeFiles = dataSourceHandler.decodeFromJSON(folderPath);
			dataSourceHandler.printAllDataSource(dataSourceDataTypeFiles);
			
			cube = olapCubeHandler.decodeFromJSON(folderPath);
			cube.printCube();
		} else {
			System.out.print("Input the path to the folder containing the CSVs: ");
			reader = new Scanner(System.in);
			folderPath = reader.nextLine();
			
			File file = new File(folderPath);
			File[] csvs = file.listFiles(new FilenameFilter() {
				
				public boolean accept(File dir, String name) {
	                if(name.toLowerCase().endsWith(".csv")){
	                    return true;
	                } else {
	                    return false;
	                }
	            }
			});
			
			/*
			 * Defining data sources, its columns and datatypes for further usage
			 */
			dataSourceDataTypeFiles = dataSourceHandler.make(folderPath,csvs);
			dataSourceHandler.encodeToJSON(dataSourceDataTypeFiles,folderPath);
			dataSourceHandler.printAllDataSource(dataSourceDataTypeFiles);
			
			/*
			 * Creating OLAP Cube Schema
			 */
			System.out.println("=== OLAP CUBE DEFINITION ===");
			cube = olapCubeHandler.make(dataSourceDataTypeFiles);
			olapCubeHandler.encodeToJSON(cube,folderPath);
			cube.printCube();
			
		}
		
		/*
		 *  Creating cube schema (.ttl file) from the OLAPCube datatype for 
		 *  the backbone of the cube structure
		 */
		cubeSchemaMaker.make(cube, folderPath);
		
		/*
		 *  Creating OBDA (.obda) file (mapping from VDB to QB4OLAP ontology concept)
		 */
		obdaMaker.make(cube,dataSourceDataTypeFiles, folderPath);
		
		/*
		 *  Creating property file that describe the connection for Ontop at the Docker to Teiid via JDBC
		 */
		propertyMaker.make(cube,folderPath);

		/*
		 * VDBHandler handle the (1) creating the VDB from all of the CSVs for virtualization 
		 * and (2) creating connection and deploying the VDB to Teiid Server
		 */
		vdbHandler.makeAndDeploy(cube,dataSourceDataTypeFiles,folderPath, admin);
		
		
		// Connection Test
//		Connection conn = TeiidDriver.getInstance().connect("jdbc:teiid:sales.1@mm://localhost:31000;user=usernew;password=user1664!", null);
//		System.out.println("status="+conn.isValid(0));
//		Statement stmt = conn.createStatement();
//		ResultSet resultSet = stmt.executeQuery("select * from sales");
//		ResultSetMetaData rsmd = resultSet.getMetaData();
//		int columnsNumber = rsmd.getColumnCount();
//		for (int i=1; i<=columnsNumber; i++) {
//			System.out.print(rsmd.getColumnName(i) + " ");
//		}
//		System.out.println("");
//		while(resultSet.next()) {
//			for (int i=1; i<=columnsNumber; i++) {
//				if (i>1) System.out.print(",");
//							System.out.print(resultSet.getString(i));
//					System.out.print(resultSet.getObject(i));
//			}
//			System.out.println("");
//		}
	}
	
	
}

