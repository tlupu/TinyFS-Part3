package com.master;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.client.FileHandle;
import com.chunkserver.ChunkServer;
import com.client.ClientFS.FSReturnVals;

import javafx.util.Pair;

public class Master {
	// testing new branch!
	
	
	/* HashMap. <absolute_path, list<file object>> 
	  ex: <”/usr”, [“bin”, “local”, “lib”]> */
	Map<String, List<File>>  FileNamespace;
	
	/* <file-handle, a list of chunk handles and their respective address of chunk servers> 
	 to store the returned file handle */
	Map<String, List<Pair<String, String>>> ChunkNamespace;
	
	public Master() {
		
		/* master needs to process requests from client */
		while (true) {
			String request;
			// read request from input stream
			request = is.readUTF();
			
			if (request == "CreateFile") {
				String tatdir = is.readUTF();
				String filename = is.readUTF();
				ProcessCreateFile(tatdir, filename);
			}
			else if (request == "OpenFile") {
				String filepath = is.readUTF();
				FileHandle filehandle = "";
				ProcessOpenFile(filepath, filehandle);
			}
		}
		
	}
	
	public void ProcessCreateFile(String tgtdir, String filename) {
		/* Master invokes createChunk on the chunkserver */
		ChunkServer chunkserver = new ChunkServer();
		String chunkhandle = chunkserver.createChunk();
		
		/* Master updates its file namespace */
		
		/* Master updates its chunk namespace */
		List<Pair<String, String>> chunklist = ChunkNamespace.get(filename);
		if (chunklist != null) {
		    // there is already a list of chunks for this file name, so add to the list
			ChunkNamespace.get(filename).add(new Pair<String, String>(chunkhandle, "server_address"));
		} else {
			// the key does not exist, or the key does exist and the value is null
			List<Pair<String, String>> temp = new ArrayList<Pair<String, String>>();
	    		temp.add(new Pair<String, String>(chunkhandle, "server_address"));
	    		ChunkNamespace.put(filename, temp);
		}
		
		/* Master returns the createChunk response from the chunkserver to the client. For now,
		  the master creates the chunk handle and sends it to the chunk server */ 
		os.writeUTF(chunkhandle);
		
	}
	
	public void ProcessOpenFile(String FilePath, FileHandle ofh) {
		/* Master receives openFile and returns a file handle to the client.*/
	
		
	}
	
	public void ProcessDeleteFile(String tgtdir, String filename) {
		/* Rename the file to a hidden file only known to the master */
		/* Garbage collect orphaned chunks. */

	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
