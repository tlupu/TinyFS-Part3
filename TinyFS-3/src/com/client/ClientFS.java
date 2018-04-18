package com.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class ClientFS {
	public static ArrayList<String> directories;
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
		File newDir;
		boolean isCreated;
		//if creating the root directory
		if (src.equals("/")) {
			newDir = new File(dirname);
			if (newDir.exists()) {
				return FSReturnVals.DestDirExists;
			}
			newDir.mkdir();
			return FSReturnVals.Success;
		}
		//if creating a sub-directory
		else {
			File parentDir = new File(src.substring(1));
			if (!parentDir.exists()) {
				return FSReturnVals.SrcDirNotExistent;
			}
			else {
				newDir = new File(parentDir, dirname);
				if (newDir.exists()) {
					return FSReturnVals.DestDirExists;
				}
				newDir.mkdirs();
				return FSReturnVals.Success;	
			}
		}

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

		File directory;
		if (src.equals("/")) {
			directory = new File(dirname);
		}
		else {
			File parentDir = new File(src.substring(1));
			directory = new File(parentDir, dirname);			
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

	/**
	 * Renames the specified src directory in the specified path to NewName
	 * Returns SrcDirNotExistent if the src directory does not exist Returns
	 * DestDirExists if a directory with NewName exists in the specified path
	 *
	 * Example usage: RenameDir("/Shahram/CSCI485", "/Shahram/CSCI550") changes
	 * "/Shahram/CSCI485" to "/Shahram/CSCI550"
	 */
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

	/**
	 * Lists the content of the target directory Returns SrcDirNotExistent if
	 * the target directory does not exist Returns null if the target directory
	 * is empty
	 *
	 * Example usage: ListDir("/Shahram/CSCI485")
	 */
	public String[] ListDir(String tgt) {
		//ASK: how to return SrcDirNotExistent if the return type is an array of Strings
		directories = new ArrayList<String> ();
		listf(tgt.substring(1));
		System.out.println(ClientFS.directories.size());
		String [] directories = ClientFS.directories.toArray(new String[ClientFS.directories.size()]);
		return directories;
	}
	
	public void listf(String directoryName) {
	    File directory = new File(directoryName);
	    // get all the files from a directory
	    File[] fList = directory.listFiles();
	    for (File file : fList) {
			String filePath = "/" + file.getPath();
	        ClientFS.directories.add(filePath);
	        if (file.isDirectory()) {
	        		listf(file.getPath());
	        }
	    }
	}


	/**
	 * Creates the specified filename in the target directory Returns
	 * SrcDirNotExistent if the target directory does not exist Returns
	 * FileExists if the specified filename exists in the specified directory
	 *
	 * Example usage: Createfile("/Shahram/CSCI485/Lecture1/", "Intro.pptx")
	 */
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

	/**
	 * Deletes the specified filename from the tgtdir Returns SrcDirNotExistent
	 * if the target directory does not exist Returns FileDoesNotExist if the
	 * specified filename is not-existent
	 *
	 * Example usage: DeleteFile("/Shahram/CSCI485/Lecture1/", "Intro.pptx")
	 */
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

	/**
	 * Opens the file specified by the FilePath and populates the FileHandle
	 * Returns FileDoesNotExist if the specified filename by FilePath is
	 * not-existent
	 *
	 * Example usage: OpenFile("/Shahram/CSCI485/Lecture1/Intro.pptx", FH1)
	 */
	public FSReturnVals OpenFile(String FilePath, FileHandle ofh) {
		
		return null;
	}

	/**
	 * Closes the specified file handle Returns BadHandle if ofh is invalid
	 *
	 * Example usage: CloseFile(FH1)
	 */
	public FSReturnVals CloseFile(FileHandle ofh) {
		return null;
	}

}
