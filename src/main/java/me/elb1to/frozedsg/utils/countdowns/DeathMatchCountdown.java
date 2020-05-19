package me.elb1to.frozedsg.utils.countdowns;

import me.elb1to.frozedsg.PotSG;
import me.elb1to.frozedsg.border.BorderManager;
import me.elb1to.frozedsg.managers.GameManager;
import me.elb1to.frozedsg.managers.PlayerManager;
import me.elb1to.frozedsg.managers.WorldsManager;
import me.elb1to.frozedsg.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;


public class DeathMatchCountdown extends BukkitRunnable {

    public int seconds;
    private long expire;

    public DeathMatchCountdown() {
        seconds = GameManager.getInstance().getDeathMatchCountdownValue() * 60;
        long duration = 1000 * seconds;
        expire = System.currentTimeMillis() + duration;
        this.runTaskTimer(PotSG.getInstance(), 0L, 20L);
    }

    public void execute() {
        GameManager.getInstance().spawnDeathMatchArena();
        GameManager.getInstance().setDeathMatchArenaSpawned(true);
        Utils.broadcastMessage(PotSG.getInstance().getConfiguration("messages").getString("deathmatch-started"), true);
        PlayerManager.getInstance().getGamePlayers().forEach(player -> player.teleport(getDLocation()));
        BorderManager.getInstance().getBorder().setSize(15);
        if (PotSG.getInstance().getConfiguration("config").getBoolean("DEBUG")) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "Deathmatch has been started");
        }
    }

    public Location getDLocation() {
        int x = 0;
        int z = 0;
        double y = WorldsManager.getInstance().getGameWorld().getHighestBlockYAt(x, z);
        return new Location(WorldsManager.getInstance().getGameWorld(), x, y + 0.5, z);
    }

    public List<Integer> seconds() {
        return Arrays.asList(60 * 7, 60 * 8, 60 * 9, 60 * 6, 60 * 10, 60 * 5, 60 * 4, 60 * 3, 60 * 2, 60, 30, 20, 10, 5, 4, 3, 2, 1);
    }

    public void everySeconds() {
        if (seconds > 60) {
            Utils.broadcastMessage(PotSG.getInstance().getConfiguration("messages").getString("deathmatch-countdown-minutes")
                    .replaceAll("<minutes>", String.valueOf(seconds/60))
                    , true);
        } else {
            Utils.broadcastMessage(PotSG.getInstance().getConfiguration("messages").getString("deathmatch-countdown-seconds")
                            .replaceAll("<seconds>", String.valueOf(seconds))
                    , true);
        }

    }

    public Sound playSound() {
        return Sound.NOTE_PLING;
    }

    public boolean hasExpired() {
        return System.currentTimeMillis() - this.expire > 1;
    }

    public long getRemaining() {
        return this.expire - System.currentTimeMillis();
    }

    public int getSecondsLeft() {
        return (int) getRemaining() / 1000;
    }

    public String getTimeLeft() {
        return Utils.formatTime(getSecondsLeft());
    }

    public void cancelCountdown() {
        this.cancel();
        this.expire = 0;
    }

    @Override
    public void run() {
        --seconds;
        if (seconds().contains(seconds)) {
            everySeconds();
        }
        if (seconds == 0) {
            execute();
            cancel();
        }
    }
}
