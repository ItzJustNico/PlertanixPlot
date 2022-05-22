package com.itzjustnico.plertanixplot.commands;

import com.itzjustnico.plertanixplot.plots.PlotData;
import com.itzjustnico.plertanixplot.plots.PlotHandler;
import com.itzjustnico.plertanixplot.storage.Data;
import com.itzjustnico.plertanixplot.storage.Math;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.Set;

public class PlotCommand implements CommandExecutor {

    private PlotData plotData;
    private Player target;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        //p create <plotName>   ✔️
        //p delete <plotName>   ✔️
        //p delete <plotName> <Owner>   ✔️

        //p invite <Player> <PlotName>
        //p invite accept   ✔️
        //p invite deny     ✔️
        //p remove <Player> <PlotName>  ✔️

        //p home <PlotName>
        //p setHome <PlotName>
        //p info
        //p list <Player>   ✔️

        //p root    ✔️

        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (cmd.getName().equalsIgnoreCase("p")) {

                if (args.length == 1) {
                    //create Plot on Map
                    //wo?
                    //ausgangspunkt -> mitte 1. plot oder so
                    //spiralen-förmig rundherum
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
                            player.sendMessage(Data.getPrefix() + "§aDein Plot §6\"" + args[1] + "\"§a wurde erfolgreich gelöscht!");
                        } else {
                            player.sendMessage(Data.getPrefix() + "§cDu besitzt keinen Plot namens §6\"" + args[1] + "\"§c!");
                        }
                    } else if (args[0].equalsIgnoreCase("list")) {
                        Player target = Bukkit.getPlayer(args[1]);
                        if (target != null) {
                            new PlotHandler().listPlots(target);
                        } else {
                            player.sendMessage(Data.getPrefix() + "§cDer Spieler konnte nicht gefunden werden!");
                        }
                    } else if (args[0].equalsIgnoreCase("setHome")) {
                        PlotHandler plotHandler = new PlotHandler();
                        PlotData plotData = plotHandler.getPlot(player, args[1]);
                        plotHandler.setHome(plotData, player.getLocation());
                        player.sendMessage(Data.getPrefix() + "§aDie neue Home-Location wurde erstellt. Du kannst sie mit §6/p home <PlotName>§a nutzen." );
                    } else if (args[0].equalsIgnoreCase("home")) {
                        new PlotHandler().teleportHome(args[1], player);
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

                    } else if (args[0].equalsIgnoreCase("invite")) {
                        PlotHandler plotHandler = new PlotHandler();
                        if (target == null || plotData == null) {
                            player.sendMessage(Data.getPrefix() + "§cDieser command ist nicht für den manuellen Gebrauch gedacht.");
                            return false;
                        }
                        if (args[1].equalsIgnoreCase("accept")) {
                            if (plotHandler.addToTrusted(plotData, target)) {
                                target.sendMessage(Data.getPrefix() + "§aDu bist nun an dem Plot §6\"" + plotData.getName() + "\"§a von §6" + player.getName() + "§a beteiligt.");
                            } else {
                                target.sendMessage(Data.getPrefix() + "§cDu bist bereits an diesem Plot beteiligt.");

                            }
                        } else if (args[1].equalsIgnoreCase("deny")){
                            for (int i = 0; i < 80; i++) {
                                player.sendMessage("");
                            }
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
                            player.sendMessage(Data.getPrefix() + "§cDieser Spieler konnte nicht gefunden werden!");
                        }
                    } else if (args[0].equalsIgnoreCase("invite")) {
                        Player localTarget = Bukkit.getPlayer(args[1]);
                        PlotHandler plotHandler = new PlotHandler();
                        PlotData localPlotData = plotHandler.getPlot(player, args[2]);
                        if (localTarget != null) {
                            if (localPlotData != null) {
                                plotData = localPlotData;
                                target = localTarget;
                                player.sendMessage(Data.getPrefix() + "§aDer Spieler§6 " + target.getName() + "§a wurde eingeladen.");

                                TextComponent baseText = new TextComponent(Data.getPrefix() + "§7Du wurdest auf den Plot von §6" + player.getName() + " §7eingeladen: ");

                                TextComponent textAccept = new TextComponent("§a[annehmen] ");
                                textAccept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/p invite accept"));
                                textAccept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§a[annehmen]")));
                                TextComponent textDeny = new TextComponent("§c[ablehnen]");
                                textDeny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/p invite deny"));
                                textDeny.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§c[ablehnen]")));

                                baseText.addExtra(textAccept);
                                baseText.addExtra(textDeny);
                                target.spigot().sendMessage(baseText);
                            } else {
                                player.sendMessage(Data.getPrefix() + "§cDu besitzt keinen Plot mit diesem Namen.");
                            }
                        } else {
                            player.sendMessage(Data.getPrefix() + "§cDieser Spieler konnte nicht gefunden werden.");
                        }
                    } else if (args[0].equalsIgnoreCase("remove")) {
                        Player target = Bukkit.getPlayer(args[1]);
                        PlotHandler plotHandler = new PlotHandler();
                        PlotData plotData = plotHandler.getPlot(player, args[2]);
                        if (target != null) {
                            if (plotData != null) {
                                if (plotHandler.removeFromTrusted(plotData, target)) {
                                    player.sendMessage(Data.getPrefix() + "§aDer Spieler§6 " + target.getName() + "§a wurde von diesem Plot entfernt.");
                                } else {
                                    player.sendMessage(Data.getPrefix() + "§cDer Spieler§6 " + target.getName() + "§c ist nicht an diesem Plot beteiligt.");
                                }
                            } else {
                                player.sendMessage(Data.getPrefix() + "§cDu besitzt keinen Plot mit diesem Namen.");
                            }
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
        player.sendMessage(Data.getPrefix() + "§c/P remove <Player>");
        player.sendMessage(Data.getPrefix() + "§c/P delete <PlotName>");
        player.sendMessage(Data.getPrefix() + "§c/P home");
        player.sendMessage(Data.getPrefix() + "§c/P root");
    }
}

