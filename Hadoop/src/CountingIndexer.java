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
		
		@Override
		public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			StringTokenizer itr = new StringTokenizer(value.toString());
			chapter.set(context.getWorkingDirectory().getName());
			
			System.out.println("Chapter value "+chapter);
			
			while (itr.hasMoreTokens()) {
				word.set(itr.nextToken().replaceAll("[^\\p{L}+]", " "));
				context.write(new WordChapter(word, chapter), one);
			}
		}
	}

	public static class ChapterNumReducer extends Reducer<WordChapter, IntWritable, Text, ChapterCount>{
		private IntWritable result = new IntWritable();

		@Override
		public void reduce(WordChapter key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
			int sum = 0;

			for (IntWritable val : values) {
				sum += val.get();
			}
			
			result.set(sum);
			context.write(key.getWord(), new ChapterCount(key.getChapter(), result));
		}
	}

	public static class AggregateChapters extends Reducer<Text, ChapterCount, Text, ChapterArray> {
		private ChapterArray result = new ChapterArray();

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
		Job job = Job.getInstance(conf, "pride and prejudice");
		job.setJarByClass(CountingIndexer.class);

		job.setMapperClass(TokenizerMapper.class);
		job.setCombinerClass(ChapterNumReducer.class);
		job.setReducerClass(AggregateChapters.class);

//		job.setOutputKeyClass(Text.class);
//		job.setOutputValueClass(ChapterArray.class);
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));

		job.waitForCompletion(true);
	}
}