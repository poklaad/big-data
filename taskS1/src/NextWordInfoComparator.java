import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

public class NextWordInfoComparator extends WritableComparator {
    protected NextWordInfoComparator() {
        super(NextWordInfo.class, true);
    }

    @Override
    public int compare(WritableComparable a, WritableComparable b) {
        NextWordInfo info1 = (NextWordInfo)a;
        NextWordInfo info2 = (NextWordInfo)b;
        return info1.compareTo(info2);
    }
}
