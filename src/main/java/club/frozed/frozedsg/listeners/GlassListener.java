package me.elb1to.frozedsg.listeners;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import me.elb1to.frozedsg.border.BorderManager;
import me.elb1to.frozedsg.managers.PlayerDataManager;
import me.elb1to.frozedsg.player.PlayerData;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;


public class GlassListener implements Listener {
    private Map<UUID, Set<Location>> Locations = new HashMap<>();

    public int closest(int n, int... array) {
        int n2 = array[0];
        for (int i = 0; i < array.length; ++i) {
            if (Math.abs(n - array[i]) < Math.abs(n - n2)) {
                n2 = array[i];
            }
        }
        return n2;
    }

    public void update(Player player) {
        if (BorderManager.getInstance().getBorder() == null) {
            return;
        }
        PlayerData data = PlayerDataManager.getInstance().getByUUID(player.getUniqueId());
        if (data != null && data.getSettingByName("GlassBorder") != null && !data.getSettingByName("GlassBorder").isEnabled()) {
            removeGlass(player);
            return;
        }
        HashSet<Location> set = new HashSet<>();
        int Firstclosest = closest(player.getLocation().getBlockX(), -BorderManager.getInstance().getBorder().getSize(),
                BorderManager.getInstance().getBorder().getSize());
        int Secondclosest = closest(player.getLocation().getBlockZ(), -BorderManager.getInstance().getBorder().getSize(),
                BorderManager.getInstance().getBorder().getSize());
        boolean first = Math.abs(player.getLocation().getX() - Firstclosest) < 6.0;
        boolean second = Math.abs(player.getLocation().getZ() - Secondclosest) < 6.0;

        if (!first && !second) {
            removeGlass(player);
            return;
        }
        if (first) {
            for (int i = -4; i < 5; ++i) {
                for (int j = -5; j < 6; ++j) {
                    Location location = new Location(player.getLocation().getWorld(),
                            (double) Firstclosest,
                            (double) (player.getLocation().getBlockY() + i),
                            (double) (player.getLocation().getBlockZ() + j));
                    if (!set.contains(location) && !location.getBlock().getType().isOccluding()) {
                        set.add(location);
                    }
                }
            }
        }
        if (second) {
            for (int k = -4; k < 5; ++k) {
                for (int l = -5; l < 6; ++l) {
                    Location location2 = new Location(player.getLocation().getWorld(),
                            (double) (player.getLocation().getBlockX() + l),
                            (double) (player.getLocation().getBlockY() + k),
                            (double) Secondclosest);
                    if (!set.contains(location2) && !location2.getBlock().getType().isOccluding()) {
                        set.add(location2);
                    }

                }
            }
        }
        render(player, set);
    }

    @Deprecated
    public void render(Player player, Set<Location> set) {
        if (BorderManager.getInstance().getBorder() == null) {
            return;
        }
        PlayerData data = PlayerDataManager.getInstance().getByUUID(player.getUniqueId());
        if (data != null && data.getSettingByName("GlassBorder") != null && !data.getSettingByName("GlassBorder").isEnabled()) {
            return;
        }
        if (Locations.containsKey(player.getUniqueId())) {
            Locations.get(player.getUniqueId()).addAll(set);
            for (Location location : Locations.get(player.getUniqueId())) {
                if (!set.contains(location)) {
                    Block block = location.getBlock();
                    player.sendBlockChange(location, block.getTypeId(), block.getData());
                }
            }
            Iterator<Location> iterator2 = set.iterator();
            while (iterator2.hasNext()) {
                player.sendBlockChange(iterator2.next(), 95, (byte) 14);
            }
        } else {
            Iterator<Location> iterator3 = set.iterator();
            while (iterator3.hasNext()) {
                player.sendBlockChange(iterator3.next(), 95, (byte) 14);
            }
        }
        Locations.put(player.getUniqueId(), set);
    }

    public void removeGlass(Player player) {
        if (Locations.containsKey(player.getUniqueId())) {
            for (Location location : Locations.get(player.getUniqueId())) {
                Block block = location.getBlock();
                player.sendBlockChange(location, block.getTypeId(), block.getData());
            }
            Locations.remove(player.getUniqueId());
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        update(player);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        update(player);
    }
}