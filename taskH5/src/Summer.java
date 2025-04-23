import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Summer extends Reducer<Text, Text, Text, Text> {

    @Override
    protected void reduce(Text key, Iterable<Text> values, Context ctx)
            throws IOException, InterruptedException {

        Map<String, Integer> freq = new HashMap<>();

        for (Text v : values) {
            String w = v.toString();
            freq.put(w, freq.getOrDefault(w, 0) + 1);
        }

        String bestWord  = null;
        int    bestCount = 0;

        for (Map.Entry<String, Integer> e : freq.entrySet()) {
            int c = e.getValue();
            if (c > bestCount || (c == bestCount && e.getKey().compareTo(bestWord) < 0)) {
                bestWord  = e.getKey();
                bestCount = c;
            }
        }

        if (bestWord != null) {
            ctx.write(new Text(key.toString() + "\t" + bestWord),
                      new Text(String.valueOf(bestCount)));
        }
    }
}

