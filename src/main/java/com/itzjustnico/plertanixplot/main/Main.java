package com.itzjustnico.plertanixplot.main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.itzjustnico.plertanixplot.commands.PlotCommand;
import com.itzjustnico.plertanixplot.listener.BlockBreakListener;
import com.itzjustnico.plertanixplot.listener.BlockPlaceListener;
import com.itzjustnico.plertanixplot.storage.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private static Main plugin;
    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public void onEnable() {

        /*
        TODO: Hinteleportieren -> eventuell home standard mäßig setzten DONE
        TODO: Invites
        TODO: Deletes       DONE
        TODO: Info gesetzt datum
        TODO: Zuletzt online
         */


        plugin = this;

        listenerRegistration();
        registerCommands();
        new ConfigManager().registerConfig();

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
        pluginManager.registerEvents(new BlockBreakListener(), this);
        pluginManager.registerEvents(new BlockPlaceListener(), this);
    }

    private void registerCommands() {
        getCommand("p").setExecutor(new PlotCommand());
    }
    public static Main getPlugin() {
        return plugin;
    }
}
