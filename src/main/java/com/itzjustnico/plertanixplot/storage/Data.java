package com.itzjustnico.plertanixplot.storage;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Map;
import java.util.Objects;

public class Data {

    public static File file = new File("plugins//PlertanixPlot//settings.yml");
    public static YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);

    private static final String noPermission = "§cDazu hast du keine Rechte!";
    private static final String noPlayer = "§cDiesen Command dürfen nur Spieler ausführen";
    private static final String prefix = "§l§6[§bB§aB§cO§6] ";

    public static String getNoPermission() {
        return noPermission;
    }

    public static String getNoPlayer() {
        return noPlayer;
    }

    public static String getPrefix() {
        return prefix;
    }
}
