package com.itzjustnico.plertanixplot.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PluginJsonWriter {

    private final Gson gson;
    public PluginJsonWriter() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public File createFile(final JsonDataFile dataFile) {
        final File file = dataFile.getFile();
        if (!file.exists()) {
            try {
                if (!file.getParentFile().exists())
                    if (file.getParentFile().mkdirs()){
                        System.out.println("&aAPI :: Successfully created directories");
                    }
                if (file.createNewFile()) {
                    System.out.println("&aAPI :: Successfully created a DataFile!");
                    String jsonString = this.gson.toJson(dataFile);
                    Writer writer = new FileWriter(file);
                    writer.append(jsonString);
                    writer.close();
                    System.out.println("&aAPI :: Successfully wrote default data for DataFile");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return file;
    }

    public void writeDataToFile(final JsonDataFile config) {
        File file = config.getFile();
        if (!file.exists()) file = this.createFile(config);

        try {
            final Writer writer = new FileWriter(file);
            this.gson.toJson(config, writer);
            writer.flush();
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public JsonDataFile getDataFromFile(final File file, Class<? extends JsonDataFile> clazz) {
        if (!file.exists()) return null;
        try {
            return this.gson.fromJson(Files.newBufferedReader(Paths.get(file.toURI())), clazz);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
