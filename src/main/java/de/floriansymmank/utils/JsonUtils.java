package de.floriansymmank.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.mapreduce.TaskInputOutputContext;
import org.json.JSONArray;
import org.json.JSONObject;

public class JsonUtils {

    /**
     * Loads a JSON file from the distributed cache and returns a JSONObject.
     *
     * @param context The Hadoop Context (Mapper or Reducer).
     * @param filename The name of the JSON file added to the distributed cache.
     * @return JSONObject representing the JSON data.
     * @throws IOException If an I/O error occurs.
     * @throws InterruptedException If the context is interrupted.
     */
    public static JSONObject loadJsonFile(TaskInputOutputContext<?, ?, ?, ?> context, String filename)
            throws IOException, InterruptedException {

        // Get the cached files
        URI[] cacheFiles = context.getCacheFiles();

        if (cacheFiles != null) {
            for (URI cacheFile : cacheFiles) {
                // Get the file name from the URI
                File file = new File(cacheFile.getPath());
                String cacheFileName = file.getName();

                // Check if this is the file we are looking for
                if (cacheFileName.equals(new File(filename).getName())) {
                    // Load the JSON file and return the JSONObject
                    return loadJsonFromFile(cacheFileName);
                }
            }
        }

        throw new IOException("File " + filename + " not found in distributed cache.");
    }

    /**
     * Loads a JSON file from the given path and returns a JSONObject.
     *
     * @param filePath The path to the JSON file.
     * @return JSONObject representing the JSON data.
     * @throws IOException If an I/O error occurs.
     */
    public static JSONObject loadJsonFromFile(String fileName) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), StandardCharsets.UTF_8));
        StringBuilder jsonContent = new StringBuilder();
        String line;

        try {
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }
        } finally {
            reader.close();
        }

        return new JSONObject(jsonContent.toString());
    }

    /**
     * Converts a JSONObject to a Map<String, List<String>>.
     *
     * @param jsonObject The JSONObject to convert.
     * @return Map<String, List<String>> representing the JSON data.
     */
    public static Map<String, List<String>> convertJsonToMap(JSONObject jsonObject) {
        Map<String, List<String>> resultMap = new HashMap<>();

        for (String key : jsonObject.keySet()) {
            // Get the JSONArray associated with the key
            JSONArray jsonArray = jsonObject.getJSONArray(key);

            // Convert JSONArray to List<String>
            List<String> list = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                list.add(jsonArray.getString(i));
            }

            // Put the key and list into the map
            resultMap.put(key, list);
        }

        return resultMap;
    }
}
