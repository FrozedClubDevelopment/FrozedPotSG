package me.elb1to.frozedsg.api;

import lombok.Getter;
import me.elb1to.frozedsg.PotSG;
import me.elb1to.frozedsg.enums.GameState;
import me.elb1to.frozedsg.managers.GameManager;
import me.elb1to.frozedsg.managers.PlayerDataManager;
import me.elb1to.frozedsg.managers.PlayerManager;
import me.elb1to.frozedsg.player.PlayerData;
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
