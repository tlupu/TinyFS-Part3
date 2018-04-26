package UnitTests3;

import com.client.ClientFS;
import com.client.ClientFS.FSReturnVals;

/**
 * UnitTest3 for Part 3 of TinyFS
 * @author Shahram Ghandeharizadeh and Jason Gui
 *
 */
public class UnitTest3 {
	
	public static int N = 755;
	static final String TestName = "Unit Test 3: ";

	public static void main(String[] args) {
		ClientFS clientFS = null;
		//unit test 1 call
		UnitTest1 ut1 = new UnitTest1();
		if (ut1.clientFS != null) {
			clientFS = ut1.clientFS;
			System.out.println("ut1 test is not null, so ut2 gets ut1 client");
		}
		else {
			clientFS = new ClientFS();
			System.out.println("ut2 instantiates new client");
		}
		ut1.test1(clientFS);
		
//		UnitTest1 ut1 = new UnitTest1();
//		ut1.test1(clientFS);
		
		System.out.println(TestName + "DeleteDir(\"/Shahram/N\"), ListDir(\"/Shahram\") and verify direcotry N is gone (success)");
		String ut1_dir1 = "Shahram";
		
		FSReturnVals ut1_fsrv = clientFS.DeleteDir("/" + ut1_dir1 + "/", String.valueOf(N));
		String[] ret1 = clientFS.ListDir("/" + ut1_dir1);
//		FSReturnVals ut1_fsrv = clientFS.DeleteDir("/" + dir1 + "/", String.valueOf(N));
//		String[] ret1 = clientFS.ListDir("/" + dir1);
		
		boolean isExist = isDirExist(ret1, "/" + ut1_dir1+"/"+N);
		if(isExist == true){
			System.out.println("Unit test 2 result: fail!");
    		return;
		}
		
		System.out.println(TestName + "DeleteDir(\"/Ghandeharizadeh/1/2/.../N\"), ListDir(\"/Ghandeharizadeh/1/2/.../\") and verify directory N is gone (success)");
		String ut2_dir2 = "Ghandeharizadeh";
		String lastSec = "/" + ut2_dir2;
		for(int i = 1; i < ut1.N; i++){
			lastSec = lastSec + "/" + i;
		}
		ut1_fsrv = clientFS.DeleteDir(lastSec + "/", String.valueOf(N));
		
		String[] ret2 = clientFS.ListDir(lastSec);
//		String[] ret2 = clientFS.ListDir(lastSec);
		
		isExist = isDirExist(ret2, lastSec + "/" + N);
		if(isExist == true){
			System.out.println("Unit test 2 result: fail!");
    		return;
		}
		
		System.out.println(TestName + "DeleteDir(\"/Shahram\") and verify it return the correct failure code");
		
		ut1_fsrv = clientFS.DeleteDir("/", ut1_dir1);
//		fsrv = clientFS.DeleteDir("/", dir1);
		
		if(ut1_fsrv == FSReturnVals.DirNotEmpty){
			System.out.println("Good! Detected " + ut1_dir1 + " exists.");
		} else {
			System.out.println("Unit test 2 result: fail!");
    		return;
		}
		
		System.out.println(TestName + "DeleteDir(\"/Ghandeharizadeh/1/2\") and verifty it returns the correct failure code");
		
		ut1_fsrv = clientFS.DeleteDir("/" + ut2_dir2 + "/1/", "2");
//		fsrv = clientFS.DeleteDir("/" + dir2 + "/1/", "2");
		
		if(ut1_fsrv == FSReturnVals.DirNotEmpty){
			System.out.println("Good!  Detected /" + ut2_dir2 + "/1/2 exists.");
		} else {
			System.out.println("Unit test 2 result: fail!");
    		return;
		}
		
		System.out.println(TestName + "RenameDir(\"/Shahram/i\", \"/Shahram/1i\") for i from 1 to N-1.  ListDir(\"/Shahram\") and verify the N-1 returns dirs are 1i to (N-1)i");
		for(int i = 1; i < ut1.N; i++){
			
			ut1_fsrv = clientFS.RenameDir("/" + ut1_dir1 + "/" + i, "/" + ut1_dir1 + "/" + i + "i");
//			fsrv = clientFS.RenameDir("/" + dir1 + "/" + i, "/" + dir1 + "/" + i + "i");
			
			if( ut1_fsrv != FSReturnVals.Success){
				System.out.println("Unit test 2 result: fail!");
	    		return;
			}
		}
		
		System.out.println(TestName + "RenameDir(\"/Ghandeharizadeh\", \"/ShahramGhandeharizadeh\")");
		
		ut1_fsrv = clientFS.RenameDir("/" + ut2_dir2, "/ShahramGhandeharizadeh");
//		fsrv = clientFS.RenameDir("/" + dir2, "/ShahramGhandeharizadeh");
		
		if( ut1_fsrv != FSReturnVals.Success ){
			System.out.println("Unit test 2 result: fail!");
    		return;
		}
		System.out.println(TestName + "Success!");
		
		
		
		//ClientFS clientFS = new ClientFS();
		System.out.println(TestName + "CreateDir /ShahramGhandeharizadeh/CSCI485");
		String dir1 = "ShahramGhandeharizadeh";
		FSReturnVals fsrv = clientFS.CreateDir("/" + dir1 + "/", "CSCI485");
		if( fsrv != FSReturnVals.Success ){
			System.out.println("Unit test 3 result: fail!");
    		return;
		}
		
		System.out.println(TestName + "CreateFile Lecture1/2/.../15 in /ShahramGhandeharizadeh/CSCI485");
		for(int i = 1; i <= N; i++){
			fsrv = clientFS.CreateFile("/" + dir1 + "/CSCI485/", "Lecture" + i);
			if( fsrv != FSReturnVals.Success ){
				System.out.println("Unit test 3 result: fail!");
	    		return;
			}
		}
		
		System.out.println(TestName + "DeleteFile Lecture1/2/.../15 in /ShahramGhandeharizadeh/CSCI485");
		for(int i = 1; i <= N; i++){
			fsrv = clientFS.DeleteFile("/" + dir1 + "/CSCI485/", "Lecture" + i);
			if( fsrv != FSReturnVals.Success ){
				System.out.println("Unit test 3 result: fail!");
	    		return;
			}
		}
		
		System.out.println(TestName + "CreateFile /Shahram/2/Lecture1, /Shahram/2/Lecture2, ...., /Shahram/2/Lecture15");
		String dir2 = "Shahram";
		for(int i = 1; i <= N; i++){
			fsrv = clientFS.CreateFile("/" + dir2 + "/2i/", "Lecture" + i);
			if( fsrv != FSReturnVals.Success ){
				System.out.println("Unit test 3 result: fail!");
	    		return;
			}
		}
		
		System.out.println(TestName + "DeleteFile /Shahram/2/Lecture1, /Shahram/2/Lecture2, ...., /Shahram/2/Lecture15");
		for(int i = 1; i <= N; i++){
			fsrv = clientFS.DeleteFile("/" + dir2 + "/2i/", "Lecture" + i);
			if( fsrv != FSReturnVals.Success ){
				System.out.println("Unit test 3 result: fail!");
	    		return;
			}
		}
		
		System.out.println(TestName + "Success!");
	}
	public static boolean isDirExist(String[] arr, String token){
		if (arr == null || arr.length == 0) {
			return false;
		}
		for (int i=0; i < arr.length; i++)
			if (arr[i].equals(token)) return true;
		return false;
	}
}
