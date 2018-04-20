package UnitTests3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import com.client.ClientFS;
import com.client.ClientFS.FSReturnVals;

/**
 * UnitTest1 for Part 3 of TinyFS
 * @author Shahram Ghandeharizadeh and Jason Gui
 *
 */
public class UnitTest1 {
	
	public static int N = 100;
	static final String TestName = "Unit Test 1: ";
	static ClientFS clientFS = null;
	
	public static void main(String[] args) {
		System.out.println("unit test one instantiates client");
		clientFS = new ClientFS();
		test1(clientFS);
//		test1(new ClientFS());
	}
	
	public static void test1(ClientFS cfs){
		System.out.println(TestName + "Create dir /Shahram, /Shahram/1, /Shahram/2, /Shahram/3, ... /Shahram/N and verify them.");
		String dir1 = "Shahram";
		
		FSReturnVals fsrv = cfs.CreateDir("/", dir1);
//		FSReturnVals fsrv = clientFS.CreateDir("/", dir1);
		
		if ( fsrv != FSReturnVals.Success ){
			System.out.println("Unit test 1 result: fail! (1)");
    		return;
		}
		String[] gen1 = new String[N];
		for(int i = 1; i <= N; i++){
			
			fsrv = cfs.CreateDir("/" + dir1 + "/", String.valueOf(i));
//			fsrv = clientFS.CreateDir("/" + dir1 + "/", String.valueOf(i));
			
			if( fsrv != FSReturnVals.Success ){
				System.out.println("Unit test 1 result : fail! (2)");
	    		return;
			}
			gen1[i - 1] = "/" + dir1 + "/" + i;
		}
		
		String[] ret1 = cfs.ListDir("/" + dir1);
//		String[] ret1 = clientFS.ListDir("/" + dir1);
		
		boolean compare1 = compareArrays(gen1, ret1);
		if(compare1 == false){
			System.out.println("Unit test 1 result : fail! (3)");
    		return;
		}
		
		System.out.println(TestName + "Create dir /Ghandeharizadeh, /Ghandeharizadeh/1, /Ghandeharizadeh/1/2, .... /Ghandeharizadeh/1/2/.../N and verify them.");
		String dir2 = "Ghandeharizadeh";
		
		fsrv = cfs.CreateDir("/", dir2);
//		fsrv = clientFS.CreateDir("/", dir2);
		
		if( fsrv != FSReturnVals.Success ){
			System.out.println("Unit test 1 result : fail! (4)");
    		return;
		}
		String[] gen2 = new String[N];
		String prev = "/" + dir2;
		for(int i = 1; i <= N; i++){
			
			fsrv = cfs.CreateDir(prev + "/", String.valueOf(i));
//			fsrv = clientFS.CreateDir(prev + "/", String.valueOf(i));
			
			if( fsrv != FSReturnVals.Success ){
				System.out.println("Unit test 1 result : fail! (5)");
	    		return;
			}
			prev = prev + "/" + i;
			gen2[i - 1] = prev;
		}	
		
		ret1 = cfs.ListDir("/" + dir2);
//		ret1 = clientFS.ListDir("/" + dir2);
		
		compare1 = compareArrays(gen2, ret1);
		if(compare1 == false){
			System.out.println("Unit test 1 result: fail! (6)");
    		return;
		}
		
        System.out.println(TestName + "Success!"); 
	}
	
	public static boolean compareArrays(String[] arr1, String[] arr2) {
		System.out.println("ARRAY 1");
		for (int i = 0; i < arr1.length; i++) {
			System.out.println(arr1[i]);
		}
		System.out.println("ARRAY 2");
		for (int i = 0; i < arr2.length; i++) {
			System.out.println(arr2[i]);
		}
	    HashSet<String> set1 = new HashSet<String>(Arrays.asList(arr1));
	    HashSet<String> set2 = new HashSet<String>(Arrays.asList(arr2));
	    return set1.equals(set2);
	}

}
