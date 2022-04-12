package com.itzjustnico.plertanixplot.main;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private static Main plugin;

    @Override
    public void onEnable() {

        //100x100
        //10 blöcke abstand

        plugin = this;

        listenerRegistration();
        registerCommands();

        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "==================================================================");
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "PlertanixPlot wurde erfolgreich gestartet!");
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "==================================================================");

    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "==================================================================");
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "PlertanixPlot wird heruntergefahren!");
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "==================================================================");
    }


    private void listenerRegistration() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        //pluginManager.registerEvents(new QuitListener(), this);
    }

    private void registerCommands() {
        //getCommand("elements").setExecutor(new ElementsCommand());
    }
    public static Main getPlugin() {
        return plugin;
    }
}
