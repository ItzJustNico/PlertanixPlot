package com.itzjustnico.plertanixplot.plots;

import com.google.gson.TypeAdapter;
import com.itzjustnico.plertanixplot.json.PluginJsonWriter;
import com.itzjustnico.plertanixplot.main.Main;
import com.itzjustnico.plertanixplot.storage.Data;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import redempt.redlib.multiblock.MultiBlockStructure;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PlotHandler {

    private final HashMap<UUID, PlotData> plotData = new HashMap<>();
    private int minX;
    private int maxX;
    private int minZ;
    private int maxZ;
    private int waterY;

    public PlotHandler() {
        File directory = new File(Main.getPlugin().getDataFolder(), "/plots");
        if (!directory.exists()) {
            directory.mkdir();
        }
        for (File file : directory.listFiles()) {
            final PlotData data = (PlotData) new PluginJsonWriter().getDataFromFile(file, PlotData.class);
            plotData.putIfAbsent(data.getId(), data);
        }
    }

    public void createPlotJson(UUID plotOwner, String plotName, int minX, int maxX, int minZ, int maxZ, int waterY) {
        UUID randomUUID = UUID.randomUUID();
        File writeToFile = new File(Main.getPlugin().getDataFolder() +  "/plots", randomUUID.toString() + ".json");
        new PluginJsonWriter().writeDataToFile(new PlotData(writeToFile, randomUUID, plotOwner, plotName, minX, maxX, minZ, maxZ, waterY));
    }

    public void updatePlotJson(PlotData updatedPlotData) {
        File writeToFile = new File(Main.getPlugin().getDataFolder() +  "/plots", updatedPlotData.getId() + ".json");
        if (updatedPlotData.getHomeLocation() != null) {
            new PluginJsonWriter().writeDataToFile(new PlotData(writeToFile, updatedPlotData.getId(), updatedPlotData.getOwner(), updatedPlotData.getTrustedPlayers(), updatedPlotData.getName(), updatedPlotData.getMinX(), updatedPlotData.getMaxX(), updatedPlotData.getMinZ(), updatedPlotData.getMaxZ(), updatedPlotData.getWaterY(), updatedPlotData.getHomeLocation()));
        } else {
            new PluginJsonWriter().writeDataToFile(new PlotData(writeToFile, updatedPlotData.getId(), updatedPlotData.getOwner(), updatedPlotData.getTrustedPlayers(), updatedPlotData.getName(), updatedPlotData.getMinX(), updatedPlotData.getMaxX(), updatedPlotData.getMinZ(), updatedPlotData.getMaxZ(), updatedPlotData.getWaterY()));
        }
        if (plotData.containsKey(updatedPlotData.getId())) {
            plotData.remove(updatedPlotData.getId());
        }
        plotData.putIfAbsent(updatedPlotData.getId(), updatedPlotData);
    }

    //Block is occupied by a plot
    public boolean blockOccupied(Block block) {
        boolean isOccupied = false;
        int blockX = block.getLocation().getBlockX();
        int blockZ = block.getLocation().getBlockZ();

        for (PlotData plotData : plotData.values()) {
            if (blockX <= plotData.getMaxX() && blockX >= plotData.getMinX()) {
                if (blockZ <= plotData.getMaxZ() && blockZ >= plotData.getMinZ()) {
                    isOccupied = true;
                    break;
                }
            }
        }

        return isOccupied;
    }

    //returns 2 Blocks if a plot has been found else an empty array
    public Block[] getNextAvailablePlot() {
        Location root1 = (Location) Data.cfg.get("plots.root.1");
        Location root2 = (Location) Data.cfg.get("plots.root.2");
        Location plotBlock1 = root1.clone();
        Location plotBlock2 = root2.clone();

        Block[] blocks = new Block[2];

        boolean foundPlot = false;
        int maxRuns = 1;
        int offset = (int) Data.cfg.get("plots.plotSideLength") + (int) Data.cfg.get("plots.blocksBetweenPlots");
        while (!foundPlot) {

            //x loop
            for (int i = 0; i < maxRuns; i++) {
                plotBlock1.setX(plotBlock1.getX() + offset);
                plotBlock2.setX(plotBlock2.getX() + offset);
                if (!blockOccupied(plotBlock1.getBlock())) {
                    blocks[0] = plotBlock1.getBlock();
                    blocks[1] = plotBlock2.getBlock();
                    foundPlot = true;
                    break;
                }
            }

            //z loop
            if (!foundPlot) {
                for (int i = 0; i < maxRuns; i++) {
                    plotBlock1.setZ(plotBlock1.getZ() - offset);
                    plotBlock2.setZ(plotBlock2.getZ() - offset);
                    if (!blockOccupied(plotBlock1.getBlock())) {
                        blocks[0] = plotBlock1.getBlock();
                        blocks[1] = plotBlock2.getBlock();
                        foundPlot = true;
                        break;
                    }
                }
            }

            offset = offset*(-1);
            maxRuns++;
            if (maxRuns == 10) {
                break;
            }
        }

        return blocks;
    }

    public boolean placePlotOnNextAvailable(Material material, Player player) {
        Block[] blocks = new PlotHandler().getNextAvailablePlot();
        Block block1 = blocks[0];
        Block block2 = blocks[1];
        if (block1 == null || block2 == null) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "AN ERROR OCCURRED: NO PLOT AVAILABLE");
            player.sendMessage(Data.getPrefix() + "§cEs ist kein Platz für einen weiteren Plot.");
            return false;
        }

        Location placeLocation = new Location(player.getWorld(), 0, 0, 0);
        placeLocation.setY(block1.getY());
        waterY = placeLocation.getBlockY() - 1;
        if (block1.getX() < block2.getX()) {
            placeLocation.setX(block1.getX());
            minX = block1.getX();
            maxX = block2.getX();
        } else {
            placeLocation.setX(block2.getX());
            minX = block2.getX();
            maxX = block1.getX();
        }
        System.out.println(maxX);
        if (block1.getZ() < block2.getZ()) {
            placeLocation.setZ(block1.getZ());
            minZ = block1.getZ();
            maxZ = block2.getZ();
        } else {
            placeLocation.setZ(block2.getZ());
            minZ = block2.getZ();
            maxZ = block1.getZ();
        }

        int squareSize = (int) Data.cfg.get("plots.plotSideLength");
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
        placeBoat(block1, player);
        player.sendMessage(Data.getPrefix() + "§aDer Plot wurde erfolgreich erstellt.");
        player.sendMessage(Data.getPrefix() + "§7Besuche ihn mit §6/P Home§7.");
        return true;
    }

    public void placeBoat(Block block1, Player player) {
        double middleLocX = (minX + (maxX - minX) / 2) -4;
        double middleLocZ = (minZ + (maxZ - minZ) / 2) -7;
        Location middleLoc = new Location(player.getWorld(), middleLocX, block1.getY() - 1, middleLocZ);
        MultiBlockStructure.create(Main.getPlugin().getResource("plotBoat.dat"), "plotBoat").build(middleLoc);
    }

    public int getPlotAmount(Player player) {
        int amount = 0;
        for (PlotData plotData : plotData.values()) {
            if (plotData.getOwner().equals(player.getUniqueId())) {
                amount++;
            }
        }
        return amount;
    }

    public boolean checkIfNameDuplicate(Player player, String plotName) {
        boolean duplicate = false;
        for (PlotData plotData : plotData.values()) {
            if (plotData.getOwner().equals(player.getUniqueId()) && plotData.getName().equals(plotName)) {
                duplicate = true;
            }
        }
        return duplicate;
    }

    public boolean hasPlot(Player player) {
        boolean hasPlot = false;
        for (PlotData plotData : plotData.values()) {
            if (plotData.getOwner().equals(player.getUniqueId())) {
                hasPlot = true;
            }
        }
        return hasPlot;
    }

    public boolean listPlots(Player player) {
        if (hasPlot(player)) {
            player.sendMessage("§8§l---------§r§6 Plots §8§l---------");
            for (PlotData plotData : plotData.values()) {
                if (plotData.getOwner().equals(player.getUniqueId())) {
                    player.sendMessage("§7 - §e" + plotData.getName());
                }
            }
            return true;
        }
        return false;
    }

    public boolean addToTrusted(PlotData plotData, Player trustedPlayer) {
        if (plotData != null) {
            List<UUID> list = plotData.getTrustedPlayers();
            if (!list.contains(trustedPlayer.getUniqueId())) {
                list.add(trustedPlayer.getUniqueId());
                plotData.setTrustedPlayers(list);
                updatePlotJson(plotData);
                return true;
            }
        }
        return false;
    }

    public boolean removeFromTrusted(PlotData plotData, Player trustedPlayer) {
        if (plotData != null) {
            List<UUID> list = plotData.getTrustedPlayers();
            if (list.contains(trustedPlayer.getUniqueId())) {
                list.remove(trustedPlayer.getUniqueId());
                plotData.setTrustedPlayers(list);
                updatePlotJson(plotData);
                return true;
            }
        }
        return false;
    }

    public boolean checkBlockOnPlotBreakable(Player player, Block block) {
        boolean blockBreakable = false;
        int blockX = block.getX();
        int blockY = block.getY();
        int blockZ = block.getZ();
        for (PlotData plotData : plotData.values()) {
            if (plotData.getOwner().equals(player.getUniqueId()) || plotData.getTrustedPlayers().contains(player)) {
                if (blockX < plotData.getMaxX() && blockX > plotData.getMinX()) {
                    if (blockZ < plotData.getMaxZ() && blockZ > plotData.getMinZ()) {
                        if (blockY <= 320) {
                            blockBreakable = true;
                        }
                    }
                }
            }
        }
        return blockBreakable;
    }

    public PlotData getPlot(Player player, String plotName) {
        for (PlotData plotData : plotData.values()) {
            if (plotData.getOwner().equals(player.getUniqueId()) || plotData.getTrustedPlayers().contains(player)) {
                if (plotData.getName().equals(plotName)) {
                    return plotData;
                }
            }
        }
        return null;
    }

    public PlotData getPlotFromBlock(Block block) {
        int blockX = block.getX();
        int blockY = block.getY();
        int blockZ = block.getZ();
        for (PlotData plotData : plotData.values()) {
            if (blockX < plotData.getMaxX() && blockX > plotData.getMinX()) {
                if (blockZ < plotData.getMaxZ() && blockZ > plotData.getMinZ()) {
                    if (blockY <= 320) {
                        return plotData;
                    }
                }
            }
        }
        return null;
    }

    public boolean setHome(PlotData plotData, Location homeLocation) {
        if (plotData != null) {
            if (homeLocation.getBlock().getX() > plotData.getMinX() && homeLocation.getBlock().getX() < plotData.getMaxX()) {
                if (homeLocation.getBlock().getZ() > plotData.getMinZ() && homeLocation.getBlock().getZ() < plotData.getMaxZ()) {
                    plotData.setHomeLocation(homeLocation);
                    updatePlotJson(plotData);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean teleportHome(String plotName, Player player) {
        PlotData plotData = getPlot(player, plotName);
        if (plotData != null) {
            if (plotData.getHomeLocation() != null) {
                player.teleport(plotData.getHomeLocation());
                return true;
            } else {
                player.sendMessage(Data.getPrefix() + "§cDu musst die Home-Location zuerst mit §6/p setHome <PlotName>§c setzten.");
            }
        } else {
            player.sendMessage(Data.getPrefix() + "§cDu hast keinen Plot mit diesem Namen.");
        }
        return false;
    }

    /**
     * Returns true if deleting the File worked correctly.
     * Returns false if the Player doesn't own a plot.
     * @param  plotOwner  the owner of the plot that should be deleted
     * @return      if the delete worked correctly
     */
    public boolean deletePlotJson(Player plotOwner, String plotName) {
        boolean deleteWorked = false;
        PlotData plotData = getPlot(plotOwner, plotName);
        if (plotData != null) {
            File fileToDelete = new File(Main.getPlugin().getDataFolder() +  "/plots", getPlot(plotOwner, plotName).getId() + ".json");
            fileToDelete.delete();
            deleteWorked = true;
        }
        return deleteWorked;
    }

    /**
     * Returns true if deleting the File worked correctly.
     * Returns false if the Player doesn't own a plot.
     * @param  plotData the Plot that is going to be deleted
     * @return      if the delete worked correctly
     */
    public boolean deletePlotJson(PlotData plotData) {
        boolean deleteWorked = false;
        if (plotData != null) {
            File fileToDelete = new File(Main.getPlugin().getDataFolder() +  "/plots", plotData.getId() + ".json");
            if (fileToDelete.delete()) {
                deleteWorked = true;
            }
        }
        return deleteWorked;
    }

    /**
     * Returns true if deleting the Plot worked correctly.
     * Returns false if the Player doesn't own a plot.
     * @param  plotOwner  the owner of the plot which should be deleted
     * @return      if the delete worked correctly
     */
    public boolean deletePlot(Player plotOwner, String plotName) {
        boolean deleteWorked = false;
        PlotData plotData = getPlot(plotOwner, plotName);
        if (plotData != null) {
            int xMax = plotData.getMaxX();
            int xMin = plotData.getMinX();
            int zMax = plotData.getMaxZ();
            int zMin = plotData.getMinZ();
            int yMax = 320;
            int yMin = plotData.getWaterY();
            for (int x = xMax; x >= xMin; x--) {
                for (int z = zMax; z >= zMin; z--) {
                    for (int y = yMax; y >= yMin; y--) {
                        Location location = new Location(plotOwner.getWorld(), x, y, z);
                        if (y == plotData.getWaterY()) {
                            location.getBlock().setType(Material.WATER);
                        } else {
                            location.getBlock().setType(Material.AIR, false);
                        }

                    }
                }
            }
            if (deletePlotJson(plotOwner, plotName)) {
                deleteWorked = true;
            }
        }
        return deleteWorked;
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

    public int getWaterY() {
        return waterY;
    }
}
