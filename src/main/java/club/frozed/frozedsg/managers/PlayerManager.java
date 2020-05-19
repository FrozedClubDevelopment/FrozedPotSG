package club.frozed.frozedsg.managers;

import club.frozed.frozedsg.PotSG;
import club.frozed.frozedsg.enums.PlayerState;
import club.frozed.frozedsg.player.PlayerData;
import club.frozed.frozedsg.utils.ItemBuilder;
import lombok.Data;
import lombok.Getter;
import club.frozed.frozedsg.utils.Utils;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class PlayerManager {
    @Getter
    public static PlayerManager instance;

    public PlayerManager() {
        instance = this;
    }

    public List<Player> getGamePlayers() {
        return Utils.getOnlinePlayers().stream()
                .filter(player -> PlayerDataManager.getInstance().getByUUID(player.getUniqueId()) != null &&
                        PlayerDataManager.getInstance().getByUUID(player.getUniqueId()).getState() == PlayerState.INGAME)
                .collect(Collectors.toList());
    }

    public List<Player> getOnlinePlayers() {
        return new ArrayList<>(Utils.getOnlinePlayers());
    }

    public List<Player> getLobbyPlayers() {
        return Utils.getOnlinePlayers().stream().filter(player -> PlayerDataManager.getInstance().getByUUID(player.getUniqueId()) != null &&
                PlayerDataManager.getInstance().getByUUID(player.getUniqueId()).getState() == PlayerState.LOBBY)
                .collect(Collectors.toList());
    }

    public boolean isSpectator(Player player) {
        return PlayerDataManager.getInstance().getByUUID(player.getUniqueId()) != null
                && (PlayerDataManager.getInstance().getByUUID(player.getUniqueId()).getState().equals(PlayerState.PREMATCH)
                || PlayerDataManager.getInstance().getByUUID(player.getUniqueId()).getState().equals(PlayerState.SPECTATING));
    }

    public List<Player> getSpectatorPlayers() {
        return Utils.getOnlinePlayers().stream()
                .filter(player -> PlayerDataManager.getInstance().getByUUID(player.getUniqueId()) != null &&
                        PlayerDataManager.getInstance().getByUUID(player.getUniqueId()).getState() == PlayerState.SPECTATING)
                .collect(Collectors.toList());
    }

    public List<Player> getPrematchPlayers() {
        return Utils.getOnlinePlayers().stream()
                .filter(player -> PlayerDataManager.getInstance().getByUUID(player.getUniqueId()) != null &&
                        PlayerDataManager.getInstance().getByUUID(player.getUniqueId()).getState() == PlayerState.PREMATCH)
                .collect(Collectors.toList());
    }

    public void setSpectating(Player player) {
        PlayerData data = PlayerDataManager.getInstance().getByUUID(player.getUniqueId());

        Utils.clearPlayer(player);
        data.setState(PlayerState.SPECTATING);
        data.setSpecChat(true);
        player.setAllowFlight(true);
        player.setFlying(true);
        player.setGameMode(GameMode.SURVIVAL);
        player.teleport(GameManager.getInstance().getGameWorldCenterLocation().add(0, 10, 0));

        ItemBuilder alivePlayers = new ItemBuilder(Material.ITEM_FRAME);
        alivePlayers.setNameWithArrows(PotSG.getInstance().getConfiguration("items").getString("spectator-inventory.alive-players.name"));
        for (String lore : PotSG.getInstance().getConfiguration("items").getStringList("spectator-inventory.alive-players.lore")) {
            alivePlayers.addLoreLine(lore);
        }

        ItemBuilder lobby = new ItemBuilder(Material.REDSTONE);
        if (GameManager.getInstance().isToUseLobby()) {
            lobby.setNameWithArrows(PotSG.getInstance().getConfiguration("items").getString("spectator-inventory.quit-sg.lobby-enabled.name"));
            for (String lore : PotSG.getInstance().getConfiguration("items").getStringList("spectator-inventory.quit-sg.lobby-enabled.lore")) {
                lobby.addLoreLine(lore);
            }
        } else {
            lobby.setNameWithArrows(PotSG.getInstance().getConfiguration("items").getString("spectator-inventory.quit-sg.lobby-disabled.name"));
            for (String lore : PotSG.getInstance().getConfiguration("items").getStringList("spectator-inventory.quit-sg.lobby-disabled.lore")) {
                lobby.addLoreLine(lore);
            }
        }

        ItemBuilder inv = new ItemBuilder(Material.BOOK);
        inv.setNameWithArrows(PotSG.getInstance().getConfiguration("items").getString("spectator-inventory.inspect-inventory.name"));
        for (String lore : PotSG.getInstance().getConfiguration("items").getStringList("spectator-inventory.inspect-inventory.lore")) {
            inv.addLoreLine(lore);
        }

        ItemBuilder random = new ItemBuilder(Material.WATCH);
        random.setNameWithArrows(PotSG.getInstance().getConfiguration("items").getString("spectator-inventory.random-teleport.name"));
        for (String lore : PotSG.getInstance().getConfiguration("items").getStringList("spectator-inventory.random-teleport.lore")) {
            random.addLoreLine(lore);
        }

        player.getInventory().setItem(0, alivePlayers.toItemStack());
        player.getInventory().setItem(1, inv.toItemStack());
        player.getInventory().setItem(7, random.toItemStack());
        player.getInventory().setItem(8, lobby.toItemStack());
    }
}
