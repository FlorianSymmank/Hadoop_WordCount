package de.floriansymmank.utils;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.apache.hadoop.mapreduce.TaskInputOutputContext;
import org.json.JSONObject;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

public class JsonUtilsTest {

    @Mock
    private TaskInputOutputContext<?, ?, ?, ?> context;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testLoadJsonFile() throws IOException, InterruptedException {
        URI[] cacheFiles = {new File("src/test/resources/test.json").toURI()};
        
        when(context.getCacheFiles()).thenReturn(cacheFiles);

        JSONObject jsonObject = JsonUtils.loadJsonFile(context, "test.json");

        assertEquals("value", jsonObject.getString("key"));
    }

    @Test
    public void testLoadJsonFile_FileNotFound() throws IOException, InterruptedException {
        URI[] cacheFiles = {new File("src/test/resources/other.json").toURI()};
        when(context.getCacheFiles()).thenReturn(cacheFiles);

        thrown.expect(IOException.class);
        thrown.expectMessage("File test.json not found in distributed cache.");

        JsonUtils.loadJsonFile(context, "test.json");
    }

    @Test
    public void testLoadJsonFromFile() throws IOException {
        JSONObject jsonObject = JsonUtils.loadJsonFromFile("src/test/resources/test.json");

        assertEquals("value", jsonObject.getString("key"));
    }
}
