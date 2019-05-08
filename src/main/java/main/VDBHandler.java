package main;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.xml.stream.XMLStreamException;

import org.teiid.adminapi.Admin;
import org.teiid.adminapi.AdminException;
import org.teiid.adminapi.Model;
import org.teiid.adminapi.VDB;
import org.teiid.adminapi.VDB.ConnectionType;
import org.teiid.adminapi.impl.ModelMetaData;
import org.teiid.adminapi.impl.VDBMetaData;
import org.teiid.adminapi.impl.VDBMetadataParser;
import org.teiid.adminapi.jboss.AdminFactory;
import org.teiid.jdbc.TeiidDriver;

import olapcube.OLAPCube;

@SuppressWarnings("restriction")
public class VDBHandler {
	private static Admin admin;
	
	public void makeAndDeploy(OLAPCube cube, DataSourceDataTypeFiles dataSourceDataTypeFiles, String folderPath) throws IOException, AdminException, SQLException {
		// VDB Initialization
		VDBMetaData vdbMD = new VDBMetaData();
		vdbMD.setName(cube.getCubeName());
		vdbMD.setDescription(cube.getCubeName()+" vdb description");
		vdbMD.setVersion(1);
		vdbMD.setConnectionType(ConnectionType.BY_VERSION);
		
		// The first model is the physical, for the connection to the physical CSV file
		ModelMetaData physicalModel = new ModelMetaData();
		physicalModel.setName("physicalModel");
		physicalModel.setModelType(Model.Type.PHYSICAL);
		// Setting the properties of the physical model
		physicalModel.addSourceMapping("text-connector", "file", "java:/textconnector-file");
		
		Map<String, ModelMetaData> virtualModels = new HashMap<String, ModelMetaData>();
		// Iterate for every file, make each of its virtual model
		for(String fileName :  dataSourceDataTypeFiles.getAllDataSourceDataTypes().keySet()) {
			System.out.println("file = "+fileName);
			ModelMetaData virtualModel = new ModelMetaData();
			virtualModel.setName(fileName+"VirtualModel");
			virtualModel.setModelType(Model.Type.VIRTUAL);
			String teiidDDLView = "CREATE VIEW "+fileName+" (\n";
			DataSourceDataType last = dataSourceDataTypeFiles.getDataSourceDataTypes(fileName).get(dataSourceDataTypeFiles.getDataSourceDataTypes(fileName).size()-1);
			
			for(DataSourceDataType dataSourceDataType : dataSourceDataTypeFiles.getDataSourceDataTypes(fileName)) {
				if(dataSourceDataType.getDataType().equals("decimal")) {
					teiidDDLView = teiidDDLView.concat("            "+dataSourceDataType.getColumnName()+" big"+dataSourceDataType.getDataType());
				} else {
					teiidDDLView = teiidDDLView.concat("            "+dataSourceDataType.getColumnName()+" "+dataSourceDataType.getDataType());
				}
				if(dataSourceDataType!=last) {
					teiidDDLView = teiidDDLView.concat(",");
				}
				teiidDDLView = teiidDDLView.concat("\n");
			}
			teiidDDLView = teiidDDLView.concat("        ) AS  \n");
			teiidDDLView = teiidDDLView.concat("          SELECT ");
			
			for(DataSourceDataType dataSourceDataType : dataSourceDataTypeFiles.getDataSourceDataTypes(fileName)) {
				if(dataSourceDataType!=last) {
					teiidDDLView = teiidDDLView.concat("file."+dataSourceDataType.getColumnName()+", ");
				} else {
					teiidDDLView = teiidDDLView.concat("file."+dataSourceDataType.getColumnName()+"\n");
				}
			}
			teiidDDLView = teiidDDLView.concat("            FROM (EXEC physicalModel.getTextFiles('"+fileName+".csv')) AS f, \n");
			teiidDDLView = teiidDDLView.concat("            TEXTTABLE(f.file COLUMNS ");
			
			for(DataSourceDataType dataSourceDataType : dataSourceDataTypeFiles.getDataSourceDataTypes(fileName)) {
				if(dataSourceDataType!=last) {
					if(dataSourceDataType.getDataType().equals("decimal")) {
						teiidDDLView = teiidDDLView.concat(dataSourceDataType.getColumnName()+" big"+dataSourceDataType.getDataType()+", ");
					} else {
						teiidDDLView = teiidDDLView.concat(dataSourceDataType.getColumnName()+" "+dataSourceDataType.getDataType()+", ");
					}
				} else {
					if(dataSourceDataType.getDataType().equals("decimal")) {
						teiidDDLView = teiidDDLView.concat(dataSourceDataType.getColumnName()+" big"+dataSourceDataType.getDataType()+" ");
					} else {
						teiidDDLView = teiidDDLView.concat(dataSourceDataType.getColumnName()+" "+dataSourceDataType.getDataType()+" ");
					}
				}
			}
			teiidDDLView = teiidDDLView.concat("HEADER) AS file;");
			virtualModel.addSourceMetadata("DDL",teiidDDLView);
			virtualModels.put(fileName, virtualModel);
		}
		
		/*
		 * Decimal datatypes should be converted to bigdecimal for Teiid RDB compatibility
		 */
		// Adding new HashMap, changing from decimal to bigdecimal for Teiid RDB compatibility
//		Map<String,String> teiidDataTypes = new HashMap<String,String>();
//		for(Map.Entry<String, String> columnDataType : columnDataTypes.entrySet()) {
//			if(columnDataType.getValue().equals("decimal")){
//				teiidDataTypes.put(columnDataType.getKey(), "bigdecimal");
//			}else {
//				teiidDataTypes.put(columnDataType.getKey(), "string");
//			}
//		}
		
		// DELETE CANDIDATE
		// The second model is the virtual one, for creating the virtual relational database from the CSV file
//		ModelMetaData virtualModel = new ModelMetaData();
//		virtualModel.setName("virtualModel");
//		virtualModel.setModelType(Model.Type.VIRTUAL);
//		System.out.println("testing");
//		for(Map.Entry<String, String> columnDataType : columnDataTypes.entrySet()) {
//			System.out.println(columnDataType.getKey()+" "+columnDataType.getValue());
//		}
//		for(Map.Entry<String, String> teiidDataType : teiidDataTypes.entrySet()) {
//			System.out.println(teiidDataType.getKey()+" "+teiidDataType.getValue());
//		}
//		String teiidDDLView = "CREATE VIEW "+fileName+" (\n";
//		int tempIterator = 0;
//		for(Map.Entry<String, String> teiidDataType : teiidDataTypes.entrySet()) {
//			System.out.println("tempIterator="+tempIterator);
//			System.out.println("size="+Integer.toString(teiidDataTypes.size()-1));
//			teiidDDLView = teiidDDLView.concat("            "+teiidDataType.getKey()+" "+teiidDataType.getValue());
//			if(tempIterator!=teiidDataTypes.size()-1) {
//				teiidDDLView = teiidDDLView.concat(",");
//			}
//			teiidDDLView = teiidDDLView.concat("\n");
//			tempIterator++;
//		}
//		teiidDDLView = teiidDDLView.concat("        ) AS  \n");
//		teiidDDLView = teiidDDLView.concat("          SELECT ");
//		tempIterator = 0;
//		for(Map.Entry<String, String> teiidDataType : teiidDataTypes.entrySet()) {
//			if(tempIterator!=teiidDataTypes.size()-1) {
//				teiidDDLView = teiidDDLView.concat("file."+teiidDataType.getKey()+", ");
//			}else {
//				teiidDDLView = teiidDDLView.concat("file."+teiidDataType.getKey()+"\n");
//			}
//			tempIterator++;
//		}
//		teiidDDLView = teiidDDLView.concat("            FROM (EXEC physicalModel.getTextFiles('"+fileName+".csv')) AS f, \n");
//		teiidDDLView = teiidDDLView.concat("            TEXTTABLE(f.file COLUMNS ");
//		tempIterator=0;
//		for(Map.Entry<String, String> teiidDataType : teiidDataTypes.entrySet()) {
//			if(tempIterator!=teiidDataTypes.size()-1) {
//				teiidDDLView = teiidDDLView.concat(teiidDataType.getKey()+" "+teiidDataType.getValue()+", ");
//			}else if(tempIterator==teiidDataTypes.size()-1){
//				teiidDDLView = teiidDDLView.concat(teiidDataType.getKey()+" "+teiidDataType.getValue()+" ");
//			}
//			tempIterator++;
//		}
//		teiidDDLView = teiidDDLView.concat("HEADER) AS file;");
//		virtualModel.addSourceMetadata("DDL",teiidDDLView);
//		
//		System.out.println(teiidDDLView);
		
		// Adding the physical and virtualModels
		vdbMD.addModel(physicalModel);
		for (ModelMetaData virtualModel : virtualModels.values()) {
			vdbMD.addModel(virtualModel);
		}
		
		// Marshalling
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		try {
			VDBMetadataParser.marshell(vdbMD, outStream);
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Properties connectionProps = new Properties();
		// (!!!) pathnya diambil dari filepath
		connectionProps.setProperty("ParentDirectory", folderPath);
		System.out.println(folderPath);
		connectionProps.setProperty("AllowParentPaths", "true");
		connectionProps.setProperty("class-name", "org.teiid.resource.adapter.file.FileManagedConnectionFactory");
		
		admin = AdminFactory.getInstance().createAdmin("localhost", AdminUtil.MANAGEMENT_PORT , "admin", "admin".toCharArray());
		AdminUtil.createDataSource(admin, "textconnector-file", "file", connectionProps);
		// nama xmlnya belum diambil dari filename
		admin.deploy(cube.getCubeName()+"-vdb.xml", new ByteArrayInputStream(outStream.toByteArray()));
		
		// VDB Status Test
		System.out.println("asdlkjalsdkasld");
		VDB vdbTest = admin.getVDB(cube.getCubeName(), "1");
		System.out.println("name = "+cube.getCubeName());
		System.out.println(vdbTest.getStatus());
		System.out.println("asdlkjalsdkasld");
		
		// Connection Test
		Connection conn = TeiidDriver.getInstance().connect("jdbc:teiid:"+cube.getCubeName()+".1@mm://localhost:31000;user=usernew;password=user1664!", null);
		System.out.println("status="+conn.isValid(0));
		Statement stmt = conn.createStatement();
		ResultSet resultSet = stmt.executeQuery("select * from sales");
		ResultSetMetaData rsmd = resultSet.getMetaData();
		int columnsNumber = rsmd.getColumnCount();
		for (int i=1; i<=columnsNumber; i++) {
			System.out.print(rsmd.getColumnName(i) + " ");
		}
		System.out.println("");
		while(resultSet.next()) {
			for (int i=1; i<=columnsNumber; i++) {
				if (i>1) System.out.print(",");
							System.out.print(resultSet.getString(i));
					System.out.print(resultSet.getObject(i));
			}
			System.out.println("");
		}
	}
}
