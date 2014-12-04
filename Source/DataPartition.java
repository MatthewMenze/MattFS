//THESE CLASSES EXPECT YOU TO BE NICE
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.*;

public class DataPartition {
	private DataBlock[] dataPartition;
	private boolean[] isFreeBlock;
	private String fileSystemDataLocation;
	private long sizeInBlocks;
	
	//Basic constructor, assumes file already exists.
	public DataPartition(String fileSystemDataLocation) throws Exception{
		this.fileSystemDataLocation = fileSystemDataLocation;
		Path path = Paths.get(fileSystemDataLocation);
		byte[] dataIn = Files.readAllBytes(path);
		char[] data = new char[dataIn.length];
		for (int i=0; i<data.length; i++){
			data[i] = (char)dataIn[i];
		}
		if (data.length%1024 != 0){
			System.out.println("Invalid data partition! "+data.length);
			System.exit(-1);
		}
		sizeInBlocks = data.length/1024;
		dataPartition = new DataBlock[(int)sizeInBlocks];
		for (int i=0; i<sizeInBlocks; i++)
			dataPartition[i] = new DataBlock(Arrays.copyOfRange(data, i*1024, (i*1024)+1024));
		isFreeBlock = new boolean[(int)sizeInBlocks];
		for (int i=0; i<sizeInBlocks; i++)
			isFreeBlock[i]=dataPartition[i].isFreeSpace();
	}
	
	//Write file to dataPartition, return index of first block of written file.
	public long writeFile(char[] fileData){
		if (getNumberOfFreeBlocks() < (fileData.length/1012)+1)
				return -1; //Return -1 if fileSystem does not have enough free space!
		long firstBlock = getNextFreeBlockIndex();
		long bytesLeft = fileData.length;
		long nextByte = 0;
		long nextBlock = firstBlock;
		while (bytesLeft > 1012){//Write out file data to blocks
			long workingBlock = nextBlock;
			dataPartition[(int)workingBlock].setData(Arrays.copyOfRange(fileData, (int)nextByte, (int)nextByte+1011));
			dataPartition[(int)workingBlock].setUsedBytes(1012);
			nextBlock = getNextFreeBlockIndex();
			dataPartition[(int)workingBlock].setNextBlock(nextBlock);
			isFreeBlock[(int)workingBlock] = false;
			nextByte+=1012;
			bytesLeft-=1012;
			if (bytesLeft==0) dataPartition[(int)workingBlock].setNextBlock(DataBlock.lastBlock);//Handle files that fit cleanly into blocks.
		}
		if (bytesLeft > 0){//Handle files that do not fit cleanly into blocks.
			dataPartition[(int)nextBlock].setData(Arrays.copyOfRange(fileData, (int)nextByte, (int)(nextByte+bytesLeft)));
			dataPartition[(int)nextBlock].setUsedBytes((int)bytesLeft);
			dataPartition[(int)nextBlock].setNextBlock(DataBlock.lastBlock);
			isFreeBlock[(int)nextBlock] = false;
		}
		return firstBlock;
	}
	//Read file from dataPartition, assume address given is start of file.
	public char[] readFile(long startBlock){
		Vector<Character> readData = new Vector<Character>();
		long workingBlock = startBlock;
		while (workingBlock != DataBlock.lastBlock){
			char[] chunk = dataPartition[(int)workingBlock].getData();
			for (int i = 0; i<dataPartition[(int)workingBlock].getUsedBytes(); i++)
				readData.add(chunk[i]);
			workingBlock = dataPartition[(int)workingBlock].getNextBlock();
		}
		char[] returnArray = new char[readData.size()];
		for (int i=0; i<returnArray.length; i++)
			returnArray[i] = readData.get(i);
		return returnArray;
	}
	//This method deallocates a chain of blocks into free space. It leaves data intact until overwrite though.
	public void deallocateFile(long startBlock){
		long nextBlock = startBlock;
		while (dataPartition[(int)nextBlock].getNextBlock() != DataBlock.lastBlock){
			isFreeBlock[(int)nextBlock] = true;//Mark block as freeSpace;
			DataBlock deallocateBlock = dataPartition[(int)nextBlock];
			nextBlock = deallocateBlock.getNextBlock();//Get next block to deallocate.
			deallocateBlock.setNextBlock(DataBlock.freeSpace);
		}//After this make sure last block in chain is deallocated!
		dataPartition[(int)nextBlock].setNextBlock(DataBlock.freeSpace);
		isFreeBlock[(int)nextBlock] = true;
	}
	
