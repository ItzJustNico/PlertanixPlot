package com.itzjustnico.plertanixplot.commands;

import com.itzjustnico.plertanixplot.storage.Data;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlotCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        //p create
        //p invite <Player>
        //p remove <Player>
        //p home

        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (cmd.getName().equalsIgnoreCase("p")) {

                if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("create")) {
                        player.sendMessage(Data.getPrefix() + "§aDer Plot wurde erfolgreich erstellt.");
                        player.sendMessage(Data.getPrefix() + "§7Besuche ihn mit §6/P Home§7.");
                        //create Plot on Map
                        //add Plot + Player + cords in yml
                    } else if (args[0].equalsIgnoreCase("help")) {
                        //send all possible commands wit /p
                    } else {
                        sendHelp(player);
                    }

                } else if (args.length == 2) {
                    if (args[0].equalsIgnoreCase("invite")) {
                        Player target = Bukkit.getPlayer(args[1]);
                        if (target != null) {
                            player.sendMessage("§a");
                        }

                    } else if (args[0].equalsIgnoreCase("help")) {
                        //send all possible commands wit /p
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
}

