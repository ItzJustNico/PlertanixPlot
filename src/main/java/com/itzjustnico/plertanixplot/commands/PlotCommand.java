package com.itzjustnico.plertanixplot.commands;

import com.itzjustnico.plertanixplot.main.Main;
import com.itzjustnico.plertanixplot.plots.PlotHandler;
import com.itzjustnico.plertanixplot.storage.Data;
import com.itzjustnico.plertanixplot.plots.PlotData;
import com.itzjustnico.plertanixplot.json.PluginJsonWriter;
import com.itzjustnico.plertanixplot.storage.Math;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import redempt.redlib.multiblock.MultiBlockStructure;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

public class PlotCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        //p create <plotName>
        //p delete <plotName>
        //p delete <plotName> <Owner>
        //p invite <Player> <PlotName>
        //p remove <Player>
        //p home
        //p info
        //p list <Player>

        //p root

        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (cmd.getName().equalsIgnoreCase("p")) {

                if (args.length == 1) {
                    //create Plot on Map
                    //wo?
                    //ausgangspunkt -> mitte 1. plot oder so
                    //spiralenförmig rundherum
                    // -> nächsten platz suchen
                    //plot platzieren -> für test concrete
                    //lily pads platzieren
                    //Bot laden + platzieren

                    //add Plot + Player + cords in yml
                    //Plot speichern:

                    //List of Plots
                    //Plot1
                    //min/max X
                    //min/max Z
                    //owner Player
                    //trusted Players
                    //Plot2
                    //min/max X
                    //min/max Z
                    //owner Player
                    //trusted Players
                    if (args[0].equalsIgnoreCase("list")) {
                        new PlotHandler().listPlots(player);
                    } else if (args[0].equalsIgnoreCase("help")) {
                        sendCommands(player);
                    } else {
                        sendHelp(player);
                    }

                } else if (args.length == 2) {
                    if (args[0].equalsIgnoreCase("create")) {
                        PlotHandler plotHandler = new PlotHandler();
                        if (plotHandler.getPlotAmount(player) < (int) Data.cfg.get("plots.maxPlotAmount") || player.hasPermission("morePlots")) {
                            if (!plotHandler.checkIfNameDuplicate(player, args[1])) {
                                if (plotHandler.placePlotOnNextAvailable(Material.LILY_PAD, player)) {
                                    //create Plot json
                                    new PlotHandler().createPlotJson(player.getUniqueId(), args[1], plotHandler.getMinX(), plotHandler.getMaxX(), plotHandler.getMinZ(), plotHandler.getMaxZ(), plotHandler.getWaterY());
                                    Bukkit.getConsoleSender().sendMessage(Data.getPrefix() + ChatColor.GREEN + "A new Plot has been created!");
                                }
                            } else {
                                player.sendMessage(Data.getPrefix() + "§cDu kannst diesen Namen nicht nochmal verwenden!");
                            }
                        } else {
                            player.sendMessage(Data.getPrefix() + "§cDu hast bereits die maximale Anzahl an Plots erreicht!");
                        }

                    } else if (args[0].equalsIgnoreCase("delete")) {
                        PlotHandler plotHandler = new PlotHandler();
                        if (plotHandler.deletePlot(player, args[1])) {
                            player.sendMessage(Data.getPrefix() + "§aDein Plot §6\"" + args[1] + "\" §awurde erfolgreich gelöscht!");
                        } else {
                            player.sendMessage(Data.getPrefix() + "§cDu besitzt keinen Plot namens §6\"" + args[1] + "\"§c!");
                        }
                    } else if (args[0].equalsIgnoreCase("list")) {
                        Player target = Bukkit.getPlayer(args[1]);
                        if (target != null) {
                            new PlotHandler().listPlots(target);
                        }
                    } else if (args[0].equalsIgnoreCase("root")) {
                        if (Math.isInt(args[1])) {
                            int number = Integer.parseInt(args[1]);
                            if (number == 1 || number == 2) {
                                Data.cfg.set("plots.root." + number, player.getTargetBlock((Set<Material>) null, 5).getLocation());
                                try {
                                    Data.cfg.save(Data.file);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                player.sendMessage(Data.getPrefix() + "§aDie §6" + number + ". §aEcke wurde gesetzt.");
                            } else {
                                player.sendMessage(Data.getPrefix() + "§cBitte verwende die Zahl 1 oder 2");
                            }
                        } else {
                            player.sendMessage(Data.getPrefix() + "§cBitte verwende die Zahl 1 oder 2");
                        }

                    } else if (args[0].equalsIgnoreCase("help")) {
                        sendCommands(player);
                    } else {
                        sendHelp(player);
                    }
                } else if (args.length == 3) {
                    if (args[0].equalsIgnoreCase("delete")) {
                        PlotHandler plotHandler = new PlotHandler();
                        Player target = Bukkit.getPlayer(args[2]);
                        if (target != null) {
                            if (plotHandler.deletePlot(target, args[1])) {
                                player.sendMessage(Data.getPrefix() + "§aDer Plot §6\"" + args[1] + "\"§a von §6" + target.getName() + "§a wurde erfolgreich gelöscht!");
                            } else {
                                player.sendMessage(Data.getPrefix() + ChatColor.GOLD + target.getName() + "§c besitzt keinen Plot namens §6\"" + args[1] + "\"§c!");
                            }
                        } else {
                            player.sendMessage(Data.getPrefix() + "§cDer Spieler konnte nicht gefunden werden!");
                        }
                    } else if (args[0].equalsIgnoreCase("invite")) {
                        Player target = Bukkit.getPlayer(args[1]);
                        if (target != null) {
                            PlotHandler plotHandler = new PlotHandler();
                            PlotData plotData = plotHandler.getPlot(player, args[2]);
                            plotHandler.addToTrusted(plotData, target);
                        } else {
                            player.sendMessage(Data.getPrefix() + "§cDieser Spieler konnte nicht gefunden werden.");
                        }
                    }

                } else {
                    sendHelp(player);
                }
            } else {
                sender.sendMessage(Data.getNoPlayer());
            }

        }
        return false;
    }

    private void sendHelp(Player player) {
        player.sendMessage(Data.getPrefix() + "§cDas ist kein richtiger Command!");
        player.sendMessage(Data.getPrefix() + "§c/P Help");
    }

    private void sendCommands(Player player) {
        player.sendMessage(Data.getPrefix() + "§c/P create");
        player.sendMessage(Data.getPrefix() + "§c/P invite <Player>");
        player.sendMessage(Data.getPrefix() + "§c/P remove");
        player.sendMessage(Data.getPrefix() + "§c/P home");
        player.sendMessage(Data.getPrefix() + "§c/P root");
    }
}

