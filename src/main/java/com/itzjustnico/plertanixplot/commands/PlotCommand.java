package com.itzjustnico.plertanixplot.commands;

import com.itzjustnico.plertanixplot.main.Main;
import com.itzjustnico.plertanixplot.plots.PlotData;
import com.itzjustnico.plertanixplot.plots.PlotHandler;
import com.itzjustnico.plertanixplot.storage.Data;
import com.itzjustnico.plertanixplot.storage.Math;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class PlotCommand implements CommandExecutor {

    private HashMap<UUID, PlotData> invitedPlayers = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        //p create <plotName>   ✔️
        //p delete <plotName>   ✔️
        //p delete <plotName> <Owner>   ✔️

        //p invite <Player> <PlotName>
        //p invite accept   ✔️
        //p invite deny     ✔️
        //p remove <Player> <PlotName>  ✔️

        //p home <PlotName>     ✔️
        //p setHome <PlotName>  ✔️
        //p info  ✔️
        //p list <Player>   ✔️

        //p root    ✔️

        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (cmd.getName().equalsIgnoreCase("p")) {

                if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("info")) {
                        PlotHandler plotHandler = new PlotHandler();
                        PlotData plotData = plotHandler.getPlotFromBlock(player.getLocation().getBlock().getRelative(BlockFace.DOWN));
                        if (plotData != null && Bukkit.getPlayer(plotData.getOwner()) != null) {
                            player.sendMessage("§f§l--------§r§6 Grundstücksinfo §f§l--------");
                            player.sendMessage("§7Owner: §e" + Bukkit.getPlayer(plotData.getOwner()).getName());
                            player.sendMessage("§7Name: §e" + plotData.getName());

                            List<UUID> trustedPlayer = plotData.getTrustedPlayers();
                            String message = "§7Vertraut: §e";
                            int i = 0;
                            for (UUID uuid : trustedPlayer) {
                                if (i > 0) {
                                    message += "§7, §e";
                                }
                                message += Bukkit.getPlayer(uuid).getName();
                                i++;
                            }
                            if (i == 0) {
                                message += "§7Niemand";
                            }
                            player.sendMessage(message);
                            Date createdAT = new Date(Long.parseLong(plotData.getCreatedAT()));
                            SimpleDateFormat sdf = new SimpleDateFormat("d'.' MMMM yyyy");
                            String date = sdf.format(createdAT.getTime());
                            player.sendMessage("§7Erstellt: §e" + date);
                            player.sendMessage("§f§l---------§r§6 PlertanixPlot §f§l---------");

                        } else {
                            player.sendMessage(Data.getPrefix() + "§cHier befindet sich kein Grundstück.");
                        }
                    } else if (args[0].equalsIgnoreCase("list")) {
                        if (!new PlotHandler().listPlots(player)) {
                            player.sendMessage(Data.getPrefix() + "§cDu hast noch keinen Plot.");
                        }
                    } else if (args[0].equalsIgnoreCase("version")) {
                        if (player.hasPermission("plertanix.version")) {
                            player.sendMessage(Data.getPrefix() + "§6" + Main.getPlugin().getPluginDescriptionFile().getVersion());
                        } else {
                            player.sendMessage(Data.getPrefix() + Data.getNoPermission());
                        }
                    } else if (args[0].equalsIgnoreCase("help")) {
                        sendCommands(player);
                    } else {
                        sendHelp(player);
                    }

                } else if (args.length == 2) {
                    if (args[0].equalsIgnoreCase("create")) {
                        PlotHandler plotHandler = new PlotHandler();
                        if (plotHandler.getPlotAmount(player) < (int) Data.cfg.get("plots.maxPlotAmount") || player.hasPermission("plertanix.morePlots")) {
                            if (!plotHandler.checkIfNameDuplicate(player, args[1])) {
                                if (Data.cfg.get("plots.root.1") != null && Data.cfg.get("plots.root.2") != null ) {
                                    if (Data.cfg.get("plots.plotSideLength") != null && Data.cfg.get("plots.blocksBetweenPlots") != null ) {
                                        if ((int) Data.cfg.get("plots.plotSideLength") != 0 && (int) Data.cfg.get("plots.blocksBetweenPlots") != 0 ) {
                                            if (plotHandler.placePlotOnNextAvailable(Material.LILY_PAD, player)) {
                                                //create Plot json
                                                new PlotHandler().createPlotJson(player.getUniqueId(), args[1], plotHandler.getMinX(), plotHandler.getMaxX(), plotHandler.getMinZ(), plotHandler.getMaxZ(), plotHandler.getWaterY());
                                                PlotData plotData = new PlotHandler().getPlot(player, args[1]);
                                                if (plotData != null) {
                                                    player.teleport(plotData.getHomeLocation());
                                                }
                                                Bukkit.getConsoleSender().sendMessage(Data.getPrefix() + ChatColor.GREEN + "A new Plot has been created!");
                                            }
                                        } else {
                                            if (player.hasPermission("plertanix.admin")) {
                                                player.sendMessage(Data.getPrefix() + "§cEs wurde noch keine Plot-größe eingestellt. Bitte stelle diese in der Config ein.");
                                            } else {
                                                player.sendMessage(Data.getPrefix() + "§cEs wurde noch keine Plot-größe eingestellt. Bitte kontaktiere einen Admin.");
                                            }
                                        }
                                    } else {
                                        if (player.hasPermission("plertanix.admin")) {
                                            player.sendMessage(Data.getPrefix() + "§cEs wurde noch keine Plot-größe eingestellt. Bitte stelle diese in der Config ein.");
                                        } else {
                                            player.sendMessage(Data.getPrefix() + "§cEs wurde noch keine Plot-größe eingestellt. Bitte kontaktiere einen Admin.");
                                        }
                                    }
                                } else {
                                    if (player.hasPermission("plertanix.admin")) {
                                        player.sendMessage(Data.getPrefix() + "§cEs wurde noch kein Ausgangs-Plot gesetzt. Bitte setzte diesen mit §6/p root§c.");
                                    } else {
                                        player.sendMessage(Data.getPrefix() + "§cEs wurde noch kein Ausgangs-Plot gesetzt. Bitte kontaktiere einen Admin.");
                                    }
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
                            if (!new PlotHandler().listPlots(target)) {
                                player.sendMessage(Data.getPrefix() + "§cDieser Spieler hat noch keinen Plot.");
                            }
                        } else {
                            player.sendMessage(Data.getPrefix() + "§cDer Spieler konnte nicht gefunden werden!");
                        }
                    } else if (args[0].equalsIgnoreCase("setHome")) {
                        PlotHandler plotHandler = new PlotHandler();
                        PlotData plotData = plotHandler.getPlot(player, args[1]);
                        if (plotData != null) {
                            if (plotHandler.setHome(plotData, player.getLocation())) {
                                player.sendMessage(Data.getPrefix() + "§aDie neue Home-Location wurde erstellt. Du kannst sie mit §6/p home <PlotName>§a nutzen.");
                            } else {
                                player.sendMessage(Data.getPrefix() + "§cDu kannst die Home-Location nur auf deinem eigenen Plot setzen.");
                            }
                        } else {
                            player.sendMessage(Data.getPrefix() + "§cDu hast keinen Plot mit diesem Namen.");
                        }

                    } else if (args[0].equalsIgnoreCase("home")) {
                        new PlotHandler().teleportHome(args[1], player);
                    } else if (args[0].equalsIgnoreCase("root")) {
                        if (player.hasPermission("plertanix.root")) {
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
                        } else {
                            player.sendMessage(Data.getPrefix() + Data.getNoPermission());
                        }

                    } else if (args[0].equalsIgnoreCase("invite")) {
                        if (args[1].equalsIgnoreCase("accept") || args[1].equalsIgnoreCase("deny")) {
                            if (!invitedPlayers.containsKey(player.getUniqueId())) {
                                player.sendMessage(Data.getPrefix() + "§cDieser command ist nicht für den manuellen Gebrauch gedacht.");
                                return false;
                            }
                            PlotData plotData = invitedPlayers.get(player.getUniqueId());
                            if (args[1].equalsIgnoreCase("accept")) {
                                PlotHandler plotHandler = new PlotHandler();
                                if (plotHandler.addToTrusted(plotData, player)) {
                                    player.sendMessage(Data.getPrefix() + "§aDu bist nun an dem Plot §6\"" + plotData.getName() + "\"§a von §6" + player.getName() + "§a beteiligt.");
                                } else {
                                    player.sendMessage(Data.getPrefix() + "§cDu bist bereits an diesem Plot beteiligt.");
                                }
                                invitedPlayers.remove(player.getUniqueId(), plotData);
                            } else if (args[1].equalsIgnoreCase("deny")) {
                                for (int i = 0; i < 100; i++) {
                                    player.sendMessage("");
                                }
                                invitedPlayers.remove(player.getUniqueId(), plotData);
                            }
                        } else {
                            sendHelp(player);
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
                        Player target = Bukkit.getPlayer(args[1]);
                        PlotHandler plotHandler = new PlotHandler();
                        PlotData plotData = plotHandler.getPlot(player, args[2]);
                        if (target != null) {
                            if (plotData != null) {
                                if (!plotData.getTrustedPlayers().contains(target.getUniqueId())) {
                                    invitedPlayers.put(target.getUniqueId(), plotData);
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
                                    player.sendMessage(Data.getPrefix() + "§cDieser Spieler ist bereits an deinem Plot beteiligt.");
                                }
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
                                    target.sendMessage(Data.getPrefix() + "§cDu wurdest von dem Plot §6\"" + plotData.getName() + "\"§c entfernt.");
                                } else {
                                    player.sendMessage(Data.getPrefix() + "§cDer Spieler§6 " + target.getName() + "§c ist nicht an diesem Plot beteiligt.");
                                }
                            } else {
                                player.sendMessage(Data.getPrefix() + "§cDu besitzt keinen Plot mit diesem Namen.");
                            }
                        } else {
                            player.sendMessage(Data.getPrefix() + "§cDieser Spieler konnte nicht gefunden werden.");
                        }
                    } else {
                        sendHelp(player);
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
        player.sendMessage(Data.getPrefix() + "§cZur Hilfe: §e/p §6help");
    }

    private void sendCommands(Player player) {
        player.sendMessage("§8§l---------§r§6 Plot-Hilfe §8§l---------");
        player.sendMessage("§e/p §6create <Plotname> §8- §7Erstellt einen Plot für dich");
        player.sendMessage("§e/p §6delete <Plotname> §8- §7Entfernt einen Plot für dich");
        player.sendMessage("§e/p §6invite <Spieler> <Plotname> §8- §7Gibt einem anderen Spieler Rechte auf deinem Plot");
        player.sendMessage("§e/p §6remove <Spieler> <Plotname> §8- §7Entfernt die Rechte eines anderen Spielers auf deinem Plot");
        player.sendMessage("§e/p §6home <Plotname> §8- §7Teleportiert dich and einen von dir gesetzten Punkt");
        player.sendMessage("§e/p §6setHome <Plotname> §8- §7Setzt einen Punkt zu dem du dich teleportieren kannst");
        player.sendMessage("§e/p §6info §8- §7Gibt dir Informationen über den Plot auf dem du dich befindest");
        player.sendMessage("§e/p §6list <Spieler> §8- §7Zeigt dir alle deine Plots");

        if (player.hasPermission("plertanix.admin")) {
            player.sendMessage("§e/p §6delete <Plotname> <Spieler> §8- §7Entfernt den Plot eines anderen Spielers (Admin)");
            player.sendMessage("§e/p §6root <1|2> §8- §7Setzt die Ecken des Ausgangs-Plot (Admin)");
            player.sendMessage("§e/p §6version §8- §7Zeigt dir die Version des Plugins (Admin)");
        }
        player.sendMessage("§8§l---------§r§6 Plot-Hilfe §8§l---------");
    }
}

