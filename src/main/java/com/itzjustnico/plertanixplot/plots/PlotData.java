package com.itzjustnico.plertanixplot.plots;

import com.itzjustnico.plertanixplot.json.JsonDataFile;

import java.io.File;
import java.net.URI;
import java.util.UUID;

public class PlotData extends JsonDataFile {

    private String owner;
    private String id;
    private int minX;
    private int maxX;
    private int minZ;
    private int maxZ;

    public PlotData(File file, UUID id, UUID owner) {
        super(file);
        this.owner = owner.toString();
        this.id = id.toString();
    }

    public PlotData(URI fileUri) {
        super(fileUri);
    }

    public UUID getOwner() {
        return UUID.fromString(this.owner);
    }

    public UUID getId() {
        return UUID.fromString(this.id);
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
