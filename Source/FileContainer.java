
public class FileContainer {
	private FileDataNode metaData;
	private char[] data;
	private DataPartition partition;
	private int putPos, getPos;
	
	public FileContainer(FileDataNode metaData, char[] data, DataPartition partition){
		this.metaData = metaData;
		this.data = data;
		this.partition = partition;
		putPos = 0;
		getPos = 0;
	}
	
	public FileContainer(String fileName, DirectoryPartition metaData, DataPartition partition){
		data = new char[1];
		data[0]='\0';//Initialize array to null char for starts.
		putPos = 0;//Point to beginning
		getPos = 0;
		this.partition = partition;
		long firstBlock = this.partition.writeFile(data);//Allocate a start block!
		metaData.addInOrder(fileName+firstBlock+"/");//Add file entry TO PARTITION!
		this.metaData = metaData.getFile(fileName);//Get metaData for file
	}
	
	public void printFile(){
		for (int i=0; i<data.length; i++)
			System.out.println(data[i]);
	}
	
	public void printMetaData(){
		System.out.println("File Name: "+metaData.getFileName());
		System.out.println("Parent Directory: "+metaData.getParentDirectoryString());
		System.out.println("File Size (Blocks): "+((data.length/1012)+(data.length%1012)));
		System.out.println("Root Block on Disk: "+metaData.getFirstBlock());
	}
	//Set put position:
	public boolean seekP(int position){
		if (position<data.length-1){
			putPos = position;
			return true;
		}
		return false;
	}
	//Overloaded method for seeking to start/end of file.
	public boolean seekP(String position){
		if (position.compareToIgnoreCase("BEG")==0){
			putPos = 0;
			return true;
		}
		else if (position.compareToIgnoreCase("END")==0){
			putPos = data.length-1;
			return true;
		}
		else return false;
	}
	//Set get position:
	public boolean seekG(int position){
		if (position >= 0 && position<data.length){
			getPos = position;
			return true;
		}
		return false;
	}
	//Overloaded method for seeking to start/end of file.
	public boolean seekG(String position){
		if (position.compareToIgnoreCase("BEG")==0){
			getPos = 0;
			return true;
		}
		else if (position.compareToIgnoreCase("END")==0){
			getPos = data.length-1;
			return true;
		}
		else return false;
	}
	//Write char data to put position, increment put position.
	public boolean put(char data){
		if (putPos<this.data.length){
			this.data[putPos] = data;
			putPos++;
			return true;
		}
		return false;
	}
	//Read char from get position, increment get position.
	public char get(){
		if (getPos<data.length){
			getPos++;
			return data[getPos-1];
		}
		return '\0';
	}
	
	public void append(char appendData){
		char[] newData = new char[data.length+1];
		for (int i=0; i<data.length; i++){
			newData[i]=data[i];
		}
		newData[newData.length-1] = appendData;
		data = newData;
	}
	
	public void append(char[] appendData){
		char[] newData = new char[data.length+appendData.length];
		for (int i=0; i<newData.length; i++){//Copy in array;
			newData[i] = (i<data.length) ? data[i] : appendData[i-data.length];
		}
		data = newData;//Make data new array with appended data.
	}
	
	public boolean close(){
		return writeToFS();
	}
	
	public boolean writeToFS(){
		try{
			partition.deallocateFile(metaData.getFirstBlock());
			metaData.setNextBlock(partition.writeFile(data));
			return true;
		}
		catch (Exception e){
			e.printStackTrace();
			return false;
		}
	}
}
