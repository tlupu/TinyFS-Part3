package com.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import com.chunkserver.ChunkServer;
import com.master.Master;

public class ClientFS {
	public static ArrayList<String> directories;
//	Master master = new Master();
	
	public static ChunkServer cs = null;
	public Socket ClientSocket;
	public ObjectOutputStream WriteOutput;
	public ObjectInputStream ReadInput;
	String hostname = "localhost";
	
	public ClientFS() {
		try {
			ClientSocket = new Socket(hostname, 9000);
			System.out.println("initialized socket");
			
			WriteOutput = new ObjectOutputStream(ClientSocket.getOutputStream());
			ReadInput = new ObjectInputStream(ClientSocket.getInputStream());
			System.out.println("initialized WriteOutput and ReadInput");
			
		} catch (UnknownHostException e) {
			System.out.println("Couldn't find host: " + hostname);
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Could not get I/O for the connection to: " + hostname);
			e.printStackTrace();
		}
	}
	
	public enum FSReturnVals {
		DirExists, // Returned by CreateDir when directory exists
		DirNotEmpty, //Returned when a non-empty directory is deleted
		SrcDirNotExistent, // Returned when source directory does not exist
		DestDirExists, // Returned when a destination directory exists
		FileExists, // Returned when a file exists
		FileDoesNotExist, // Returns when a file does not exist
		BadHandle, // Returned when the handle for an open file is not valid
		RecordTooLong, // Returned when a record size is larger than chunk size
		BadRecID, // The specified RID is not valid, used by DeleteRecord
		RecDoesNotExist, // The specified record does not exist, used by DeleteRecord
		NotImplemented, // Specific to CSCI 485 and its unit tests
		Success, //Returned when a method succeeds
		Fail //Returned when a method fails
	}

