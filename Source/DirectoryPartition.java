//THESE CLASSES EXPECT YOU TO BE NICE
import java.io.*;

public class DirectoryPartition {
	private FileDataNode head;
	private String fileSystemDirectoryLocation;
	
	public DirectoryPartition(){
		head = null;
	}
	
	public DirectoryPartition(FileDataNode head){
		this.head = head;
	}
	
	public DirectoryPartition(String fileSystemDirectoryLocation) throws Exception{
		this.fileSystemDirectoryLocation = fileSystemDirectoryLocation;
		FileInputStream fis = new FileInputStream(this.fileSystemDirectoryLocation);
		BufferedReader directoryIn = new BufferedReader(new InputStreamReader(fis));
		String file = null;
		while ((file = directoryIn.readLine()) != null){
			this.addInOrder(file);
		}
		directoryIn.close();
	}
	
	public void writeOut() throws Exception{
		PrintWriter writer = new PrintWriter(fileSystemDirectoryLocation, "UTF-8");
		if (head == null){//If there is nothing to write:
			writer.close();
			return;
		}
		FileDataNode tmp = head;
		while (tmp != null){
			writer.println(tmp.getFileDataString());
			tmp = tmp.getNext();
		}
		writer.close();
	}
	
	public boolean addInOrder(String file){
		if (head==null){
			head = new FileDataNode(file, null);
			return true;
		}
		return head.addInOrder(file);
	}
	
	public boolean removeDirectory(String directory){
		if (head!=null){
			if (head.isDirectory() && head.getParentDirectoryString().compareTo(directory)==0){
				head = head.getNext();
				return true;
			}
			return head.removeDirectory(directory);
		}
		System.out.println("Empty file system!");
		return false;
	}
	
	public boolean removeDirectoryWithoutReport(String directory){
		if (head!=null){
			if (head.isDirectory() && head.getParentDirectoryString().compareTo(directory)==0){
				head = head.getNext();
				return true;
			}
			return head.removeDirectoryWithoutReport(directory);
		}
		return false;
	}
	
	public boolean printFileSystem(){
		if (head!=null) {
			head.printAll();
			return true;
		}
		else {
			System.out.println("NULL FILE SYSTEM!");
			return false;
		}
	}
	
	public boolean printFileSystemFrom(String dir){
		if (getFileCountIn(dir) < 1){ 
			System.out.println("Directory does not exist");
			return false;
		}
		if (head!=null){
			head.printIfIn(dir.split("/"));
			return true;
		}
		System.out.println("NULL FILE SYSTEM!");
		return false;
	}
	
	public boolean fileExists(String fileName){
		if (head!=null) return head.fileExists(fileName);
		return false;
	}
	
	public FileDataNode getFile(String fileName){
		if (head!=null) return head.getFile(fileName);
		else return null;
	}
	
	//Count == 0 -> Dir doesn't exist. Count == 1 -> Empty Dir. Else Count -> # of files contained.
	public long getFileCountIn(String dir){
		if (head!=null) return head.getFileCountIn(dir.split("/"));
		else return 0;
	}
	
	public long getDirectoryCountIn(String dir){
		if (head!=null) return head.getDirectoryCountIn(dir.split("/"));
		else return 0;
	}
}