	//Write out current data partition to file.
	public void writeOut() throws Exception{
		byte[] writeData = new byte[(int)(1024*sizeInBlocks)];
		for (int i=0; i<sizeInBlocks; i++){
			char[] blockData = dataPartition[i].writeOut();
			for (int j=0; j<blockData.length; j++)
				writeData[i*1024+j] = (byte)blockData[j];
		}
		Path path = Paths.get(fileSystemDataLocation);
		Files.write(path, writeData);
	}
	
	public void formatToFreeSpace(){
		for (int i=0; i<sizeInBlocks; i++){
			dataPartition[i].setNextBlock(DataBlock.freeSpace);
			isFreeBlock[i] = true;
		}
	}
	
	public boolean growPartitionBy(long newBlocks){
		try {
			DataBlock[] tmp = new DataBlock[(int)(sizeInBlocks+newBlocks)];
			for (int i=0; i<sizeInBlocks; i++)//Copy in old block array
				tmp[i] = dataPartition[i];
			for (int i=(int)sizeInBlocks; i<tmp.length; i++)
				tmp[i] = new DataBlock();
			dataPartition = tmp;//Swap pointer to expanded partition.
			sizeInBlocks = dataPartition.length;
			isFreeBlock = new boolean[(int)sizeInBlocks];
			for (int i=0; i<sizeInBlocks; i++)
				isFreeBlock[i]=dataPartition[i].isFreeSpace();
			return true;
		}
		catch (Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean shrinkPartitionBy(long blocksToShrink){
		if (blocksToShrink > sizeInBlocks){
			System.out.println("Invalid shrink amount!");
			return false;
		}
		if (sizeInBlocks-blocksToShrink < getLastFreeBlockIndex()){
			System.out.println("Unable to shrink partition that much!");
			return false;
		}
		DataBlock[] tmp = new DataBlock[(int)(sizeInBlocks-blocksToShrink)];
		for (int i=0; i<tmp.length; i++)
			tmp[i] = dataPartition[i];
		dataPartition = tmp;
		sizeInBlocks = dataPartition.length;
		isFreeBlock = new boolean[(int)sizeInBlocks];
		for (int i=0; i<sizeInBlocks; i++)
			isFreeBlock[i]=dataPartition[i].isFreeSpace();
		return true;
	}
	
	//Return count of free blocks
	public long getNumberOfFreeBlocks(){
		long numberOfFreeBlocks = 0;
		for (long i=0; i<sizeInBlocks; i++)
			if (isFreeBlock[(int)i])
				numberOfFreeBlocks++;
		return numberOfFreeBlocks;
	}
	//Return count of used blocks
	public long getNumberOfUsedBlocks(){
		return sizeInBlocks - getNumberOfFreeBlocks();
	}
	//Return partition size in blocks
	public long getSizeInBlocks(){
		return sizeInBlocks;
	}
	
	//Get index of first free block.
	public long getNextFreeBlockIndex(){
		for (long i=0; i<sizeInBlocks; i++)
			if (isFreeBlock[(int)i])
				return i;
		return -1;//Returns -1 if full!
	}
	
	public long getLastFreeBlockIndex(){
		for (long i=sizeInBlocks-1; i>=0; i--)
			if (!isFreeBlock[(int)i])
				if (i!=sizeInBlocks-1)return i+1;
				else return -1;
		return 0;
	}
	
	//Print outs for debug:
	public void printOutAllocation(){
		for (int i=0; i<dataPartition.length; i++){
			long nextBlockVal = dataPartition[i].getNextBlock();
			if (nextBlockVal == 0)
				System.out.print("[ZERO]");
			else if (nextBlockVal == DataBlock.freeSpace)
				System.out.print("[FREE]");
			else if (nextBlockVal == DataBlock.lastBlock)
				System.out.print("[LAST]");
			else
				System.out.print("[OTHR]");
			if ((i+1)%10==0) System.out.println();
		}
	}
	public void printOutAllocationValue(){
		for (int i=0; i<dataPartition.length; i++){
			System.out.print("["+dataPartition[i].getNextBlock()+"]");
			if ((i+1)%10==0) System.out.println();
		}
	}
}
