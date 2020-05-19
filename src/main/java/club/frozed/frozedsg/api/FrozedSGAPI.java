package club.frozed.frozedsg.api;

import club.frozed.frozedsg.PotSG;
import club.frozed.frozedsg.managers.GameManager;
import lombok.Getter;
import club.frozed.frozedsg.enums.GameState;
import club.frozed.frozedsg.managers.PlayerDataManager;
import club.frozed.frozedsg.managers.PlayerManager;
import club.frozed.frozedsg.player.PlayerData;
import org.bukkit.entity.Player;

import java.util.List;

public class FrozedSGAPI {

    @Getter
    private static FrozedSGAPI api = new FrozedSGAPI();

    public PlayerData getPlayerData(Player player) {
        return PlayerDataManager.getInstance().getByUUID(player.getUniqueId());
    }

    public GameState getGameState() {
        return GameManager.getInstance().getGameState();
    }

    public List<Player> getGamePlayers() {
        return PlayerManager.getInstance().getGamePlayers();
    }

    public List<Player> getLobbyPlayers() {
        return PlayerManager.getInstance().getLobbyPlayers();
    }

    public PotSG getGame() {
        return PotSG.getInstance();
    }

}
