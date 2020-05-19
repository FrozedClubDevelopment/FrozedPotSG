package me.elb1to.frozedsg.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

public class ChunkListener implements Listener {

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        if (event.getWorld().getName().equalsIgnoreCase("potsg_map_FOR_USE")) {
            event.setCancelled(true);
        }
    }

}
