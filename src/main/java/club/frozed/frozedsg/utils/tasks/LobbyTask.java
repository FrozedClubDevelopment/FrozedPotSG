package club.frozed.frozedsg.utils.tasks;

import club.frozed.frozedsg.PotSG;
import club.frozed.frozedsg.enums.GameState;
import club.frozed.frozedsg.managers.GameManager;
import club.frozed.frozedsg.managers.PlayerManager;
import club.frozed.frozedsg.utils.Utils;
import org.bukkit.Sound;

public class LobbyTask implements Runnable {

    @Override
    public void run() {
        if (!GameManager.getInstance().getGameState().equals(GameState.LOBBY)) {
            return;
        }
        if (PlayerManager.getInstance().getLobbyPlayers().size() < GameManager.getInstance().getMinPlayers()) {
            if (GameManager.getInstance().getStartCountdown() != null && !GameManager.getInstance().getStartCountdown().hasExpired() && !GameManager.getInstance().isForceStarted()) {
                GameManager.getInstance().getStartCountdown().cancelCountdown();
                Utils.broadcastMessage(PotSG.getInstance().getConfiguration("messages").getString("start-stopped-not-enough-players"), true);
                Utils.broadcastMessage(PotSG.getInstance().getConfiguration("messages").getString("require-players-amount")
                        .replaceAll("<player_amount>", String.valueOf(GameManager.getInstance().getRequiredPlayersToJoin()))
                        , true);
                Utils.playSound(Sound.FIREWORK_BLAST);
            }
        }
        if (PlayerManager.getInstance().getLobbyPlayers().size() >= GameManager.getInstance().getMinPlayers()) {
            if (GameManager.getInstance().getStartCountdown() == null || (GameManager.getInstance().getStartCountdown() != null && GameManager.getInstance().getStartCountdown().hasExpired())) {
                if (GameManager.getInstance().getGameState().equals(GameState.LOBBY)) {
                    GameManager.getInstance().startGame();
                }
            }
        }
    }
}
