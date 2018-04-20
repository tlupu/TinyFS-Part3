package com.master;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.client.ClientFS;
import com.client.FileHandle;
import com.chunkserver.ChunkServer;
import com.client.ClientFS.FSReturnVals;

import javafx.util.Pair;

public class Master {
	
	ServerSocket ServerSocket;
	Socket ClientSocket;
	public ObjectOutputStream WriteOutput;
	public ObjectInputStream ReadInput;
	String hostname = "localhost";

	/* HashMap. <absolute_path, list<file object>> */
	Map<String, List<File>>  FileNamespace;
	public static ArrayList<String> directories;
	
	/* <file-handle, a list of chunk handles and their respective address of chunk servers> 
	 to store the returned file handle */
	Map<String, List<Pair<String, String>>> ChunkNamespace;
	
	public Master() {
		
		ClientSocket = null;
		
		try {
			ServerSocket = new ServerSocket(9000);
			System.out.println("Initialized server socket");
		} catch (IOException e) {
			System.out.println("Could not get I/O for the connection to: " + hostname);
			e.printStackTrace();
		}
		
//		try {
//			ClientSocket = serverSocket.accept();
//			System.out.println("Accepted server socket");
//			WriteOutput = new ObjectOutputStream(ClientSocket.getOutputStream());
//			ReadInput = new ObjectInputStream(ClientSocket.getInputStream());
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
		/* master needs to process requests from client */
		while (true) {
			
			try {
				ClientSocket = ServerSocket.accept();
				System.out.println("Accepted server socket");
				WriteOutput = new ObjectOutputStream(ClientSocket.getOutputStream());
				ReadInput = new ObjectInputStream(ClientSocket.getInputStream());
				System.out.println("initialized output and input streams in master");
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			while (!ClientSocket.isClosed()) {
				
				try {
					char request = 'a';
					System.out.println("trying to read the char...");
					request = ReadInput.readChar();
					System.out.println("this is the request recieved in master: " + request);

					if (request == 'C') {
						// CreateDir(String src, String dirname)
						System.out.println("read CreateDir request");
						String src = ReadInput.readUTF();
						String dirname = ReadInput.readUTF();
						CreateDir(src, dirname);
					}
//					else if (request == "DeleteDir") {
//						// DeleteDir(String src, String dirname)
//						String src = ReadInput.readUTF();
//						String dirname = ReadInput.readUTF();
//						DeleteDir(src, dirname);
//					}
//					else if (request == "RenameDir") {
//						// RenameDir(String src, String NewName)
//						String src = ReadInput.readUTF();
//						String NewName = ReadInput.readUTF();
//						RenameDir(src, NewName);
//					}
					else if (request == 'L') {
						// ListDir(String tgt)
						System.out.println("read request to list dir");
						String tgt = ReadInput.readUTF();
						ListDir(tgt);
					}
//					else if (request == "CreateFile") {
//						String tatdir = ReadInput.readUTF();
//						String filename = ReadInput.readUTF();
//						CreateFile(tatdir, filename);
//					}
//					else if (request == "DeleteFile") {
//						// DeleteFile(String tgtdir, String filename)
//						String tatdir = ReadInput.readUTF();
//						String filename = ReadInput.readUTF();
//						DeleteFile(tatdir, filename);
//					}
//					else if (request == "OpenFile") {
//						String filepath = ReadInput.readUTF();
//						// figure out how to read object
//						FileHandle filehandle = ReadInput.readObject();
//						OpenFile(filepath, filehandle);
//					}
				} catch (IOException e) {
					System.out.println("caught exception");
					break;
				}
			}
		}
	}
	
	public void CreateDir(String tgtdir, String filename) {
		try {
			File newDir;
			//if creating the root directory
			if (tgtdir.equals("/")) {
				newDir = new File(filename);
				if (newDir.exists()) {
					WriteOutput.writeUnshared(FSReturnVals.DestDirExists);
					System.out.println("wrote dest dir exists to client from master");
//					return FSReturnVals.DestDirExists;
				}
				newDir.mkdir();
				WriteOutput.writeUnshared(FSReturnVals.Success);
				System.out.println("wrote success to client from master");
//				return FSReturnVals.Success;
			}
			//if creating a sub-directory
			else {
				File parentDir = new File(tgtdir.substring(1));
				if (!parentDir.exists()) {
					WriteOutput.writeUnshared(FSReturnVals.SrcDirNotExistent);
					System.out.println("wrote SrcDirNotExistent to client from master");
//					return FSReturnVals.SrcDirNotExistent;
				}
				else {
					newDir = new File(parentDir, filename);
					if (newDir.exists()) {
						WriteOutput.writeUnshared(FSReturnVals.DestDirExists);
						System.out.println("wrote DestDirExists to client from master");
//						return FSReturnVals.DestDirExists;
					}
					newDir.mkdirs();
					WriteOutput.writeUnshared(FSReturnVals.Success);
					System.out.println("wrote Success to client from master");
//					return FSReturnVals.Success;	
				}
			}
			
			WriteOutput.flush();
		} catch (IOException e) {
			System.out.println("could not create dir in master");
			e.printStackTrace();
		} 
		
//		/* Master invokes createChunk on the chunkserver */
//		ChunkServer chunkserver = new ChunkServer();
//		String chunkhandle = chunkserver.createChunk();
//		
//		/* Master updates its file namespace */
//		
//		/* Master updates its chunk namespace */
//		List<Pair<String, String>> chunklist = ChunkNamespace.get(filename);
//		if (chunklist != null) {
//		    // there is already a list of chunks for this file name, so add to the list
//			ChunkNamespace.get(filename).add(new Pair<String, String>(chunkhandle, "server_address"));
//		} else {
//			// the key does not exist, or the key does exist and the value is null
//			List<Pair<String, String>> temp = new ArrayList<Pair<String, String>>();
//	    		temp.add(new Pair<String, String>(chunkhandle, "server_address"));
//	    		ChunkNamespace.put(filename, temp);
//		}
//		
//		/* Master returns the createChunk response from the chunkserver to the client. For now,
//		  the master creates the chunk handle and sends it to the chunk server */ 
//		os.writeUTF(chunkhandle);
		
	}
	
	public FSReturnVals DeleteDir(String tgtdir, String filename) {
		/* Rename the file to a hidden file only known to the master */
		/* Garbage collect orphaned chunks. */

		File directory;
		if (tgtdir.equals("/")) {
			directory = new File(filename);
		}
		else {
			File parentDir = new File(tgtdir.substring(1));
			directory = new File(parentDir, filename);			
		}
		//make sure directory exists
	    	if(!directory.exists()){
	    		System.out.println("Does not exists: " + directory.getPath());
	    		return FSReturnVals.SrcDirNotExistent;
	    }
	    	else{   
	    		if(directory.list().length != 0) {
	    	        return FSReturnVals.DirNotEmpty;
	    		}     
	    		else {
	    			directory.delete();
	    	        return FSReturnVals.DestDirExists;
	    		}
		}

	}
	
	public FSReturnVals RenameDir(String src, String NewName) {
		File dir = new File(src.substring(1));
	    if (!dir.isDirectory()) {
	    		return FSReturnVals.SrcDirNotExistent;
	    }
	    File newDir = new File(NewName.substring(1));
	    if (newDir.exists()) {
	    		System.out.println("new dir already exists");
	    		return FSReturnVals.DestDirExists;
	    }
	    dir.renameTo(newDir);
		return FSReturnVals.Success;
	}
	
	
	public void ListDir(String tgt) {
		
		try {
			Master.directories = new ArrayList<String> ();
			listf(tgt.substring(1));
			System.out.println(Master.directories.size());
			String [] directories = Master.directories.toArray(new String[Master.directories.size()]);
			// first write the size of the array
			WriteOutput.writeInt(directories.length);
			// then write the array
			for (int i = 0; i < directories.length; i++) {
				WriteOutput.writeUTF(directories[i]);
			}
			
			System.out.println("wrote array of strings to client from master");
			
			WriteOutput.flush();
			
		} catch (IOException e) {
			System.out.println("could not list dir in master");
			e.printStackTrace();
		} 
	}
	
	public void listf(String directoryName) {
	    File directory = new File(directoryName);
	    // get all the files from a directory
	    File[] fList = directory.listFiles();
	    for (File file : fList) {
			String filePath = "/" + file.getPath();
	        Master.directories.add(filePath);
	        if (file.isDirectory()) {
	        		listf(file.getPath());
	        }
	    }
	}
	
	public FSReturnVals CreateFile(String tgtdir, String filename) {
		File directory = new File(tgtdir.substring(1));
		if (!directory.exists()) {
			return FSReturnVals.SrcDirNotExistent;
		}
		String fName = tgtdir.substring(1) + filename;
		File file = new File(fName);
		if (file.exists()) {
			return FSReturnVals.FileExists;
		}
		else {
			try {
				OutputStream out = new FileOutputStream(file);
				out.close();	
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return FSReturnVals.Success;
		}
	}
	
	public FSReturnVals DeleteFile(String tgtdir, String filename) {
		File directory = new File(tgtdir.substring(1));
		if (!directory.exists()) {
			return FSReturnVals.SrcDirNotExistent;
		}
		String fName = tgtdir.substring(1) + filename;
		File file = new File(fName);
		if (!file.exists()) {
			return FSReturnVals.FileDoesNotExist;
		}
		file.delete();
		return FSReturnVals.Success;		
	}
	
	public FSReturnVals OpenFile(String FilePath, FileHandle ofh) {
		return null;
	}
	
	public FSReturnVals CloseFile(FileHandle ofh) {
		return null;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Master master = new Master();
	}

}