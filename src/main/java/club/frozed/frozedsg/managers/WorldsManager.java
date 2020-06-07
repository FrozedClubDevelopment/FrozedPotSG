package club.frozed.frozedsg.managers;

import club.frozed.frozedsg.PotSG;
import lombok.Data;
import lombok.Getter;
import club.frozed.frozedsg.events.SGWorldLoad;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.WorldType;

import java.io.File;
import java.io.IOException;

@Data
public class WorldsManager {
    @Getter public static WorldsManager instance;
    private World lobbyWorld = null;
    private World gameWorld = null;
    private World gameWorldForEdit = null;

    public WorldsManager() {
        instance = this;
        handleWorldsCopy();
        if (PotSG.getInstance().getConfiguration("config").getBoolean("DEBUG")) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "registered WorldsManager");
        }
    }

    private void registerWorlds(boolean makeRoads) {
        String lobbyWorldName = PotSG.getInstance().getConfiguration("config").getString("WORLDS.LOBBY");
        String gameWorldName = PotSG.getInstance().getConfiguration("config").getString("WORLDS.GAME");
        lobbyWorld = Bukkit.createWorld(new org.bukkit.WorldCreator(lobbyWorldName).environment(World.Environment.NORMAL).type(WorldType.NORMAL));
        lobbyWorld.setGameRuleValue("doFireTick", "false");
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(PotSG.getInstance(), () -> {
            gameWorld = Bukkit.createWorld(new org.bukkit.WorldCreator(gameWorldName + "_FOR_USE").environment(World.Environment.NORMAL).type(WorldType.NORMAL));
            gameWorld.setGameRuleValue("doFireTick", "false");
            Bukkit.getServer().getPluginManager().callEvent(new SGWorldLoad(gameWorld));
        }, 20L);
    }

    private void handleWorldsCopy() {
        if (PotSG.getInstance().getConfiguration("config").getBoolean("DEBUG")) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "WorldsManager handleWorldsCopy running");
        }
        String gameWorldName = PotSG.getInstance().getConfiguration("config").getString("WORLDS.GAME");

        handleWorldDelete(gameWorldName + "_FOR_USE");

        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(PotSG.getInstance(), () -> {
            handleWorldCopy(gameWorldName, gameWorldName + "_FOR_USE");
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(PotSG.getInstance(), () -> {
                this.registerWorlds(true);
            }, 20L);
        }, 20L);
    }

    public void saveCurrentWorld() {
        getGameWorld().save();
        Bukkit.unloadWorld(getGameWorld().getName(), false);
        String gameWorldName = PotSG.getInstance().getConfiguration("config").getString("WORLDS.GAME");
        handleWorldDelete(gameWorldName);
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(PotSG.getInstance(), () -> handleWorldCopy(getGameWorld().getName(), gameWorldName), 20L * 2);
        registerWorlds(false);
    }

    private void handleWorldCopy(String worldToCopy, String worldToPaste) {
        File srcDir = new File(worldToCopy);
        File destDir = new File(worldToPaste);
        try {
            FileUtils.copyDirectory(srcDir, destDir);
        } catch (IOException e) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "World '" + worldToCopy + "' doesn't exists!");
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Brand new world will be created!");
        }
    }

    private void handleWorldDelete(String world) {
        Bukkit.unloadWorld(world, true);
        try {
            FileUtils.deleteDirectory(new File(world));
        } catch (IOException e) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "World '" + world + "' doesn't exists!");
        }
    }
}
