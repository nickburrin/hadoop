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
	IntWritable occurances;

	public ChapterCount(Text text, IntWritable result) {
		super();
		chapter = new Text(text);
		occurances = result;
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		// TODO Auto-generated method stub
		chapter.readFields(in);
		occurances.readFields(in);
	}

	@Override
	public void write(DataOutput out) throws IOException {
		// TODO Auto-generated method stub
		chapter.write(out);
		occurances.write(out);
	}

	public void write(Text word, IntWritable one) {
		chapter = word;
		occurances = one;
	}

	public Text getChapter() {
		return chapter;
	}

	public void setChapter(Text chapter) {
		this.chapter = chapter;
	}

	public IntWritable getOccurances() {
		return occurances;
	}

	public void setOccurances(IntWritable occurances) {
		this.occurances = occurances;
	}

	@Override
	public int hashCode(){
		return chapter.hashCode();
	}

	@Override
	public int compareTo(ChapterCount other) {
		return chapter.compareTo(other.chapter);
	}
}
