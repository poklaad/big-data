import org.apache.hadoop.io.*;
import java.io.*;

public class NextWordInfo implements Writable {
    private Text word;
    private IntWritable count;

    public NextWordInfo() {
        this.word = new Text();
        this.count = new IntWritable();
    }

    public NextWordInfo(Text word, IntWritable count) {
        this.word = word;
        this.count = count;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        word.write(out);
        count.write(out);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        word.readFields(in);
        count.readFields(in);
    }

    public Text getWord() {
        return word;
    }

    public IntWritable getCount() {
        return count;
    }

    @Override
    public String toString() {
        return word.toString() + ":" + count.get();
    }
}
