import java.util.Scanner;

public class FSProject {
	public static FileSystemManager FS;

	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		boolean ret;
		FS = new FileSystemManager();
		//FS.dataPartition.formatToFreeSpace();
		ret =FS.mkDir("Stuff");
		System.out.println(ret);
		ret = FS.mkDir("Things");
		System.out.println(ret);
		ret = FS.mkDir("Things");
		System.out.println(ret);
		ret = FS.mkDir("Things/OtherStuff");
		System.out.println(ret);
		ret = FS.mkDir("Things/MoreStuff/Stuff");
		System.out.println(ret);
		ret = FS.mkDir("Trinkets");
		System.out.println(ret);
		ret = FS.mkDir("Stuff/OtherStuff");
		System.out.println(ret);
		ret = FS.rmDir("^/Stuff/OtherStuff");
		System.out.println(ret);
		ret = FS.rmDir("^");
		System.out.println(ret);
		ret = FS.list();
		System.out.println(ret);
		ret = FS.list("^/Things");
		System.out.println(ret);
		System.out.println("Free Space (Blocks): "+FS.freeSpace());
		ret = FS.growFS(100);
		System.out.println(ret);
		System.out.println("Free Space (Blocks): "+FS.freeSpace());
		ret = FS.shrinkFS(100);
		System.out.println(ret);
		System.out.println("Free Space (Blocks): "+FS.freeSpace());	
		FS.list();
		ret = FS.delTree("^/Things");
		System.out.println(ret);
		FS.list();
		FileContainer file = FS.open("^/Trinkets/testFile");
		file.append("Holy shit did this work?".toCharArray());
		file.close();
		
		FS.list();
		FS.close();
		
	}

}
