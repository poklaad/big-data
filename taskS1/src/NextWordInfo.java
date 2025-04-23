import org.apache.hadoop.io.*;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import org.apache.hadoop.io.*;
import java.io.*;

public class NextWordInfo implements WritableComparable<NextWordInfo> {
    private IntWritable frequency;
    private Text wordPair;

    public NextWordInfo() {
        this.frequency = new IntWritable();
        this.wordPair = new Text();
    }

    public NextWordInfo(int frequency, String wordPair) {
        this.frequency = new IntWritable(frequency);
        this.wordPair = new Text(wordPair);
    }

    @Override
    public void write(DataOutput out) throws IOException {
        frequency.write(out);
        wordPair.write(out);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        frequency.readFields(in);
        wordPair.readFields(in);
    }

    @Override
    public int compareTo(NextWordInfo other) {
        int cmp = -frequency.compareTo(other.frequency);
        if (cmp != 0) {
            return cmp;
        }
        return wordPair.compareTo(other.wordPair);
    }

    public IntWritable getFrequency() {
        return frequency;
    }

    public Text getWordPair() {
        return wordPair;
    }

    @Override
    public String toString() {
        return wordPair.toString() + ":" + frequency.get();
    }
}