	/**
	 * Creates the specified dirname in the src directory Returns
	 * SrcDirNotExistent if the src directory does not exist Returns
	 * DestDirExists if the specified dirname exists
	 *
	 * Example usage: CreateDir("/", "Shahram"), CreateDir("/Shahram/",
	 * "CSCI485"), CreateDir("/Shahram/CSCI485/", "Lecture1")
	 */
	public FSReturnVals CreateDir(String src, String dirname) {
		//ASK: every time a test is run, do we need to delete the directories that already exist
		//because if not, the test will fail if DestDirExists is returned
		
		// return master.CreateDir(src, dirname);
		
		try {
			if (ClientSocket != null && WriteOutput != null && ReadInput != null) {
				// send the request to create a directory
				WriteOutput.writeChar('C');
				WriteOutput.writeUTF(src);
				WriteOutput.writeUTF(dirname);
				WriteOutput.flush();
				System.out.println("sent data to master in createdir");
			}
			
			// look into how to read an enum
			FSReturnVals response = (FSReturnVals) ReadInput.readUnshared();
			System.out.println("recieved response from master in createdir");
			return response;
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return FSReturnVals.Fail;
	}

	/**
	 * Deletes the specified dirname in the src directory Returns
	 * SrcDirNotExistent if the src directory does not exist Returns
	 * DestDirExists if the specified dirname exists
	 *
	 * Example usage: DeleteDir("/Shahram/CSCI485/", "Lecture1")
	 */
	public FSReturnVals DeleteDir(String src, String dirname) {
		//ASK: what case would you return DestDirExists within this
		//shouldnt we return success
		
		// return master.DeleteDir(src, dirname);
		
		try {
			if (ClientSocket != null && WriteOutput != null && ReadInput != null) {
				// send the request to create a directory
				WriteOutput.writeChar('D');
				WriteOutput.writeUTF(src);
				WriteOutput.writeUTF(dirname);
				WriteOutput.flush();
			}
			
			FSReturnVals response = (FSReturnVals) ReadInput.readUnshared();
			System.out.println("recieved response from master in deletedir");
			return response;
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return FSReturnVals.Fail;
	}

	/**
	 * Renames the specified src directory in the specified path to NewName
	 * Returns SrcDirNotExistent if the src directory does not exist Returns
	 * DestDirExists if a directory with NewName exists in the specified path
	 *
	 * Example usage: RenameDir("/Shahram/CSCI485", "/Shahram/CSCI550") changes
	 * "/Shahram/CSCI485" to "/Shahram/CSCI550"
	 */
	public FSReturnVals RenameDir(String src, String NewName) {
		
		// return master.RenameDir(src, NewName);
		
		try {
			if (ClientSocket != null && WriteOutput != null && ReadInput != null) {
				// send the request to create a directory
				WriteOutput.writeChar('R');
				WriteOutput.writeUTF(src);
				WriteOutput.writeUTF(NewName);
				
				WriteOutput.flush();
			}
			
			FSReturnVals response = (FSReturnVals) ReadInput.readUnshared();
			System.out.println("recieved response from master in renamedir");
			return response;
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return FSReturnVals.Fail;
	}

	/**
	 * Lists the content of the target directory Returns SrcDirNotExistent if
	 * the target directory does not exist Returns null if the target directory
	 * is empty
	 *
	 * Example usage: ListDir("/Shahram/CSCI485")
	 */
	public String[] ListDir(String tgt) {
		
		// return master.ListDir(tgt);
		
		try {
			if (ClientSocket != null && WriteOutput != null && ReadInput != null) {
				// send the request to create a directory
				WriteOutput.writeChar('L');
				WriteOutput.writeUTF(tgt);
				WriteOutput.flush();
				System.out.println("sent data to master in listdir");
			}
			
			// look into how to read a string array
			int length = ReadInput.readInt();
			String data[] = new String[length];
			System.out.println("this is the length read in the client: " + length);
			for (int i = 0; i < length; i++) {
				data[i] = ReadInput.readUTF();
			}
			
			System.out.println("recieved response from master in createdir");
			return data;
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}


	/**
	 * Creates the specified filename in the target directory Returns
	 * SrcDirNotExistent if the target directory does not exist Returns
	 * FileExists if the specified filename exists in the specified directory
	 *
	 * Example usage: Createfile("/Shahram/CSCI485/Lecture1/", "Intro.pptx")
	 */
	public FSReturnVals CreateFile(String tgtdir, String filename) {
		
		// return master.CreateFile(tgtdir, filename);
		
		try {
			if (ClientSocket != null && WriteOutput != null && ReadInput != null) {
				// send the request to create a directory
				WriteOutput.writeChar('c');
				WriteOutput.writeUTF(tgtdir);
				WriteOutput.writeUTF(filename);
				
				WriteOutput.flush();
			}
		
			FSReturnVals response = (FSReturnVals) ReadInput.readUnshared();
			System.out.println("recieved response from master in createfile");
			return response;
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return FSReturnVals.Fail;
	}

	/**
	 * Deletes the specified filename from the tgtdir Returns SrcDirNotExistent
	 * if the target directory does not exist Returns FileDoesNotExist if the
	 * specified filename is not-existent
	 *
	 * Example usage: DeleteFile("/Shahram/CSCI485/Lecture1/", "Intro.pptx")
	 */
	public FSReturnVals DeleteFile(String tgtdir, String filename) {
		
		// return master.DeleteFile(tgtdir, filename);
		
		try {
			if (ClientSocket != null && WriteOutput != null && ReadInput != null) {
				// send the request to create a directory
				WriteOutput.writeChar('d');
				WriteOutput.writeUTF(tgtdir);
				WriteOutput.writeUTF(filename);
				
				WriteOutput.flush();
			}
		
			FSReturnVals response = (FSReturnVals) ReadInput.readUnshared();
			System.out.println("recieved response from master in deletefile");
			return response;
		
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return FSReturnVals.Fail;
	}

	/**
	 * Opens the file specified by the FilePath and populates the FileHandle
	 * Returns FileDoesNotExist if the specified filename by FilePath is
	 * not-existent
	 *
	 * Example usage: OpenFile("/Shahram/CSCI485/Lecture1/Intro.pptx", FH1)
	 */
	public FSReturnVals OpenFile(String FilePath, FileHandle ofh) {
		
		// return master.OpenFile(FilePath, ofh);
		
		try {
			if (ClientSocket != null && WriteOutput != null && ReadInput != null) {
				// send the request to create a directory
				WriteOutput.writeChar('o');
				WriteOutput.writeUTF(FilePath);
				WriteOutput.writeObject(ofh);
				
				WriteOutput.flush();
			}
		
			FSReturnVals response = (FSReturnVals) ReadInput.readUnshared();
			System.out.println("recieved response from master in openfile");
			return response;
		
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return FSReturnVals.Fail;
	}

	/**
	 * Closes the specified file handle Returns BadHandle if ofh is invalid
	 *
	 * Example usage: CloseFile(FH1)
	 */
	public FSReturnVals CloseFile(FileHandle ofh) {
		
		// return master.CloseFile(ofh);
		
		try {
			if (ClientSocket != null && WriteOutput != null && ReadInput != null) {
				// send the request to create a directory
				WriteOutput.writeChar('x');
				WriteOutput.writeObject(ofh);
			}
		
			FSReturnVals response = (FSReturnVals) ReadInput.readUnshared();
			System.out.println("recieved response from master in closefile");
			return response;
		
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return FSReturnVals.Fail;
	}
	
	public void CloseClientFS() {
		try {
			ReadInput.close();
			WriteOutput.close();
			ClientSocket.close();
		} catch (IOException e) {
			System.out.println("issues closing the client");
			e.printStackTrace();
		}
	}

}