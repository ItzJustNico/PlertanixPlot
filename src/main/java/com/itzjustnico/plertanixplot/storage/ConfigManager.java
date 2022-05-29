package com.itzjustnico.plertanixplot.storage;

import java.io.IOException;

public class ConfigManager {

    public void registerConfig() {
        if (Data.cfg.get("plots.maxPlotAmount") == null) {
            Data.cfg.set("plots.maxPlotAmount", 1);
        }

        if (Data.cfg.get("plots.plotSideLength") == null) {
            Data.cfg.set("plots.plotSideLength", 0);
        }
        if (Data.cfg.get("plots.blocksBetweenPlots") == null) {
            Data.cfg.set("plots.blocksBetweenPlots", 0);
        }

        try {
            Data.cfg.save(Data.file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
