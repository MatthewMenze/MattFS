public class FileDataNode {
	private String[] fileData;
	private FileDataNode next;
	
	public FileDataNode(String fileData, FileDataNode next){
		this.fileData = fileData.split("/");
		this.next=next;
	}

	public boolean addInOrder(String file){
		String fileString = this.getFileDataString();
		if (file.compareTo(fileString)==0)//Return false if file already exists!
			return false;
		if (next==null || file.compareTo(next.getFileDataString()) < 0){
			next = new FileDataNode(file, next);
			return true;
		}
		return next.addInOrder(file);
	}
	
	public boolean removeDirectory(String directory){
		if (next!=null){//This node is known to not be directory.
			//System.out.println(directory + "     " + next.getParentDirectoryString());
			if (next.isDirectory() && next.getParentDirectoryString().compareTo(directory)==0){
				next = next.getNext();
				return true;
			}
			return next.removeDirectory(directory);
		}
		System.out.println("Directory doesn't exist!");
		return false;
	}
	
	public boolean removeDirectoryWithoutReport(String directory){
		if (next!=null){//This node is known to not be directory.
			System.out.println(directory + "     " + next.getParentDirectoryString());
			if (next.isDirectory() && next.isPartOf(directory.split("/"))){
				next = next.getNext();
				return true;
			}
			return next.removeDirectoryWithoutReport(directory);
		}
		return false;
	}
	
	public boolean fileExists(String fileName){
		if (getAbsoluteFileLocation().compareTo(fileName)==0) return true;
		if (next!=null) return next.fileExists(fileName);
		return false;
	}
	
	//GETTER METHODS:
	//First block is last index of array made by splitting filenames.
	//If directory, first block == -1;
	public long getFirstBlock(){
		return Long.parseLong(fileData[fileData.length-1]);
	}
	
	public String getFileName(){
		return fileData[fileData.length-2];
	}
	
	public String getAbsoluteFileLocation(){
		String ret = "";
		for (int i=0; i<fileData.length-1; i++)
			ret+=fileData[i]+"/";
		return ret;
	}
	
	public String[] getParentDirectoryArray(){
		String[] ret = new String[fileData.length-2];
		for (int i=0; i<ret.length; i++){
			ret[i] = fileData[i];
		}
		return ret;
	}
	
	public String getParentDirectoryString(){
		String ret = "";
		for (int i=0; i<fileData.length-2; i++){
			ret+=fileData[i]+"/";
		}
		return ret;
	}
	
	public String getFileDataString(){
		String ret = "";
		for (int i=0; i<fileData.length; i++){
			ret+=fileData[i]+"/";
		}
		return ret;
	}
	
	public String[] getFileData(){
		return fileData;
	}
	
	public FileDataNode getNext(){
		return next;
	}
	
	public FileDataNode getFile(String fileName){
		if (fileName.compareTo(getAbsoluteFileLocation())==0){
			return this;
		}
		if (next!=null) return next.getFile(fileName);
		return null;
	}
	//SETTER METHODS:
	public void setNextBlock(long nextBlock){
		fileData[fileData.length-1] = nextBlock+"";
	}
	
	public void setFileName(String fileData){
		this.fileData = fileData.split("/");
	}
	
	public void setFileName(String[] fileData){
		this.fileData = fileData;
	}
	
	public void setNext(FileDataNode next){
		this.next = next;
	}
	
	//INFO METHODS:
	public long getFileCountIn(String[] dir){
		if (dir.length > fileData.length-2){//Don't check shorter addresses.
			if (next!= null) return next.getFileCountIn(dir);
			return 0;
		}
		for (int i=0; i<dir.length; i++){//See if address is different than current node
			if (dir[i].compareTo(fileData[i])!=0){
				if (next!=null) return next.getFileCountIn(dir);
				return 0;
			}
		}//Here we know current node is within specified directory.
		if (next!=null) return 1+next.getFileCountIn(dir);
		else return 1;
	}

	public long getDirectoryCountIn(String[] dir){
		if (dir.length > fileData.length-2){//Don't check shorter addresses.
			if (next!= null) return next.getFileCountIn(dir);
			return 0;
		}
		for (int i=0; i<dir.length; i++){//See if address is different than current node
			if (dir[i].compareTo(fileData[i])!=0 || this.getFirstBlock() != -1){//check if in directory
				if (next!=null) return next.getFileCountIn(dir);
				return 0;
			}
		}//Here we know current node is within specified directory.
		if (next!=null) return 1+next.getFileCountIn(dir);
		else return 1;
	}	
	
	public boolean isDirectory(){
		if (this.getFileName().compareTo("-")==0 && this.getFirstBlock() == -1)
			return true;
		return false;
	}
	
	public boolean isPartOf(String[] dir){
		if (dir.length > fileData.length-2){//Don't check shorter addresses.
			return false;
		}
		for (int i=0; i<dir.length; i++){//See if address is different than current node
			if (dir[i].compareTo(fileData[i])!=0){
				return false;
			}
		}//Here we know current node is within specified directory.
		return true;	
	}
	//PRINTER METHODS:
	public void printAll(){
		System.out.println(this.getFileDataString());
		if (next!=null) next.printAll();
	}
	
	public void printIfIn(String[] dir){
		if (dir.length > fileData.length-2){//Don't check shorter addresses.
			if (next!= null) next.printIfIn(dir);
			return;
		}
		for (int i=0; i<dir.length; i++){//See if address is different than current node
			if (dir[i].compareTo(fileData[i])!=0){
				if (next!=null) next.printIfIn(dir);
				return;
			}
		}//Here we know current node is within specified directory.
		System.out.println(this.getFileDataString());
		if (next!=null) next.printIfIn(dir);
		return;
	}
}
