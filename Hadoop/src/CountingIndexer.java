import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class CountingIndexer {

	public static class TokenizerMapper extends Mapper<Object, Text, WordChapter, IntWritable>{

		private IntWritable one = new IntWritable(1);
		private Text word = new Text();
		private Text chapter = new Text();
		
		/**
		 * The mapper function takes in <Object, Text> --> <WordChapter, IntWritable>
		 * Parses the input and writes the output to the context. The parameters of the 
		 * context look something like <KeyIn, ValueIn, KeyOut, ValueOut>.
		 * All this really does is tally the number of times a word is found in a certain chapter.
		 */
		@Override
		public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			StringTokenizer itr = new StringTokenizer(value.toString());
			chapter.set(context.getWorkingDirectory().getName());			//Get the file name (not sure if works)
			
			System.out.println("Chapter value "+chapter);
			
			while (itr.hasMoreTokens()) {
				word.set(itr.nextToken().replaceAll("[^\\p{L}+]", " "));	//Strip the word of non-letter characters
				context.write(new WordChapter(word, chapter), one);			//Write the output 
			}
		}
	}

	public static class ChapterNumReducer extends Reducer<WordChapter, IntWritable, Text, ChapterCount>{
		private IntWritable result = new IntWritable();

		/**
		 * Technically this is the "combiner function". <WordChapter, IntWritable> --> <Text, ChapterCount>
		 * Takes in all the keys that have the same Word and Chapter and sums their occurrences. 
		 * i.e. If there are 10 WordChapters that look like <<"hey", chap00>, 1> then the output 
		 * will be one ChapterCount that looks like <"hey", <chap00, 10>>.
		 */
		@Override
		public void reduce(WordChapter key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
			int sum = 0;

			for (IntWritable val : values) {
				//Sum the number of times a given word is found in one chapter
				sum += val.get();
			}
			
			result.set(sum);
			context.write(key.getWord(), new ChapterCount(key.getChapter(), result));	//Write the output
		}
	}

	public static class AggregateChapters extends Reducer<Text, ChapterCount, Text, ChapterArray> {
		private ChapterArray result = new ChapterArray();

		/**
		 * So now we've got a bunch of words floating around with different ChapterCounts. 
		 * This method grabs all the words ChapterCount variables for one word (i.e. "hey") 
		 * and adds those ChapterCounts to an ArrayList. So essentially one ArrayList has 
		 * every ChapterCount associated with one word i.e. ("hey", <chap00, 10>, <chap04, 14>, ....)
		 */
		@Override
		public void reduce(Text key, Iterable<ChapterCount> values, Context context) throws IOException, InterruptedException {
			
			for (ChapterCount val : values) {
				result.add(val);
			}
			context.write(key, result);
		}
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf, "pride and prejudice"); //if you view the job counters, I'm assuming it's name will be "pride and prejudice"
		job.setJarByClass(CountingIndexer.class); //setting the main method of the jar

		//Setting mapper, combinere, and reducer classes
		job.setMapperClass(TokenizerMapper.class);
		job.setCombinerClass(ChapterNumReducer.class);
		job.setReducerClass(AggregateChapters.class);

		//Setting output types and remote input/output paths
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(ChapterArray.class);
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));

		//Starts the job and waits until it finishes
		job.waitForCompletion(true);
	}
}