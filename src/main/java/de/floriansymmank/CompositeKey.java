package de.floriansymmank;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;

public class CompositeKey implements WritableComparable<CompositeKey> {

    private IntWritable count;
    private Text word;

    public CompositeKey() {
        this.count = new IntWritable();
        this.word = new Text();
    }

    public CompositeKey(int count, String word) {
        this.count = new IntWritable(count);
        this.word = new Text(word);
    }

    public IntWritable getCount() {
        return count;
    }

    public Text getWord() {
        return word;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        count.write(out);
        word.write(out);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        count.readFields(in);
        word.readFields(in);
    }

    @Override
    public int compareTo(CompositeKey o) {
        // First compare counts in descending order
        int cmp = -1 * this.count.compareTo(o.getCount());
        if (cmp != 0) {
            return cmp;
        }
        // If counts are equal, compare words in ascending order
        return this.word.compareTo(o.getWord());
    }

    @Override
    public String toString() {
        return count + "\t" + word;
    }
}