package club.frozed.frozedsg.layout;

import club.frozed.frozedsg.PotSG;
import club.frozed.frozedsg.enums.GameState;
import club.frozed.frozedsg.managers.GameManager;
import club.frozed.frozedsg.managers.PlayerDataManager;
import club.frozed.frozedsg.managers.PlayerManager;
import club.frozed.frozedsg.player.PlayerData;
import club.frozed.frozedsg.utils.chat.Color;
import me.allen.ziggurat.ZigguratAdapter;
import me.allen.ziggurat.objects.BufferedTabObject;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TablistLayout implements ZigguratAdapter {

    int top5Slot;
    private List<String> formats = new ArrayList<>();

    @Override
    public String getHeader() {
        return Color.translate(PotSG.getInstance().getConfiguration("tablist").getString("tablist.header"));
    }

    @Override
    public String getFooter() {
        return Color.translate(PotSG.getInstance().getConfiguration("tablist").getString("tablist.footer"));
    }

    @Override
    public Set<BufferedTabObject> getSlots(Player player) {
        Set<BufferedTabObject> objects = new HashSet<>();

        PlayerData data = PlayerDataManager.getInstance().getByUUID(player.getUniqueId());
        GameManager gameManager = GameManager.getInstance();
        PlayerManager playerManager = PlayerManager.getInstance();

        if (gameManager.getGameState() == GameState.LOBBY) {
            objects.add(new BufferedTabObject().slot(21).text(PotSG.getInstance().getConfiguration("tablist").getString("isInLobby.title")));

            objects.add(new BufferedTabObject().slot(3).text(replace(PotSG.getInstance().getConfiguration("tablist").getString("isInLobby.you"), player)));
            objects.add(new BufferedTabObject().slot(4).text(replace(PotSG.getInstance().getConfiguration("tablist").getString("isInLobby.yourName"), player)));

            objects.add(new BufferedTabObject().slot(23).text(PotSG.getInstance().getConfiguration("tablist").getString("isInLobby.server")));
            objects.add(new BufferedTabObject().slot(24).text(replace(PotSG.getInstance().getConfiguration("tablist").getString("isInLobby.serverName"), player)));

            objects.add(new BufferedTabObject().slot(43).text(PotSG.getInstance().getConfiguration("tablist").getString("isInLobby.state")));
            objects.add(new BufferedTabObject().slot(44).text(replace(PotSG.getInstance().getConfiguration("tablist").getString("isInLobby.playersAmount"), player)));
            objects.add(new BufferedTabObject().slot(45).text(replace(PotSG.getInstance().getConfiguration("tablist").getString("isInLobby.maxPlayersAmount"), player)));
            objects.add(new BufferedTabObject().slot(46).text(replace(PotSG.getInstance().getConfiguration("tablist").getString("isInLobby.requiredPlayers"), player)));

            int x = player.getLocation().getBlockX();
            int z = player.getLocation().getBlockZ();
            objects.add(new BufferedTabObject().slot(48).text(PotSG.getInstance().getConfiguration("tablist").getString("isInLobby.location")));
            objects.add(new BufferedTabObject().slot(49).text(ChatColor.translateAlternateColorCodes('&', "&7(&f" + x + "&7, &f" + z + "&7)")));

            objects.add(new BufferedTabObject().slot(51).text(PotSG.getInstance().getConfiguration("tablist").getString("isInLobby.player_info")));
            objects.add(new BufferedTabObject().slot(52).text(replace(PotSG.getInstance().getConfiguration("tablist").getString("isInLobby.killsAmount"), player)));
            objects.add(new BufferedTabObject().slot(53).text(replace(PotSG.getInstance().getConfiguration("tablist").getString("isInLobby.deathsAmount"), player)));
            objects.add(new BufferedTabObject().slot(54).text(replace(PotSG.getInstance().getConfiguration("tablist").getString("isInLobby.KDRAmount"), player)));
            objects.add(new BufferedTabObject().slot(55).text(replace(PotSG.getInstance().getConfiguration("tablist").getString("isInLobby.winsAmount"), player)));

            objects.add(new BufferedTabObject().slot(20).text(PotSG.getInstance().getConfiguration("tablist").getString("tablist.bottom_left")));
            objects.add(new BufferedTabObject().slot(40).text(PotSG.getInstance().getConfiguration("tablist").getString("tablist.bottom_center")));
            objects.add(new BufferedTabObject().slot(60).text(PotSG.getInstance().getConfiguration("tablist").getString("tablist.bottom_right")));
        }

        if (gameManager.getGameState() == GameState.PREMATCH || gameManager.getGameState() == GameState.INGAME || gameManager.getGameState() == GameState.ENDING) {
            objects.add(new BufferedTabObject().slot(21).text(PotSG.getInstance().getConfiguration("tablist").getString("isInMatch.title")));

            objects.add(new BufferedTabObject().slot(3).text(PotSG.getInstance().getConfiguration("tablist").getString("isInMatch.players_title")));
            objects.add(new BufferedTabObject().slot(4).text(replace(PotSG.getInstance().getConfiguration("tablist").getString("isInMatch.ingameAmount"), player)));

            objects.add(new BufferedTabObject().slot(23).text(PotSG.getInstance().getConfiguration("tablist").getString("isInMatch.server")));
            objects.add(new BufferedTabObject().slot(24).text(replace(PotSG.getInstance().getConfiguration("tablist").getString("isInMatch.serverName"), player)));

            objects.add(new BufferedTabObject().slot(43).text(PotSG.getInstance().getConfiguration("tablist").getString("isInMatch.spectators_title")));
            objects.add(new BufferedTabObject().slot(44).text(replace(PotSG.getInstance().getConfiguration("tablist").getString("isInMatch.spectatorsAmount"), player)));

            int playerSlot = 6;
            for (Player online : Bukkit.getServer().getOnlinePlayers()) {
                objects.add(new BufferedTabObject().slot(playerSlot).text((playerManager.isSpectator(online) ? "&c&l× &7" : "&7") + online.getPlayer().getName()));
                playerSlot++;
                if (playerSlot == 18) {
                    playerSlot = 26;
                }
                if (playerSlot == 38) {
                    playerSlot = 46;
                }
                if (playerSlot == 58) {
                    break;
                }
            }

            objects.add(new BufferedTabObject().slot(20).text(PotSG.getInstance().getConfiguration("tablist").getString("tablist.bottom_left")));
            objects.add(new BufferedTabObject().slot(40).text(PotSG.getInstance().getConfiguration("tablist").getString("tablist.bottom_center")));
            objects.add(new BufferedTabObject().slot(60).text(PotSG.getInstance().getConfiguration("tablist").getString("tablist.bottom_right")));
        }
        return objects;
    }

    public String replace(String string, Player player) {
        PlayerData data = PlayerDataManager.getInstance().getByUUID(player.getUniqueId());
        GameManager gameManager = GameManager.getInstance();
        PlayerManager playerManager = PlayerManager.getInstance();

        return string
                .replaceAll("<playerName>", player.getName())
                .replaceAll("<server_name>", gameManager.getServerName())
                .replaceAll("<max_players>", String.valueOf(gameManager.getMaxPlayers()))
                .replaceAll("<require_players>", String.valueOf(gameManager.getRequiredPlayersToJoin()))
                .replaceAll("<countdown_players>", String.valueOf(playerManager.getLobbyPlayers().size()))
                .replaceAll("<prematch_players_size>", String.valueOf(playerManager.getPrematchPlayers().size()))

                .replaceAll("<game_players>", String.valueOf(playerManager.getGamePlayers().size()))
                .replaceAll("<spectators_size>", String.valueOf(playerManager.getSpectatorPlayers().size()))

                .replaceAll("<playerKills>", String.valueOf(data.getKills().getAmount()))
                .replaceAll("<playerDeaths>", String.valueOf(data.getDeaths().getAmount()))
                .replaceAll("<playerKDR>", String.valueOf(data.getKdr()))
                .replaceAll("<playerWins>", String.valueOf(data.getWins().getAmount()))
                ;
    }

}
