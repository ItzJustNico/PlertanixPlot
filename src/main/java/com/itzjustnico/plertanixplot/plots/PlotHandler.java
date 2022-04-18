package com.itzjustnico.plertanixplot.plots;

import com.itzjustnico.plertanixplot.json.PluginJsonWriter;
import com.itzjustnico.plertanixplot.main.Main;
import com.itzjustnico.plertanixplot.storage.Data;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

public class PlotHandler {

    private final HashMap<UUID, PlotData> plotData = new HashMap<>();
    private final File directory = new File(Main.getPlugin().getDataFolder(), "/plots");
    private int minX;
    private int maxX;
    private int minZ;
    private int maxZ;

    public PlotHandler() {
        for (File file : directory.listFiles()) {
            final PlotData data = (PlotData) new PluginJsonWriter().getDataFromFile(file, PlotData.class);
            plotData.putIfAbsent(data.getId(), data);
        }
    }

    public void createPlotJson(UUID plotOwner, int minX, int maxX, int minZ, int maxZ) {
        UUID randomUUID = UUID.randomUUID();
        File writeToFile = new File(Main.getPlugin().getDataFolder() +  "/plots", randomUUID.toString() + ".json");
        new PluginJsonWriter().writeDataToFile(new PlotData(writeToFile, randomUUID, plotOwner, minX, maxX, minZ, maxZ));
    }

    //Block is occupied by a plot
    public boolean blockOccupied(Block block) {
        boolean isOccupied = false;
        int blockX = block.getLocation().getBlockX();
        int blockZ = block.getLocation().getBlockZ();

        System.out.println("Values:");
        System.out.println(plotData.values());
        for (PlotData plotData : plotData.values()) {
            System.out.println(blockX);
            System.out.println(plotData.getMaxX());
            System.out.println(plotData.getMinX());
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

        System.out.println(plotBlock1);
        System.out.println(plotBlock2);

        Block[] blocks = new Block[2];

        boolean foundPlot = false;
        int maxRuns = 1;
        int offset = 7;
        while (!foundPlot) {
            System.out.println(maxRuns);
            System.out.println(offset);
            //x loop
            for (int i = 0; i < maxRuns; i++) {
                plotBlock1.setX(root1.getX() + offset);
                plotBlock2.setX(root2.getX() + offset);
                System.out.println(plotBlock1);
                System.out.println(plotBlock2);
                if (blockOccupied(plotBlock1.getBlock())) {
                    System.out.println("Is occupied");
                } else {
                    System.out.println("not Occupied");
                    blocks[0] = plotBlock1.getBlock();
                    blocks[1] = plotBlock2.getBlock();
                    foundPlot = true;
                }
            }
            //z loop
            if (foundPlot == false) {
                for (int i = 0; i < maxRuns; i++) {
                    plotBlock1.setZ(root1.getZ() - offset);
                    plotBlock2.setZ(root2.getZ() - offset);
                    if (!blockOccupied(plotBlock1.getBlock()) && !blockOccupied(plotBlock2.getBlock())) {
                        blocks[0] = plotBlock1.getBlock();
                        blocks[1] = plotBlock2.getBlock();
                        foundPlot = true;
                    }
                }
            }

            offset = offset*(-1);
            maxRuns++;
            if (maxRuns == 5) {
                break;
            }
        }
        return blocks;
    }

    public void placePlotOnNextAvailable(Material material, Player player) {
        Block[] blocks = new PlotHandler().getNextAvailablePlot();
        Block block1 = blocks[0];
        Block block2 = blocks[1];
        if (block1 == null || block2 == null) {
            System.out.println( ChatColor.RED + "AN ERROR OCCURRED: NO PLOT AVAILABLE");
            player.sendMessage(Data.getPrefix() + "§cEs ist kein Platz für einen weiteren Plot.");
            return;
        }

        Location placeLocation = new Location(Bukkit.getWorld("world"), 0, 0, 0);
        placeLocation.setY(block1.getY());
        if (block1.getX() < block2.getX()) {
            placeLocation.setX(block1.getX());
            minX = block1.getX();
            maxX = block2.getX();
        } else {
            placeLocation.setX(block2.getX());
            minX = block2.getX();
            maxX = block1.getX();
        }
        if (block1.getZ() < block2.getZ()) {
            placeLocation.setZ(block1.getZ());
            minZ = block1.getZ();
            maxZ = block2.getZ();
        } else {
            placeLocation.setZ(block2.getZ());
            minZ = block2.getZ();
            maxZ = block1.getZ();
        }

        System.out.println(placeLocation);
        int squareSize = 5;
        placeLocation.getBlock().setType(material);
        for (int i = 1; i < squareSize; i++) {
            placeLocation.setX(placeLocation.getX() + 1);
            placeLocation.getBlock().setType(material);
        }
        for (int i = 1; i < squareSize; i++) {
            placeLocation.setZ(placeLocation.getZ() + 1);
            placeLocation.getBlock().setType(material);
        }
        for (int i = 1; i < squareSize; i++) {
            placeLocation.setX(placeLocation.getX() - 1);
            placeLocation.getBlock().setType(material);
        }
        for (int i = 1; i < squareSize; i++) {
            placeLocation.setZ(placeLocation.getZ() - 1);
            placeLocation.getBlock().setType(material);
        }


        player.sendMessage(Data.getPrefix() + "§aDer Plot wurde erfolgreich erstellt.");
        player.sendMessage(Data.getPrefix() + "§7Besuche ihn mit §6/P Home§7.");
    }

    public HashMap<UUID, PlotData> getPlotData() {
        return plotData;
    }

    public int getMinX() {
        return minX;
    }

    public int getMaxX() {
        return maxX;
    }

    public int getMinZ() {
        return minZ;
    }

    public int getMaxZ() {
        return maxZ;
    }
}
