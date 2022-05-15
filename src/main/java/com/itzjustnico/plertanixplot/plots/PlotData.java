package com.itzjustnico.plertanixplot.plots;

import com.itzjustnico.plertanixplot.json.JsonDataFile;

import java.io.File;
import java.net.URI;
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

    public void setTrustedPlayers(List<UUID> trustedPlayers) {
        this.trustedPlayers = trustedPlayers;
    }
}


