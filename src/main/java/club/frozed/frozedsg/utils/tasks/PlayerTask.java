package club.frozed.frozedsg.utils.tasks;

import club.frozed.frozedsg.managers.PlayerManager;
import club.frozed.frozedsg.utils.Utils;
import org.bukkit.entity.Player;

public class PlayerTask implements Runnable {

    @Override
    public void run() {
        for (Player online : Utils.getOnlinePlayers()) {
            PlayerManager.getInstance().getSpectatorPlayers().forEach(online::hidePlayer);
            //PlayerManager.getInstance().getPrematchPlayers().forEach(online::hidePlayer);
        }
        for (Player online : Utils.getOnlinePlayers()) {
            PlayerManager.getInstance().getGamePlayers().forEach(online::showPlayer);
        }
    }
}
