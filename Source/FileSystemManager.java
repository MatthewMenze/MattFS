import java.util.*;
import java.io.*;

public class FileSystemManager {
	public static DirectoryPartition directoryPartition;
	public static DataPartition dataPartition;
	private String currentWorkingDir;
	
	public FileSystemManager(){
		try {
			directoryPartition = new DirectoryPartition("C:\\Users\\Matthew\\workspace\\FileSystem\\src\\test.dir");
			dataPartition = new DataPartition("C:\\Users\\Matthew\\workspace\\FileSystem\\src\\test.dat");
		}
		catch (Exception e){
			System.out.println("Bad filename!!!");
			e.printStackTrace();
		}
	}
	
	public void close(){
		try{
			directoryPartition.writeOut();
			dataPartition.writeOut();
		}
		catch (Exception e){
			System.out.println("We're dead Jim, cannot write out FS!");
			e.printStackTrace();
		}
	}
	
	//Adds directory, assumes nice input.
	public boolean mkDir(String directory){
		return directoryPartition.addInOrder("^/"+directory+"/-/-1/");//-1 at end note directory, as size is -1, filename of all dirs is "-" and meaningless.
	}
	
	//ASSUMES DIRECTORY NOT FILE
	public boolean rmDir(String directory){
		directory+="/";//Expects input dir format of "^/dir/dir/dir/dirToDelete" without closing "/"!!!
		long status = directoryPartition.getFileCountIn(directory);
		//System.out.println("STATUS: "+status);
		if (status == 0){
			System.out.println("Directory does not exist!");
			return false;
		}
		if (status == 1){
			directoryPartition.removeDirectory(directory);
			return true;
		}
		System.out.println("Directory is not empty!");
		return false;
	}
	
	public boolean delTree(String directory){
		directory+="/";//Expects input dir format of "^/dir/dir/dir/dirToDelete" without closing "/"!!!
		long countOfFiles = directoryPartition.getFileCountIn(directory);
		long countOfDirs = directoryPartition.getDirectoryCountIn(directory);
		if (countOfFiles == 0){
			System.out.println("Directory does not exist!");
			return false;
		}
		if (countOfFiles == countOfDirs){
			while(directoryPartition.removeDirectoryWithoutReport(directory));
			return true;
		}
		System.out.println("Directory is not empty!");
		return false;
	}
	
	public FileContainer open(String fileName){
		fileName+="/";
		if (directoryPartition.fileExists(fileName)){
			FileDataNode fileData = directoryPartition.getFile(fileName);
			long firstBlock = fileData.getFirstBlock();
			char[] data = dataPartition.readFile(firstBlock);
			return new FileContainer(fileData, data, dataPartition);
		}
		return new FileContainer(fileName, directoryPartition, dataPartition);
	}
	
	public boolean growFS(long blocksToGrow){
		System.out.println("Expanding file system by "+blocksToGrow+" blocks.");
		return dataPartition.growPartitionBy(blocksToGrow);
	}
	
	public boolean shrinkFS(long blocksToShrink){
		System.out.println("Shrinking file system by "+blocksToShrink+" blocks.");
		return dataPartition.shrinkPartitionBy(blocksToShrink);
	}
	
	public boolean list(){//Print whole directory!
		return directoryPartition.printFileSystem();
	}
	
	public boolean list(String directory){
		directory+="/";//Expects input format "^/dir/dir/dirToPrintFrom" without closing "/"!!!
		return directoryPartition.printFileSystemFrom(directory);
	}
	
	public boolean isFull(){
		if (dataPartition.getNumberOfFreeBlocks() != 0) return false;
		else return true;
	}
	
	public long freeSpace(){
		return dataPartition.getNumberOfFreeBlocks();
	}
}
