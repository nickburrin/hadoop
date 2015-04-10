import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;


public class ChapterCount implements WritableComparable<ChapterCount>, Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1579855138498247809L;
	Text chapter;
	IntWritable occurences;

	public ChapterCount(Text text, IntWritable result) {
		super();
		chapter = new Text(text);
		occurences = result;
	}

	//Dont worry about this
	@Override
	public void readFields(DataInput in) throws IOException {
		// TODO Auto-generated method stub
		chapter.readFields(in);
		occurences.readFields(in);
	}

	//Dont worry about this
	@Override
	public void write(DataOutput out) throws IOException {
		// TODO Auto-generated method stub
		chapter.write(out);
		occurences.write(out);
	}

	public Text getChapter() {
		return chapter;
	}

	public void setChapter(Text chapter) {
		this.chapter = chapter;
	}

	public IntWritable getOccurances() {
		return occurences;
	}

	public void setOccurances(IntWritable occurances) {
		this.occurences = occurances;
	}

	@Override
	public int hashCode(){
		return chapter.hashCode();
	}

	// ChapterCount variables can ONLY be compared against other ChapterCounts. 
	// The thing that makes them different is the filename/chapter
	@Override
	public int compareTo(ChapterCount other) {
		return chapter.compareTo(other.chapter);
	}
}
