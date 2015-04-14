import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;


public class ChapterArray implements Serializable, Writable{
	private static final long serialVersionUID = 979576855418269634L;
	ArrayList<Text> list;
	final int comma = 7;
	
	public ChapterArray(){
		list = new ArrayList<Text>();
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		for(Text st: list){
			st.readFields(in);
		}
	}

	@Override
	public void write(DataOutput out) throws IOException {
		for(Text st: list){
			st.write(out);
		}
	}
	
	public void add(Text t, int s, int c){
//		String temp = "";
//		int tempSum = 0;
//		
//		for(int i=0; i < list.size(); i++){
//			temp = list.get(i).toString();
//			tempSum = Integer.parseInt(temp.substring(comma+2, temp.length()-1));
//			
//			if(s > tempSum){
//				// compare sums
//				list.add(i, t);
//				return;
//			}
//			else if((s == tempSum)
//					&& (c < Integer.parseInt(temp.substring(comma-2, comma)))){
//				//If sums are equal, compare chapters
//				list.add(i, t);
//				return;
//			}
//		}

		list.add(t);
	}
	
	public String toString(){
		String result = "\n";
		for(int i=0; i<list.size(); i++){
			result += list.get(i).toString() + "\n";
		}
		
		return result;
	}
}
