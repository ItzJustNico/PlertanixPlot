package com.itzjustnico.plertanixplot.listener;

import com.itzjustnico.plertanixplot.plots.PlotHandler;
import com.itzjustnico.plertanixplot.storage.Data;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class InteractListener implements Listener {

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        System.out.println(block);
        PlotHandler plotHandler = new PlotHandler();
        if (!player.hasPermission("blockInteractPerm")) {
            if (!plotHandler.hasPlot(player)) {
                event.setCancelled(true);
                player.sendMessage(Data.getPrefix() + "§cDu kannst nicht mit diesem Block interagieren!");
            } else if (!plotHandler.checkBlockOnPlotBreakable(player, block)) {
                event.setCancelled(true);
                player.sendMessage(Data.getPrefix() + "§cDu kannst nicht mit diesem Block interagieren!");
            }
        }
    }
}


