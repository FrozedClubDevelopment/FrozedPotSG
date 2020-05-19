package club.frozed.frozedsg.utils.countdowns;

import club.frozed.frozedsg.enums.PlayerState;
import club.frozed.frozedsg.PotSG;
import club.frozed.frozedsg.enums.GameState;
import club.frozed.frozedsg.managers.GameManager;
import club.frozed.frozedsg.managers.PlayerDataManager;
import club.frozed.frozedsg.managers.PlayerManager;
import club.frozed.frozedsg.player.PlayerData;
import club.frozed.frozedsg.utils.Utils;
import club.frozed.frozedsg.utils.chat.Color;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;


public class StartCountdown extends BukkitRunnable {

    public int seconds;
    private long expire;

    public StartCountdown() {
        seconds = GameManager.getInstance().getStartCountdownValue();
        long duration = 1000 * seconds;
        expire = System.currentTimeMillis() + duration;
        this.runTaskTimer(PotSG.getInstance(), 0L, 20L);
    }

    public void execute() {
        if (PotSG.getInstance().getConfiguration("config").getBoolean("DEBUG")) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "StartCountdown execute running");
        }
        Utils.broadcastMessage(PotSG.getInstance().getConfiguration("messages").getString("starting-pre-match"), true);
        GameManager.getInstance().setGameState(GameState.PREMATCH);
        PlayerManager.getInstance().getLobbyPlayers().forEach(player -> {
            PlayerData data = PlayerDataManager.getInstance().getByUUID(player.getUniqueId());

            Utils.clearPlayer(player);
            player.teleport(GameManager.getInstance().getGameWorldCenterLocation().add(0, 10, 0));
            player.setAllowFlight(true);
            player.setFlying(true);
            data.setState(PlayerState.PREMATCH);
            player.setGameMode(GameMode.SURVIVAL);
            player.sendMessage(GameManager.getInstance().getGamePrefix() + Color.translate(PotSG.getInstance().getConfiguration("messages").getString("pre-match-countdown"))
                    .replaceAll("<seconds>", String.valueOf(GameManager.getInstance().getPrematchCountdownValue()))
            );
        });
        GameManager.getInstance().setPrematchCountdown(new PrematchCountdown());
    }

    public List<Integer> seconds() {
        return Arrays.asList(60 * 5, 60 * 4, 60 * 3, 60 * 2, 60, 30, 20, 10, 5, 4, 3, 2, 1);
    }

    public void everySeconds() {
        if (seconds > 60) {
            Utils.broadcastMessage(PotSG.getInstance().getConfiguration("messages").getString("pre-match-countdown-minutes")
                            .replaceAll("<minutes>", String.valueOf(seconds/60))
                    , true);
        } else {
            Utils.broadcastMessage(PotSG.getInstance().getConfiguration("messages").getString("pre-match-countdown-seconds")
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
            playSound();
        }
        if (seconds == 0) {
            execute();
            cancel();
        }
    }
}
