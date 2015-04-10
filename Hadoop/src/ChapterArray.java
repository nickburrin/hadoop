import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import org.apache.hadoop.io.Writable;


public class ChapterArray implements Serializable, Writable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3278973353365638229L;
	ArrayList<ChapterCount> list;
	
	public ChapterArray() {
		super();
		this.list = new ArrayList<ChapterCount>();
	}

	@Override
	public void readFields(DataInput arg0) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void write(DataOutput arg0) throws IOException {
		// TODO Auto-generated method stub
		
	}

	public void add(ChapterCount val) {
		list.add(val);
	}

}
