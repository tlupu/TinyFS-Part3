package com.master;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.client.FileHandle;
import com.client.ClientFS.FSReturnVals;

public class Master {

	Map<String, List<File>>  FileNamespace;
	
	public String ProcessCreateFile(String tgtdir, String filename) {
		/* Master updates its file namespace and chunk namespace */
		
		/* Master returns the createChunk response from the chunkserver to the client. For now,
		  the master creates the chunk handle and sends it to the chunk server */ 
		
		return null;
	}
	
	public String ProcessOpenFile(String FilePath, FileHandle ofh) {
		/* Master receives openFile and returns a file handle to the client.*/
	
		
		return null;
	}
	
	public void ProcessDeleteFile(String tgtdir, String filename) {
		/* Rename the file to a hidden file only known to the master */
		/* Garbage collect orphaned chunks. */

	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
