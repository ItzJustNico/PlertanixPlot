package com.itzjustnico.plertanixplot.plots;

import com.itzjustnico.plertanixplot.json.PluginJsonWriter;
import com.itzjustnico.plertanixplot.main.Main;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

public class PlotHandler {

    private final HashMap<UUID, PlotData> plotData = new HashMap<>();
    private final File directory = new File(Main.getPlugin().getDataFolder(), "/plots");

    public PlotHandler() {
        for (File file : directory.listFiles()) {
            final PlotData data = (PlotData) new PluginJsonWriter().getDataFromFile(file, PlotData.class);
            plotData.putIfAbsent(data.getId(), data);
        }
    }

    public void createPlotJson(UUID plotOwner) {
        UUID randomUUID = UUID.randomUUID();
        File writeToFile = new File(Main.getPlugin().getDataFolder() +  "/plots", randomUUID.toString() + ".json");
        new PluginJsonWriter().writeDataToFile(new PlotData(writeToFile, randomUUID, plotOwner));
    }

    //Block is occupied by a plot
    public boolean blockOccupied(Block block) {
        boolean isOccupied = false;
        int blockX = block.getLocation().getBlockX();
        int blockZ = block.getLocation().getBlockZ();

        for (PlotData plotData : plotData.values()) {
            if (blockX <= plotData.getMaxX() || blockX >= plotData.getMinX() || blockZ <= plotData.getMaxZ() || blockZ >= plotData.getMinZ()) {
                isOccupied = true;
            }
        }
        return isOccupied;
    }

    public HashMap<UUID, PlotData> getPlotData() {
        return plotData;
    }
}
