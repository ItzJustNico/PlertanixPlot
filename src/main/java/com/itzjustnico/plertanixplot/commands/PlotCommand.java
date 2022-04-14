package com.itzjustnico.plertanixplot.commands;

import com.itzjustnico.plertanixplot.main.Main;
import com.itzjustnico.plertanixplot.plots.PlotHandler;
import com.itzjustnico.plertanixplot.storage.Data;
import com.itzjustnico.plertanixplot.plots.PlotData;
import com.itzjustnico.plertanixplot.json.PluginJsonWriter;
import com.itzjustnico.plertanixplot.storage.Math;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

public class PlotCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        //p create
        //p invite <Player>
        //p remove <Player>
        //p home

        //p root

        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (cmd.getName().equalsIgnoreCase("p")) {

                if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("create")) {
                        player.sendMessage(Data.getPrefix() + "§aDer Plot wurde erfolgreich erstellt.");
                        player.sendMessage(Data.getPrefix() + "§7Besuche ihn mit §6/P Home§7.");
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

                        //create Plot json
                        new PlotHandler().createPlotJson(player.getUniqueId());

                    } else if (args[0].equalsIgnoreCase("help")) {
                        sendCommands(player);
                    } else {
                        sendHelp(player);
                    }

                } else if (args.length == 2) {
                    if (args[0].equalsIgnoreCase("invite")) {
                        Player target = Bukkit.getPlayer(args[1]);
                        if (target != null) {
                            player.sendMessage(Data.getPrefix() + "§a");
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
                } else {
                    sendHelp(player);
                }
            }
        } else {
            sender.sendMessage(Data.getNoPlayer());
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

