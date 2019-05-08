package main;

import java.io.FileWriter;
import java.io.IOException;

import olapcube.OLAPCube;

public class PropertyMaker {
	public void make(OLAPCube cube, String folderPath) throws IOException {
		FileWriter fw2 = new FileWriter(folderPath+"/"+cube.getCubeName()+".docker.properties");
		fw2.write("jdbc.url=jdbc\\:teiid\\:"+cube.getCubeName()+".1@mm\\://172.17.0.1\\:31000\n");
		fw2.write("jdbc.driver=org.teiid.jdbc.TeiidDriver\n");
		fw2.write("jdbc.user=usernew\n");
		fw2.write("jdbc.name=\n");
		fw2.write("jdbc.password=user1664!");
		fw2.close();
	}
}
