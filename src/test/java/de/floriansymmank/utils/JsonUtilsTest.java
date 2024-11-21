package de.floriansymmank.utils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.mapreduce.TaskInputOutputContext;
import org.json.JSONException;
import org.json.JSONObject;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
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

        // {
        //     "key": "value"
        // }
        URI[] cacheFiles = {new File("src/test/resources/test.json").toURI()};

        when(context.getCacheFiles()).thenReturn(cacheFiles);

        JSONObject jsonObject = JsonUtils.loadJsonFile(context, "test.json");

        assertEquals("value", jsonObject.getString("key"));
    }

    @Test
    public void testLoadJsonFileWithDict() throws IOException, InterruptedException {

        // {
        //     "key1": [
        //         "value1",
        //         "value2"
        //     ],
        //     "key2": [
        //         "value3",
        //         "value4"
        //     ]
        // }
        URI[] cacheFiles = {new File("src/test/resources/dict.json").toURI()};

        when(context.getCacheFiles()).thenReturn(cacheFiles);

        JSONObject jsonObject = JsonUtils.loadJsonFile(context, "dict.json");

        assertEquals("value1", jsonObject.getJSONArray("key1").getString(0));
        assertEquals("value2", jsonObject.getJSONArray("key1").getString(1));
        assertEquals("value3", jsonObject.getJSONArray("key2").getString(0));
        assertEquals("value4", jsonObject.getJSONArray("key2").getString(1));
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

    @Test
    public void testConvertJsonToMap_NormalCase() throws IOException {
        // JSON-Inhalt: {"key1": ["value1", "value2"], "key2": ["value3", "value4"]}
        String jsonString = "{"
                + "\"key1\": [\"value1\", \"value2\"],"
                + "\"key2\": [\"value3\", \"value4\"]"
                + "}";

        JSONObject jsonObject = new JSONObject(jsonString);

        // Aufruf der Methode
        Map<String, List<String>> resultMap = JsonUtils.convertJsonToMap(jsonObject);

        // Erwartete Map erstellen
        Map<String, List<String>> expectedMap = new HashMap<>();
        expectedMap.put("key1", Arrays.asList("value1", "value2"));
        expectedMap.put("key2", Arrays.asList("value3", "value4"));

        // Überprüfung
        assertEquals(expectedMap, resultMap);
    }

    @Test
    public void testConvertJsonToMap_EmptyJson() throws IOException {
        JSONObject jsonObject = new JSONObject("{}");

        Map<String, List<String>> resultMap = JsonUtils.convertJsonToMap(jsonObject);

        assertTrue(resultMap.isEmpty());
    }

    @Test(expected = JSONException.class)
    public void testConvertJsonToMap_NonStringValues() throws IOException {
        // JSON-Inhalt: {"numbers": [1, 2, 3], "booleans": [true, false]}
        String jsonString = "{"
                + "\"numbers\": [1, 2, 3],"
                + "\"booleans\": [true, false]"
                + "}";

        JSONObject jsonObject = new JSONObject(jsonString);

        Map<String, List<String>> resultMap = JsonUtils.convertJsonToMap(jsonObject);
    }

    @Test(expected = JSONException.class)
    public void testConvertJsonToMap_InvalidJson() throws IOException {
        // Ungültiger JSON-Inhalt
        String jsonString = "{ invalid json }";

        JSONObject jsonObject = new JSONObject(jsonString);

        // Sollte eine JSONException auslösen
        JsonUtils.convertJsonToMap(jsonObject);
    }

    @Test(expected = JSONException.class)
    public void testConvertJsonToMap_WrongValueType() throws IOException {
        // JSON-Inhalt mit falschem Werttyp
        // {"key1": "not an array", "key2": ["value3", "value4"]}
        String jsonString = "{"
                + "\"key1\": \"not an array\","
                + "\"key2\": [\"value3\", \"value4\"]"
                + "}";

        JSONObject jsonObject = new JSONObject(jsonString);

        // Sollte eine JSONException auslösen, da "key1" kein JSONArray ist
        JsonUtils.convertJsonToMap(jsonObject);
    }

    @Test
    public void testConvertJsonToMap_NestedArrays() throws IOException {
        // JSON-Inhalt mit verschachtelten Arrays
        String jsonString = "{"
                + "\"key1\": [[\"nested1\", \"nested2\"], \"value2\"],"
                + "\"key2\": [\"value3\", \"value4\"]"
                + "}";

        JSONObject jsonObject = new JSONObject(jsonString);

        try {
            JsonUtils.convertJsonToMap(jsonObject);
            fail("Erwartete JSONException aufgrund verschachtelter Arrays");
        } catch (JSONException e) {
            // Test erfolgreich, da eine JSONException erwartet wurde
        }
    }
}
