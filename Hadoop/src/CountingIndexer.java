import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class CountingIndexer {

	public static class TokenizerMapper extends Mapper<Object, Text, Text, Text>{

		private Text one = new Text("1");
		private Text word_chapter = new Text();

		/**
		 * The mapper function takes in <Object, Text> --> <WordChapter, IntWritable>
		 * Parses the input and writes the output to the context. The parameters of the 
		 * context look something like <KeyIn, ValueIn, KeyOut, ValueOut>.
		 * All this really does is tally the number of times a word is found in a certain chapter.
		 */
		@Override
		public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			StringTokenizer itr = new StringTokenizer(value.toString());

			String chapter = ((FileSplit) context.getInputSplit()).getPath().getName();

			while (itr.hasMoreTokens()) {
				String parsed = itr.nextToken().replaceAll("[^\\p{L}+]", " ");	//Strip the word of non-letter characters
				StringTokenizer tok = new StringTokenizer(parsed);

				while(tok.hasMoreTokens()){
					word_chapter = new Text(tok.nextToken().toLowerCase() + " " + chapter);
					context.write(word_chapter, one);			//Write the output
				}
			}
		}
	}

	public static class ChapterNumReducer extends Reducer<Text, Text, Text, Text>{
		private Text result = new Text();

		/**
		 * Technically this is the "combiner function". <WordChapter, IntWritable> --> <Text, ChapterCount>
		 * Takes in all the keys that have the same Word and Chapter and sums their occurrences. 
		 * i.e. If there are 10 WordChapters that look like <<"hey", chap00>, 1> then the output 
		 * will be one ChapterCount that looks like <"hey", <chap00, 10>>.
		 */
		@Override
		public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
			int sum = 0;

			for (Text val : values) {
				//Sum the number of times a given word is found in one chapter
				sum += Integer.parseInt(val.toString());
			}

			result.set("<" + key.toString().split(" ")[1] + ", " + sum + ">");	//move chapter from key to value
			key.set(key.toString().substring(0, key.toString().indexOf(" ")));	//remove the chapter from key

			context.write(key, result);	//Write the output
		}
	}

	public static class AggregateChapters extends Reducer<Text, Text, Text, Text> {
		private ChapterArray result = new ChapterArray();

		/**
		 * So now we've got a bunch of words floating around with different ChapterCounts. 
		 * This method grabs all the words ChapterCount variables for one word (i.e. "hey") 
		 * and adds those ChapterCounts to an ArrayList. So essentially one ArrayList has 
		 * every ChapterCount associated with one word i.e. ("hey", <chap00, 10>, <chap04, 14>, ....)
		 */
		@Override
		public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
			
			int sum;
			int chap;
			ArrayList<String> output = new ArrayList<String>();

			for (Text val : values) {
//				result.add(val, sum, chap);
				String insert = val.toString();
				sum = Integer.parseInt(insert.substring(9, insert.length()-1));
				chap = Integer.parseInt(insert.substring(5, 7));
				String temp = "";
				boolean added = false;
				
				for(int i = 0; i < output.size(); i++){
					temp = output.get(i);
					int tempSum = Integer.parseInt(temp.substring(9, temp.length()-1));
					if(sum > tempSum){
						output.add(i, insert);
						added = true;
						break;
					} else if(sum == tempSum){
						if(chap < Integer.parseInt(temp.substring(5, 7))){
							output.add(i, insert);
							added = true;
							break;
						}
					}
				}
				
				if(added == false){
					output.add(insert);
				}
				
				added = false;
			}
			
			String out = "\n";
			for(int i = 0; i < output.size(); i++){
				out += output.get(i) + "\n";
			}
			
			Text result = new Text(out);
			context.write(key, result);
		}
	}

	//After you undo this, you're back to where you were
	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf, "pride and prejudice");
		job.setJarByClass(CountingIndexer.class);

		//Settings for mapper, combiner and reducer
		job.setMapperClass(TokenizerMapper.class);
		job.setCombinerClass(ChapterNumReducer.class);
		job.setReducerClass(AggregateChapters.class);

		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(ChapterArray.class);

		//File path stuff
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));

		//Starts the job and waits until it finishes
		job.waitForCompletion(true);
	}
}