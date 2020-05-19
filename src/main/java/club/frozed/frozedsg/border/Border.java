package me.elb1to.frozedsg.border;

import lombok.Getter;
import lombok.Setter;
import me.elb1to.frozedsg.PotSG;
import me.elb1to.frozedsg.managers.WorldsManager;
import me.elb1to.frozedsg.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.stream.Collectors;

@Getter
@Setter
public class Border {
    private int size;
    private int seconds;
    private int lastBorder;
    private int startBorder;

    public Border() {
        this.startBorder = BorderManager.getInstance().getStartBorder();
        this.size = startBorder;
        this.seconds = BorderManager.getInstance().getShrinkEvery();
        this.lastBorder = BorderManager.getInstance().getShrinkUntil();
    }

    public void increaseSeconds() {
        seconds--;
    }

    public int getNextBorder() {
        String shrinkStream = PotSG.getInstance().getConfiguration("config").getString("BORDER.SHRINK-STREAM");
        String[] shrinksStream = shrinkStream.split(";");
        int current;
        if (Arrays.stream(shrinksStream).collect(Collectors.toList()).contains(String.valueOf(this.size))) {
            current = Arrays.stream(shrinksStream).collect(Collectors.toList()).indexOf(String.valueOf(this.size));
        } else {
            return Utils.getNextBorderDefault();
        }
        if (current == shrinksStream.length - 1) return Integer.parseInt(shrinksStream[shrinksStream.length - 1]);
        return Integer.parseInt(shrinksStream[current + 1]);
    }

    public void shrinkBorder(int size) {
        this.size = size;
        World w = WorldsManager.getInstance().getGameWorld();
        this.buildWalls(size, Material.BEDROCK, 4, w);
        for (Player player : w.getPlayers()) {
            if (player.getLocation().getBlockX() > size) {
                player.setNoDamageTicks(100);
                player.setFallDistance(0.0f);
                player.teleport(new Location(w, (size - 4), w.getHighestBlockYAt(size - 4, player.getLocation().getBlockZ()) + 0.5, player.getLocation().getBlockZ()));
                player.setFallDistance(0.0f);
                player.getLocation().add(0.0, 2.0, 0.0).getBlock().setType(Material.AIR);
                player.getLocation().add(0.0, 3.0, 0.0).getBlock().setType(Material.AIR);
                player.getLocation().add(0.0, 4.0, 0.0).getBlock().setType(Material.AIR);
                player.teleport(new Location(w, player.getLocation().getBlockX(), w.getHighestBlockAt(player.getLocation().getBlockX(), player.getLocation().getBlockZ()).getLocation().getBlockY() + 0.5, player.getLocation().getBlockZ()));
            }

            if (player.getLocation().getBlockZ() > size) {
                player.setNoDamageTicks(100);
                player.setFallDistance(0.0f);
                player.teleport(new Location(w, player.getLocation().getBlockX(), w.getHighestBlockYAt(player.getLocation().getBlockX(), size - 4) + 0.5, (size - 4)));
                player.setFallDistance(0.0f);
                player.getLocation().add(0.0, 2.0, 0.0).getBlock().setType(Material.AIR);
                player.getLocation().add(0.0, 3.0, 0.0).getBlock().setType(Material.AIR);
                player.getLocation().add(0.0, 4.0, 0.0).getBlock().setType(Material.AIR);
                player.teleport(new Location(w, player.getLocation().getBlockX(), w.getHighestBlockAt(player.getLocation().getBlockX(), player.getLocation().getBlockZ()).getLocation().getBlockY() + 0.5, player.getLocation().getBlockZ()));
            }

            if (player.getLocation().getBlockX() < -size) {
                player.setNoDamageTicks(100);
                player.setFallDistance(0.0f);
                player.teleport(new Location(w, (-size + 4), w.getHighestBlockYAt(-size + 4, player.getLocation().getBlockZ()) + 0.5, player.getLocation().getBlockZ()));
                player.setFallDistance(0.0f);
                player.getLocation().add(0.0, 2.0, 0.0).getBlock().setType(Material.AIR);
                player.getLocation().add(0.0, 3.0, 0.0).getBlock().setType(Material.AIR);
                player.getLocation().add(0.0, 4.0, 0.0).getBlock().setType(Material.AIR);
                player.teleport(new Location(w, player.getLocation().getBlockX(), w.getHighestBlockAt(player.getLocation().getBlockX(), player.getLocation().getBlockZ()).getLocation().getBlockY() + 0.5, player.getLocation().getBlockZ()));
            }

            if (player.getLocation().getBlockZ() < -size) {
                player.setNoDamageTicks(100);
                player.setFallDistance(0.0f);
                player.teleport(new Location(w, player.getLocation().getBlockX(), w.getHighestBlockYAt(player.getLocation().getBlockX(), -size + 4) + 0.5, (-size + 4)));
                player.setFallDistance(0.0f);
                player.getLocation().add(0.0, 2.0, 0.0).getBlock().setType(Material.AIR);
                player.getLocation().add(0.0, 3.0, 0.0).getBlock().setType(Material.AIR);
                player.getLocation().add(0.0, 4.0, 0.0).getBlock().setType(Material.AIR);
                player.teleport(new Location(w, player.getLocation().getBlockX(), w.getHighestBlockAt(player.getLocation().getBlockX(), player.getLocation().getBlockZ()).getLocation().getBlockY() + 0.5, player.getLocation().getBlockZ()));
            }
        }
    }

    public void buildWalls(int size, Material mat, int h, World w) {
        Location loc = new Location(w, 0.0, 59.0, 0.0);
        for (int i = h; i < h + h; ++i) {
            for (int x = loc.getBlockX() - size; x <= loc.getBlockX() + size; ++x) {
                for (int y = 58; y <= 58; ++y) {
                    for (int z = loc.getBlockZ() - size; z <= loc.getBlockZ() + size; ++z) {
                        if (x == loc.getBlockX() - size || x == loc.getBlockX() + size || z == loc.getBlockZ() - size
                                || z == loc.getBlockZ() + size) {
                            Location loc2 = new Location(w, x, y, z);
                            loc2.setY(w.getHighestBlockYAt(loc2));
                            loc2.getBlock().setType(mat);
                        }
                    }
                }
            }
        }
    }
}
