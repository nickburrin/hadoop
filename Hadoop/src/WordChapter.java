import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;


public class WordChapter implements WritableComparable<WordChapter>, Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3798131815094136230L;
	Text word;
	Text chapter;
	
	public WordChapter(Text word, Text chapter) {
		super();
		this.word = word;
		this.chapter = chapter;
	}

	public Text getWord() {
		return word;
	}

	public void setWord(Text word) {
		this.word = word;
	}

	public Text getChapter() {
		return chapter;
	}

	public void setChapter(Text chapter) {
		this.chapter = chapter;
	}

	//Dont worry about this
	@Override
	public void readFields(DataInput in) throws IOException {
		// TODO Auto-generated method stub
		word.readFields(in);
		chapter.readFields(in);
	}

	//Dont worry about this
	@Override
	public void write(DataOutput out) throws IOException {
		// TODO Auto-generated method stub
		word.write(out);
		chapter.write(out);
	}

	@Override
	public int hashCode(){
		return (word.toString() + chapter.toString()).hashCode();
	}

	// This is tricky. WordChapter can only be compared against other WordChapters
	// If two WordChapters are the same, then they should have the same word AND filename/chapter
	@Override
	public int compareTo(WordChapter that) {
		int res = this.word.toString().compareTo(that.getWord().toString());
		
		if(res < 0){
			return -1;
		}else if(res == 0){
			return this.chapter.toString().compareTo(that.chapter.toString());
		} else{
			return 1;
		}
	}
}
