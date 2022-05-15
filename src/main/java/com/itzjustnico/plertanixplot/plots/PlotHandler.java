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
import redempt.redlib.multiblock.MultiBlockStructure;

import java.io.File;
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
        deletePlotJson(Bukkit.getPlayer(updatedPlotData.getOwner()), updatedPlotData.getName());
        new PluginJsonWriter().writeDataToFile(updatedPlotData);
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

    //returns 2 Blocks if a plot has been found else an emtpy array
    public Block[] getNextAvailablePlot() {
        Location root1 = (Location) Data.cfg.get("plots.root.1");
        Location root2 = (Location) Data.cfg.get("plots.root.2");
        Location plotBlock1 = root1.clone();
        Location plotBlock2 = root2.clone();

        Block[] blocks = new Block[2];

        boolean foundPlot = false;
        int maxRuns = 1;
        int offset = 25;
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
            if (maxRuns == 5) {
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
        int squareSize = 20;
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
        Location middleLoc = new Location(player.getWorld(), middleLocX, block1.getY(), middleLocZ);
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

    public void listPlots(Player player) {
        player.sendMessage("§7§l---------§r§6Plots-" + player.getName() + "§7§l---------");

        for (PlotData plotData : plotData.values()) {
            if (plotData.getOwner().equals(player.getUniqueId())) {
                player.sendMessage("§7 - §6" + plotData.getName());
            }
        }
    }

    public void addToTrusted(PlotData plotData, Player trustedPlayer) {
        if (plotData != null) {
            List<UUID> list = plotData.getTrustedPlayers();
            list.add(trustedPlayer.getUniqueId());
            plotData.setTrustedPlayers(list);
            updatePlotJson(plotData);
            Bukkit.getPlayer(plotData.getOwner()).sendMessage(Data.getPrefix() + "§aDer Spieler§6 " + trustedPlayer.getName() + "§a wurde eingeladen.");
        }
    }

    public boolean checkBlockOnPlot(Player owner, Block block) {
        boolean blockBreakable = false;
        int blockX = block.getX();
        int blockY = block.getY();
        int blockZ = block.getZ();
        for (PlotData plotData : plotData.values()) {
            if (plotData.getOwner().equals(owner.getUniqueId())) {
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

    public PlotData getPlot(Player owner, String plotName) {
        for (PlotData plotData : plotData.values()) {
            if (plotData.getOwner().equals(owner.getUniqueId()) && plotData.getName().equals(plotName)) {
                return plotData;
            }
        }
        return null;
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
            fileToDelete.delete();
            deleteWorked = true;
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
            int yMin = plotData.getWaterY() + 1;
            for (int x = xMin; x <= xMax; x++) {
                for (int z = zMin; z <= zMax; z++) {
                    for (int y = yMin; y <= yMax; y++) {
                        Location location = new Location(plotOwner.getWorld(), x, y, z);
                        location.getBlock().setType(Material.AIR);
                    }
                }
            }
            if (deletePlotJson(plotOwner, plotName)) {
                deleteWorked = true;
            }
        }
        return deleteWorked;
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

    public int getWaterY() {
        return waterY;
    }
}
