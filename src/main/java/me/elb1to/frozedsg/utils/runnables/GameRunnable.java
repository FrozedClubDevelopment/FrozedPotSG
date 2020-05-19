package me.elb1to.frozedsg.utils.runnables;

import lombok.Getter;
import lombok.Setter;
import me.elb1to.frozedsg.PotSG;
import me.elb1to.frozedsg.enums.GameState;
import me.elb1to.frozedsg.events.SGGameWinEvent;
import me.elb1to.frozedsg.managers.GameManager;
import me.elb1to.frozedsg.managers.PlayerManager;
import me.elb1to.frozedsg.utils.Utils;
import me.elb1to.frozedsg.utils.chat.Color;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
@Setter
public class GameRunnable implements Runnable{
    private int seconds = 0;
    private boolean announced = false;

    public GameRunnable() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(PotSG.getInstance(), this, 20L, 20L);
    }

    @Override
    public void run() {
        if (GameManager.getInstance().getGameState().equals(GameState.INGAME)) {
            seconds++;
            if (PlayerManager.getInstance().getGamePlayers().size() == 1) {
                if (!announced) {
                    Player winer = PlayerManager.getInstance().getGamePlayers().get(0);
                    setAnnounced(true);
                    Bukkit.getServer().getPluginManager().callEvent(new SGGameWinEvent(winer));
                }
            } else if (PlayerManager.getInstance().getGamePlayers().size() <= 0 || PlayerManager.getInstance().getGamePlayers().isEmpty()) {
                if (!announced) {
                    Bukkit.broadcastMessage(Color.translate("&cSystem has detected the game has no players left, system will automatically restart in 10 seconds..."));
                    setAnnounced(true);
                    new BukkitRunnable() {
                        public void run() {
                            Bukkit.shutdown();
                        }
                    }.runTaskLaterAsynchronously(PotSG.getInstance(), 200L);
                }
            }
            /*if (PlayerManager.getInstance().getGamePlayers().size() == 1) {
                if (!announced) {
                    setAnnounced(true);
                    Bukkit.getServer().getPluginManager().callEvent(new SGGameWinEvent());
                }
            }*/
        }
    }

    public String getTime() {
        return Utils.formatTime(seconds);
    }
}
