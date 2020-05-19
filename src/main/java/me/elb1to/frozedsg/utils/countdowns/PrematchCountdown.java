package me.elb1to.frozedsg.utils.countdowns;

import me.elb1to.frozedsg.PotSG;
import me.elb1to.frozedsg.border.BorderManager;
import me.elb1to.frozedsg.enums.GameState;
import me.elb1to.frozedsg.enums.PlayerState;
import me.elb1to.frozedsg.managers.GameManager;
import me.elb1to.frozedsg.managers.PlayerDataManager;
import me.elb1to.frozedsg.managers.PlayerManager;
import me.elb1to.frozedsg.managers.WorldsManager;
import me.elb1to.frozedsg.player.PlayerData;
import me.elb1to.frozedsg.utils.ItemBuilder;
import me.elb1to.frozedsg.utils.Utils;
import me.elb1to.frozedsg.utils.runnables.GameRunnable;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;


public class PrematchCountdown extends BukkitRunnable {

    public int seconds;
    private long expire;

    public PrematchCountdown() {
        seconds = GameManager.getInstance().getPrematchCountdownValue();
        long duration = 1000 * seconds;
        expire = System.currentTimeMillis() + duration;
        this.runTaskTimer(PotSG.getInstance(), 0L, 20L);
    }

    public void execute() {
        Utils.broadcastMessage(PotSG.getInstance().getConfiguration("messages").getString("game-started"), true);
        PlayerManager.getInstance().getPrematchPlayers().forEach(player -> {
            PlayerData data = PlayerDataManager.getInstance().getByUUID(player.getUniqueId());

            Utils.clearPlayer(player);
            player.teleport(GameManager.getInstance().getGameWorldCenterLocation());
            data.setState(PlayerState.INGAME);
            data.getGamesPlayed().increaseAmount(1);
            player.setAllowFlight(false);
            player.setFlying(false);
            player.setGameMode(GameMode.SURVIVAL);
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 49, 1));
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 15, 14));
            player.getInventory().addItem(new ItemBuilder(Material.COMPASS).setNameWithArrows(PotSG.getInstance().getConfiguration("items").getString("player-tracker.name")).toItemStack());
        });
        WorldsManager.getInstance().getGameWorld().setPVP(false);
        GameManager.getInstance().setGameState(GameState.INGAME);
        BorderManager.getInstance().startBorderShrink();
        GameManager.getInstance().setGameRunnable(new GameRunnable());
        Bukkit.getScheduler().scheduleSyncDelayedTask(PotSG.getInstance(), () -> {
            GameManager.getInstance().setPvpCountdown(new PvPCountdown());
            GameManager.getInstance().setFeastCountdown(new FeastCountdown());
            GameManager.getInstance().setDeathMatchCountdown(new DeathMatchCountdown());
        }, 20L);
    }

    public List<Integer> seconds() {
        return Arrays.asList(60 * 5, 60 * 4, 60 * 3, 60 * 2, 60, 30, 20, 10, 5, 4, 3, 2, 1);
    }

    public void everySeconds() {
        if (seconds > 60) {
            Utils.broadcastMessage(PotSG.getInstance().getConfiguration("messages").getString("game-begin-countdown-minutes")
                            .replaceAll("<minutes>", String.valueOf(seconds/60))
                    , true);
        } else {
            Utils.broadcastMessage(PotSG.getInstance().getConfiguration("messages").getString("game-begin-countdown-seconds")
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
