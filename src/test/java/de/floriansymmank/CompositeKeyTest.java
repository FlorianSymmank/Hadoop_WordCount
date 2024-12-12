package de.floriansymmank;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

public class CompositeKeyTest {

    private CompositeKey compositeKey;

    @Before
    public void setUp() {
        compositeKey = new CompositeKey(10, "test");
    }

    @Test
    public void testGetCount() {
        assertEquals(new IntWritable(10), compositeKey.getCount());
    }

    @Test
    public void testGetWord() {
        assertEquals(new Text("test"), compositeKey.getWord());
    }

    @Test
    public void testWriteAndReadFields() throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutput out = new DataOutputStream(byteArrayOutputStream);
        compositeKey.write(out);

        byte[] byteArray = byteArrayOutputStream.toByteArray();
        DataInput in = new DataInputStream(new ByteArrayInputStream(byteArray));

        CompositeKey newCompositeKey = new CompositeKey();
        newCompositeKey.readFields(in);

        assertEquals(compositeKey.getCount(), newCompositeKey.getCount());
        assertEquals(compositeKey.getWord(), newCompositeKey.getWord());
    }

    @Test
    public void testCompareTo() {
        CompositeKey key1 = new CompositeKey(10, "banana");
        CompositeKey key2 = new CompositeKey(10, "apple");
        CompositeKey key3 = new CompositeKey(5, "apple");

        // Comparing different words with the same count, ascending order
        assertEquals(1, key1.compareTo(key2)); // "banana" > "apple"
        assertEquals(-1, key2.compareTo(key1)); // "apple" < "banana"

        // Comparing different counts, descending order
        assertEquals(-1, key1.compareTo(key3)); // 10 > 5
        assertEquals(1, key3.compareTo(key1)); // 5 < 10

        // Comparing equal keys, should return 0
        assertEquals(0, key1.compareTo(new CompositeKey(10, "banana"))); // same count and word
    }

    @Test
    public void testToString() {
        assertEquals("10\ttest", compositeKey.toString());
    }
}