import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.*;
import org.apache.hadoop.mapreduce.lib.output.*;
import org.apache.hadoop.util.*;

import java.io.*;
import java.util.*;

public class Summer
extends Reducer<Text, Text, Text, NextWordInfo>
{
    private NextWordInfo result = new NextWordInfo();

    public void reduce(Text key, Iterable<Text> values, Context context)
	throws IOException, InterruptedException
    {
	Map<String, Integer> nextWordCounts = new HashMap<>();

        for (Text val : values) {
            String word = val.toString();
            nextWordCounts.put(word, nextWordCounts.getOrDefault(word, 0) + 1);
        }

        if (!nextWordCounts.isEmpty()) {
	    Map.Entry<String, Integer> maxEntry = Collections.max(
                nextWordCounts.entrySet(),
                Map.Entry.comparingByValue()
            );

            result.getWord().set(maxEntry.getKey());
            result.getCount().set(maxEntry.getValue());
            context.write(key, result);
        }
    }
}
