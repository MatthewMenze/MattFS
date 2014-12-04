public class DataBlock {
	public static long freeSpace = Long.MAX_VALUE;
	public static long lastBlock = Long.MAX_VALUE-1;
	public static int maxUsableBytes = 1012;
	private long nextBlock;
	private int usedBytes;
	private char[] data;
	
	//Empty constructor:
	public DataBlock(){
		nextBlock = freeSpace;
		usedBytes = 0;
		data = new char[1012];
		for (int i=0; i<data.length; i++)
			data[i] = 0;
	}
	//Explicit constructor:
	public DataBlock(long nextBlock, int usedBytes, char[] data){
		if (usedBytes > maxUsableBytes){
			System.out.println("maxUsableBytes exceeded while constructing!");
			System.exit(-1);
		}
		if (data.length != maxUsableBytes){
			System.out.println("Invalid data array while constructing!");
			System.exit(-1);
		}
		this.nextBlock = nextBlock;
		this.usedBytes = usedBytes;
		this.data = data;
	}
	//Raw array constructor:
	public DataBlock(char[] rawArray){
		data = new char[1012];//Remember to initialize this next time, noob!
		if (rawArray.length != 1024){
			System.out.println("Invalid byte array loaded! Array Length = "+rawArray.length);
			System.exit(-1);
		}
		//Index[0] is most significant byte.
		for (int i=0; i<8; i++)//Bit-bang in nextBlock:
			nextBlock+=(((long)(rawArray[7-i]&0xFF))<<(8*i));
		for (int i=0; i<4; i++)//Bit-bang in usedBytes:
			usedBytes+=(((int)(rawArray[8+(3-i)]&0xFF))<<(8*i));
		for (int i=0; i<maxUsableBytes; i++)//Copy in data:
			data[i]=rawArray[i+12];
	}
	
	//Write out to byte array:
	public char[] writeOut(){
		char[] writeArray = new char[1024];
		long tempNextBlock = nextBlock;
		int tempUsedBytes = usedBytes;
		for (int i=7; i>=0; i--)//Bit-bang out nextBlock:
			writeArray[7-i] = (char)((tempNextBlock>>(8*i))&0xFF);
		for (int i=3; i>=0; i--)//Bit-bang out usedBytes:
			writeArray[8+(3-i)] = (char)((tempUsedBytes>>(8*i))&0xFF);
		for (int i=0; i<maxUsableBytes; i++)//Copy in data:
			writeArray[i+12] = data[i];
		/*for (int i=0; i<12; i++){
			System.out.print("["+((int)writeArray[i])+"]");
			if (i==7) System.out.print("---");
		}
		System.out.println();*/
		return writeArray;
	}
	
	public boolean isFreeSpace(){
		if (nextBlock==freeSpace) return true;
		else return false;
	}
	
	public boolean isEndOfFile(){
		if (nextBlock==lastBlock) return true;
		else return false;
	}
	
	//Setters
	public long getNextBlock(){return nextBlock;}
	public int getUsedBytes(){return usedBytes;}
	public char[] getData(){return data;}
	//Getters, error checking where possible.
	public void setNextBlock(long nextBlock){this.nextBlock = nextBlock;}
	public void setUsedBytes(int usedBytes){
		if (usedBytes > maxUsableBytes){
			System.out.println("maxUsableBytes exceeded!");
			System.exit(-1);
		}
		this.usedBytes = usedBytes;
	}
	public void setData(char[] data){
		if (data.length > maxUsableBytes){
			System.out.println("Data array error!");
			System.exit(-1);
		}
		if (data.length != maxUsableBytes){
			char[] newData = new char[1012];
			for (int i=0; i<maxUsableBytes; i++){
				if (i<data.length) newData[i] = data[i];
				else newData[i] = '\0';
			}
			this.data = newData;
			return;
		}
		this.data = data;
	}
}
