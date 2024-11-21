package de.floriansymmank.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;

import org.apache.hadoop.mapreduce.TaskInputOutputContext;
import org.json.JSONObject;

public class JsonUtils {

    /**
     * Loads a JSON file from the distributed cache and returns a JSONObject.
     *
     * @param context  The Hadoop Context (Mapper or Reducer).
     * @param filename The name of the JSON file added to the distributed cache.
     * @return JSONObject representing the JSON data.
     * @throws IOException          If an I/O error occurs.
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
                if (cacheFileName.equals(filename)) {
                    // Load the JSON file and return the JSONObject
                    return loadJsonFromFile(file.getPath());
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
    static JSONObject loadJsonFromFile(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
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
}