package com.itzjustnico.plertanixplot.storage;

import java.io.IOException;

public class ConfigManager {

    public void registerConfig() {
        if (Data.cfg.get("plots.maxPlotAmount") == null) {
            Data.cfg.set("plots.maxPlotAmount", 1);
        }

        try {
            Data.cfg.save(Data.file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
