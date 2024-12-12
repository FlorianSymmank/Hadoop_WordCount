package de.floriansymmank;

import java.io.IOException;
import java.util.Arrays;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Reducer;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class SortByCountReducerTest {

    private SortByCountReducer reducer;
    private Reducer<CompositeKey, NullWritable, CompositeKey, NullWritable>.Context context;

    @Before
    public void setUp() {
        reducer = new SortByCountReducer();
        context = mock(Reducer.Context.class);
    }

    @Test
    public void testReduce() throws IOException, InterruptedException {
        CompositeKey key = new CompositeKey(2, "word");
        CompositeKey key2 = new CompositeKey(5, "word2");

        Iterable<NullWritable> values1 = Arrays.asList(NullWritable.get(), NullWritable.get());
        Iterable<NullWritable> values2 = Arrays.asList(NullWritable.get(), NullWritable.get());

        reducer.reduce(key, values1, context);
        reducer.reduce(key2, values2, context);

        ArgumentCaptor<CompositeKey> keyCaptor = ArgumentCaptor.forClass(CompositeKey.class);
        ArgumentCaptor<NullWritable> valueCaptor = ArgumentCaptor.forClass(NullWritable.class);

        verify(context, times(2)).write(keyCaptor.capture(), valueCaptor.capture());

        CompositeKey capturedKey1 = keyCaptor.getAllValues().get(0);
        NullWritable capturedValue1 = valueCaptor.getAllValues().get(0);
        assertEquals("First write should have the correct CompositeKey", key, capturedKey1);
        assertEquals("First write should have NullWritable", NullWritable.get(), capturedValue1);

        CompositeKey capturedKey2 = keyCaptor.getAllValues().get(1);
        NullWritable capturedValue2 = valueCaptor.getAllValues().get(1);
        assertEquals("Second write should have the correct CompositeKey", key2, capturedKey2);
        assertEquals("Second write should have NullWritable", NullWritable.get(), capturedValue2);

        assertEquals(2, keyCaptor.getAllValues().size());
        assertEquals(2, valueCaptor.getAllValues().size());
    }
}
