import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class HadoopDriver extends Configured implements Tool {

    public static void main(String[] args) throws Exception {
        System.exit(ToolRunner.run(new Configuration(), new HadoopDriver(), args));
    }

    @Override
    public int run(String[] args) throws Exception {

        if (args.length != 2) {
            System.err.println("USAGE: hadoop jar ... <input> <output>");
            return 1;
        }

        Path inPath     = new Path(args[0]);
        Path tempPath   = new Path(args[1] + "_temp");
        Path outPath    = new Path(args[1]);

        Job j1 = Job.getInstance(getConf(), "BestFollower");
        j1.setJarByClass(HadoopDriver.class);

        j1.getConfiguration().set("mapreduce.output.textoutputformat.separator", "\t");

        FileInputFormat.setInputPaths(j1, inPath);
        FileOutputFormat.setOutputPath(j1, tempPath);

        j1.setMapperClass(WordMapper.class);
        j1.setReducerClass(Summer.class);

        j1.setMapOutputKeyClass(Text.class);
        j1.setMapOutputValueClass(Text.class);
        j1.setOutputKeyClass(Text.class);
        j1.setOutputValueClass(Text.class);

        if (!j1.waitForCompletion(true)) return 1;

        Job j2 = Job.getInstance(getConf(), "SortByFrequency");
        j2.setJarByClass(HadoopDriver.class);

        j2.getConfiguration().set("mapreduce.output.textoutputformat.separator", "\t");

        FileInputFormat.setInputPaths(j2, tempPath);
        FileOutputFormat.setOutputPath(j2, outPath);

        j2.setMapperClass(SortingMapper.class);
        j2.setReducerClass(SortingReducer.class);

        j2.setMapOutputKeyClass(NextWordInfo.class);
        j2.setMapOutputValueClass(NullWritable.class);
        j2.setOutputKeyClass(Text.class);
        j2.setOutputValueClass(IntWritable.class);

        j2.setSortComparatorClass(NextWordInfoComparator.class);
        j2.setNumReduceTasks(1);

        return j2.waitForCompletion(true) ? 0 : 1;
    }
}

