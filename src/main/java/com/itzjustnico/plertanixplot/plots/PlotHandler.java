package com.itzjustnico.plertanixplot.plots;

import com.itzjustnico.plertanixplot.json.PluginJsonWriter;
import com.itzjustnico.plertanixplot.main.Main;
import com.itzjustnico.plertanixplot.storage.Data;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
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

    //returns 2 Blocks if a plot has been found else an emtpy array
    public Block[] getNextAvailablePlot() {
        Location root1 = (Location) Data.cfg.get("plots.root.1");
        Location root2 = (Location) Data.cfg.get("plots.root.2");
        Location plotBlock1 = root1.clone();
        Location plotBlock2 = root2.clone();

        Block[] blocks = new Block[2];

        boolean foundPlot = false;
        while (!foundPlot) {
            int offset = 7;

            int ii = 1;
            //x loop
            for (int i = 0; i < ii; i++) {
                plotBlock1.setX(root1.getX() + offset);
                plotBlock2.setX(root2.getX() + offset);
                if (!blockOccupied(plotBlock1.getBlock()) && !blockOccupied(plotBlock2.getBlock())) {
                    blocks[0] = plotBlock1.getBlock();
                    blocks[1] = plotBlock2.getBlock();
                    foundPlot = true;
                }
            }
            //z loop
            for (int i = 0; i < ii; i++) {
                plotBlock1.setZ(root1.getZ() - offset);
                plotBlock2.setZ(root2.getZ() - offset);
                if (!blockOccupied(plotBlock1.getBlock()) && !blockOccupied(plotBlock2.getBlock())) {
                    blocks[0] = plotBlock1.getBlock();
                    blocks[1] = plotBlock2.getBlock();
                    foundPlot = true;
                }
            }

            offset = offset*(-1);
            ii++;
            if (ii == 5) {
                break;
            }
        }
        return blocks;
    }

    public void placePlotOnNextAvailable(Material material) {
        Block[] blocks = new PlotHandler().getNextAvailablePlot();
        if (blocks.length == 0) {
            System.out.println("§cAN ERROR OCCURRED: NO PLOT AVAILABLE");
            return;
        }
        Block block1 = blocks[0];
        Block block2 = blocks[1];

        Location placeBlockLocation = new Location(Bukkit.getWorld("world"), 0, 0, 0);
        placeBlockLocation.setY(block1.getY());
        if (block1.getX() < block2.getX()) {
            placeBlockLocation.setX(block1.getX());
        } else {
            placeBlockLocation.setX(block2.getX());
        }
        if (block1.getZ() < block2.getZ()) {
            placeBlockLocation.setZ(block1.getZ());
        } else {
            placeBlockLocation.setZ(block2.getZ());
        }

        int squareSize = 5;
        for (int i = 0; i <= squareSize; i++) {
            placeBlockLocation.setX(placeBlockLocation.getX() + i);
            placeBlockLocation.getBlock().setType(material);
        }
        for (int i = 0; i <= squareSize; i++) {
            placeBlockLocation.setZ(placeBlockLocation.getZ() + i);
            placeBlockLocation.getBlock().setType(material);
        }
        for (int i = 0; i <= squareSize; i++) {
            placeBlockLocation.setZ(placeBlockLocation.getZ() - i);
            placeBlockLocation.getBlock().setType(material);
        }
        for (int i = 0; i <= squareSize; i++) {
            placeBlockLocation.setX(placeBlockLocation.getX() - i);
            placeBlockLocation.getBlock().setType(material);
        }
    }

    public HashMap<UUID, PlotData> getPlotData() {
        return plotData;
    }
}
