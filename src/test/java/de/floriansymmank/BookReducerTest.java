package de.floriansymmank;

import java.io.IOException;
import java.util.Arrays;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import org.mockito.MockitoAnnotations;

public class BookReducerTest {

    private BookReducer reducer;

    @Mock
    private Reducer<Text, IntWritable, Text, IntWritable>.Context context;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        reducer = new BookReducer();
    }

    @Test
    public void testReduce() throws IOException, InterruptedException {
        Text key = new Text("word");
        Iterable<IntWritable> values = Arrays.asList(new IntWritable(1), new IntWritable(2), new IntWritable(3));

        reducer.reduce(key, values, context);

        verify(context).write(key, new IntWritable(6));
    }
}
