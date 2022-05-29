package com.itzjustnico.plertanixplot.plots;

import com.google.gson.TypeAdapter;
import com.itzjustnico.plertanixplot.json.JsonDataFile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlotData extends JsonDataFile {

    private String id;
    private String owner;
    private List<UUID> trustedPlayers;
    private String name;
    private int minX;
    private int maxX;
    private int minZ;
    private int maxZ;
    private int waterY;

    private int homeX;
    private int homeY;
    private int homeZ;
    private float homeYaw;
    private float homePitch;
    private String homeWorldName;


    public PlotData(File file, UUID id, UUID owner, String plotName, int minX, int maxX, int minZ, int maxZ, int waterY) {
        super(file);
        this.id = id.toString();
        this.owner = owner.toString();
        this.name = plotName;
        this.maxX = maxX;
        this.minX = minX;
        this.minZ = minZ;
        this.maxZ = maxZ;
        this.waterY = waterY;
        this.trustedPlayers = new ArrayList<>();
    }

    public PlotData(File file, UUID id, UUID owner, List<UUID> trustedPlayers, String plotName, int minX, int maxX, int minZ, int maxZ, int waterY) {
        super(file);
        this.id = id.toString();
        this.owner = owner.toString();
        this.name = plotName;
        this.maxX = maxX;
        this.minX = minX;
        this.minZ = minZ;
        this.maxZ = maxZ;
        this.waterY = waterY;
        this.trustedPlayers = trustedPlayers;
    }

    public PlotData(File file, UUID id, UUID owner, List<UUID> trustedPlayers, String plotName, int minX, int maxX, int minZ, int maxZ, int waterY, Location homeLocation) {
        super(file);
        this.id = id.toString();
        this.owner = owner.toString();
        this.name = plotName;
        this.maxX = maxX;
        this.minX = minX;
        this.minZ = minZ;
        this.maxZ = maxZ;
        this.waterY = waterY;
        this.trustedPlayers = trustedPlayers;

        this.homeX = homeLocation.getBlockX();
        this.homeY = homeLocation.getBlockY();
        this.homeZ = homeLocation.getBlockZ();
        this.homeYaw = homeLocation.getYaw();
        this.homePitch = homeLocation.getPitch();
        this.homeWorldName = homeLocation.getWorld().getName();
    }

    public PlotData(URI fileUri) {
        super(fileUri);
    }

    public UUID getId() {
        return UUID.fromString(this.id);
    }

    public UUID getOwner() {
        return UUID.fromString(this.owner);
    }

    public String getName() {
        return name;
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

    public List<UUID> getTrustedPlayers() {
        return trustedPlayers;
    }

    public float getHomePitch() {
        return homePitch;
    }

    public float getHomeYaw() {
        return homeYaw;
    }

    public Location getHomeLocation() {
        if (homeWorldName != null && homeX != 0 && homeY != 0 && homeZ != 0 && homeYaw != 0 && homePitch != 0) {
            Location location = new Location(Bukkit.getWorld(homeWorldName), homeX, homeY, homeZ, homeYaw, homePitch);
            return location;
        }
        return null;
    }

    public void setHomeLocation(Location homeLocation) {
        this.homeWorldName = homeLocation.getWorld().getName();
        this.homeX = homeLocation.getBlockX();
        this.homeY = homeLocation.getBlockY();
        this.homeZ = homeLocation.getBlockZ();
        this.homeYaw = homeLocation.getYaw();
        this.homePitch = homeLocation.getPitch();
    }

    public void setTrustedPlayers(List<UUID> trustedPlayers) {
        this.trustedPlayers = trustedPlayers;
    }
}


