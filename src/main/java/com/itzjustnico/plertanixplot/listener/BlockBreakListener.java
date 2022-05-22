package com.itzjustnico.plertanixplot.listener;

import com.itzjustnico.plertanixplot.plots.PlotHandler;
import com.itzjustnico.plertanixplot.storage.Data;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        PlotHandler plotHandler = new PlotHandler();
        if (!player.hasPermission("blockBreakPerm")) {
            if (!plotHandler.hasPlot(player)) {
                event.setCancelled(true);
                player.sendMessage(Data.getPrefix() + "§cDu kannst diesen Block nicht zerstören!");
            } else if (!plotHandler.checkBlockOnPlotBreakable(player, block)) {
                event.setCancelled(true);
                player.sendMessage(Data.getPrefix() + "§cDu kannst diesen Block nicht zerstören!");
            }
        }
    }
}
