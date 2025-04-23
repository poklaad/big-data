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

        Configuration conf = getConf();

        Path inPath     = new Path(args[0]);
        Path tempPath1  = new Path(args[1] + "_temp1");
        Path tempPath2  = new Path(args[1] + "_temp2");
        Path outPath    = new Path(args[1]);

        Job j1 = Job.getInstance(conf, "BestFollower");
        j1.setJarByClass(HadoopDriver.class);
        j1.getConfiguration()
           .set("mapreduce.output.textoutputformat.separator", "\t");

        FileInputFormat.setInputPaths(j1, inPath);
        FileOutputFormat.setOutputPath(j1, tempPath1);

        j1.setMapperClass(WordMapper.class);
        j1.setReducerClass(Summer.class);
        j1.setMapOutputKeyClass(Text.class);
        j1.setMapOutputValueClass(Text.class);
        j1.setOutputKeyClass(Text.class);
        j1.setOutputValueClass(Text.class);

        if (!j1.waitForCompletion(true)) return 1;

        Job j2 = Job.getInstance(conf, "SortByFrequency");
        j2.setJarByClass(HadoopDriver.class);
        j2.getConfiguration()
           .set("mapreduce.output.textoutputformat.separator", "\t");

        FileInputFormat.setInputPaths(j2, tempPath1);
        FileOutputFormat.setOutputPath(j2, tempPath2);

        j2.setMapperClass(SortingMapper.class);
        j2.setReducerClass(SortingReducer.class);
        j2.setMapOutputKeyClass(NextWordInfo.class);
        j2.setMapOutputValueClass(NullWritable.class);
        j2.setOutputKeyClass(Text.class);
        j2.setOutputValueClass(IntWritable.class);
        j2.setSortComparatorClass(NextWordInfoComparator.class);
        j2.setNumReduceTasks(1);

        if (!j2.waitForCompletion(true)) return 1;

        Job topJob = Job.getInstance(conf, "TakeTop1");
        topJob.setJarByClass(HadoopDriver.class);
        topJob.getConfiguration()
              .set("mapreduce.output.textoutputformat.separator", "\t");

        FileInputFormat.setInputPaths(topJob, tempPath2);
        FileOutputFormat.setOutputPath(topJob, outPath);

        topJob.setMapperClass(Top1Mapper.class);
        topJob.setReducerClass(PassThroughReducer.class);
        topJob.setMapOutputKeyClass(IntWritable.class);
        topJob.setMapOutputValueClass(Text.class);
        topJob.setOutputKeyClass(Text.class);
        topJob.setOutputValueClass(NullWritable.class);
        topJob.setNumReduceTasks(1);

        return topJob.waitForCompletion(true) ? 0 : 1;
    }
}

