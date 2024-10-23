package com.fp.basecalc;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ReadWrite {
    public static List<CalculationEntry> read(Context context, String fileName) {
        List<CalculationEntry> entries = new ArrayList<>();
        try {
            FileInputStream fis = context.openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }

            // Parse the JSON data
            JSONObject jsonObject = new JSONObject(sb.toString());
            JSONArray jsonArray = jsonObject.getJSONArray("entries"); // Assuming the top-level key is "entries"
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject entryObject = jsonArray.getJSONObject(i);
                long timestamp = entryObject.getLong("timestamp");
                String calculation = entryObject.getString("calculation");
                entries.add(new CalculationEntry(timestamp, calculation));
            }
        } catch (FileNotFoundException fileNotFound) {
            return null;
        } catch (IOException ioException) {
            return null;
        } catch (JSONException jsonException) {
            return null;
        }
        return entries;
    }

    public static boolean create(Context context, String fileName, List<CalculationEntry> entries) {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        try {
            for (CalculationEntry entry : entries) {
                JSONObject entryObject = new JSONObject();
                entryObject.put("timestamp", entry.getTimestamp());
                entryObject.put("calculation", entry.getCalculation());
                jsonArray.put(entryObject);
            }
            jsonObject.put("entries", jsonArray); // Wrap in a top-level key "entries"

            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(jsonObject.toString().getBytes());
            fos.close();
            return true;
        } catch (FileNotFoundException fileNotFound) {
            return false;
        } catch (IOException ioException) {
            return false;
        } catch (JSONException jsonException) {
            return false;
        }
    }

    public static boolean isFilePresent(Context context, String fileName) {
        String path = context.getFilesDir().getAbsolutePath() + "/" + fileName;
        File file = new File(path);
        return file.exists();
    }

    public static boolean write(Context context, String fileName, CalculationEntry newEntry) {
        List<CalculationEntry> entries = read(context, fileName);
        if (entries == null) {
            entries = new ArrayList<>();
        }
        entries.add(newEntry); // Add the new entry

        return create(context, fileName, entries); // Save all entries back to the file
    }

//    to read and write
//    List<CalculationEntry> calculations;
//    if (ReadWrite.isFilePresent(this, "storage.json")) {
//        calculations = ReadWrite.read(this, "storage.json");
//        // Process the calculations
//    } else {
//        List<CalculationEntry> initialEntries = new ArrayList<>();
//        initialEntries.add(new CalculationEntry(System.currentTimeMillis(), "Initial calculation"));
//        boolean isFileCreated = ReadWrite.create(this, "storage.json", initialEntries);
//        // Proceed with storing or showing the UI
//    }
}
