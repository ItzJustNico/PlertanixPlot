package com.itzjustnico.plertanixplot.plots;

import com.google.gson.TypeAdapter;
import com.itzjustnico.plertanixplot.json.PluginJsonWriter;
import com.itzjustnico.plertanixplot.main.Main;
import com.itzjustnico.plertanixplot.storage.Data;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
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

    private boolean isPlotFree(Location block1, Location block2) {
        double yMax = 200;
        double yMin = block1.getY();
        double xMin;
        double xMax;
        double zMin;
        double zMax;

        if (block1.getX() < block2.getX()) {
            xMin = block1.getX();
            xMax = block2.getX();
        } else {
            xMin = block2.getX();
            xMax = block1.getX();
        }
        if (block1.getZ() < block2.getZ()) {
            zMin = block1.getZ();
            zMax = block2.getZ();
        } else {
            zMin = block2.getZ();
            zMax = block1.getZ();
        }

            for (double x = xMax; x >= xMin; x--) {
                for (double z = zMax; z >= zMin; z--) {
                    for (double y = yMax; y >= yMin; y--) {
                        Location location = (Location) block1.getWorld();
                        if (!location.getBlock().getType().isAir()) {
                            System.out.println("Block in the way, loc: " + location);
                            return false;
                        }
                    }
                }
            }
        return true;
    }

    //returns 2 Blocks if a plot has been found else an empty array
    public Block[] getNextAvailablePlot() {
        Location root1 = (Location) Data.cfg.get("plots.root.1");
        Location root2 = (Location) Data.cfg.get("plots.root.2");
        Location plotBlock1 = root1.clone();
        Location plotBlock2 = root2.clone();
        System.out.println("root" + plotBlock1);
        System.out.println("root" + plotBlock2);


        Block[] blocks = new Block[2];

        boolean foundPlot = false;
        int maxRuns = 1;
        int offset = (int) Data.cfg.get("plots.plotSideLength") + (int) Data.cfg.get("plots.blocksBetweenPlots");
        while (!foundPlot) {

            //x loop
            for (int i = 0; i < maxRuns; i++) {
                plotBlock1.setX(plotBlock1.getX() + offset);
                plotBlock2.setX(plotBlock2.getX() + offset);
                System.out.println(plotBlock1);
                System.out.println(plotBlock2);
                if (!blockOccupied(plotBlock1.getBlock())) {
                   if (isPlotFree(plotBlock1, plotBlock2)) {
                        blocks[0] = plotBlock1.getBlock();
                        blocks[1] = plotBlock2.getBlock();
                        foundPlot = true;
                        break;
                    }
                }
            }

            //z loop
            if (!foundPlot) {
                for (int i = 0; i < maxRuns; i++) {
                    plotBlock1.setZ(plotBlock1.getZ() - offset);
                    plotBlock2.setZ(plotBlock2.getZ() - offset);
                    if (!blockOccupied(plotBlock1.getBlock())) {
                        if (isPlotFree(plotBlock1, plotBlock2)) {
                            blocks[0] = plotBlock1.getBlock();
                            blocks[1] = plotBlock2.getBlock();
                            foundPlot = true;
                            break;
                        }
                    }
                }
            }

            offset = offset*(-1);
            maxRuns++;
            System.out.println(maxRuns);
            if (maxRuns == 10) {
                System.out.println("break");
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
        Location chestLoc = new Location(player.getWorld(), middleLocX + 4, middleLoc.getBlockY() + 1, middleLocZ + 9);
        Chest chest = (Chest) chestLoc.getBlock().getState();
        chest.setCustomName("§9Starterkiste");
        chest.getBlockInventory().setItem(0, new ItemStack(Material.FISHING_ROD));
        chest.getBlockInventory().setItem(1, new ItemStack(Material.STONE_SHOVEL));
        chest.getBlockInventory().setItem(2, new ItemStack(Material.STONE_PICKAXE));
        chest.getBlockInventory().setItem(3, new ItemStack(Material.STONE_AXE));
        chest.getBlockInventory().setItem(4, new ItemStack(Material.STONE_HOE));
        chest.getBlockInventory().setItem(5, new ItemStack(Material.COMPASS));
        chest.getBlockInventory().setItem(6, new ItemStack(Material.LILY_PAD,5));
        chest.getBlockInventory().setItem(7, new ItemStack(Material.BIRCH_SAPLING, 2));
        chest.getBlockInventory().setItem(8, new ItemStack(Material.OAK_SAPLING, 2));
        chest.getBlockInventory().setItem(9, new ItemStack(Material.SPYGLASS));
        chest.getBlockInventory().setItem(11, new ItemStack(Material.STICK, 6));
        chest.getBlockInventory().setItem(12, new ItemStack(Material.OAK_BOAT));
        ItemStack potion = new ItemStack(Material.POTION);
        PotionMeta meta = (PotionMeta)potion.getItemMeta();
        meta.setColor(Color.BLUE);
        meta.addCustomEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 180 * 20, 0), true);
        meta.setDisplayName("§9Potion of Water Breathing");
        potion.setItemMeta((ItemMeta)meta);

        chest.getBlockInventory().setItem(14, potion);
        chest.getBlockInventory().setItem(16, new ItemStack(Material.GRASS_BLOCK, 4));
        chest.getBlockInventory().setItem(17, new ItemStack(Material.DIRT, 4));
        chest.getBlockInventory().setItem(18, new ItemStack(Material.BONE, 5));
        chest.getBlockInventory().setItem(19, new ItemStack(Material.COAL, 4));
        chest.getBlockInventory().setItem(21, new ItemStack(Material.WHEAT_SEEDS, 2));
        chest.getBlockInventory().setItem(22, new ItemStack(Material.BREAD));
        chest.getBlockInventory().setItem(23, new ItemStack(Material.SALMON, 2));
        chest.getBlockInventory().setItem(24, new ItemStack(Material.APPLE, 2));
        chest.getBlockInventory().setItem(26, new ItemStack(Material.TORCH, 6));
        Location signLoc = new Location(player.getWorld(), chestLoc.getBlockX(), chestLoc.getBlockY() + 1, chestLoc.getBlockZ());
        signLoc.getBlock().setType(Material.AIR);
        //Sign sign = (Sign) signLoc.getBlock().getState();

        //signLoc.getBlock().getRelative(BlockFace.WEST).setType(Material.OAK_WALL_SIGN);

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
            System.out.println(list);
            if (!list.contains(trustedPlayer.getUniqueId())) {
                list.add(trustedPlayer.getUniqueId());
                plotData.setTrustedPlayers(list);
                System.out.println(list);
                System.out.println(plotData.getTrustedPlayers());
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
