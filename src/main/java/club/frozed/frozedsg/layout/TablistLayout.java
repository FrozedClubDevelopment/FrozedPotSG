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

            objects.add(new BufferedTabObject().slot(3).text(PotSG.getInstance().getConfiguration("tablist").getString("isInLobby.you")));
            objects.add(new BufferedTabObject().slot(4).text(ChatColor.translateAlternateColorCodes('&', "&fName: &b" + player.getName())));

            objects.add(new BufferedTabObject().slot(23).text(PotSG.getInstance().getConfiguration("tablist").getString("isInLobby.server")));
            objects.add(new BufferedTabObject().slot(24).text(ChatColor.translateAlternateColorCodes('&', "&fName: &b" + gameManager.getServerName())));

            objects.add(new BufferedTabObject().slot(43).text(PotSG.getInstance().getConfiguration("tablist").getString("isInLobby.state")));
            objects.add(new BufferedTabObject().slot(44).text(ChatColor.translateAlternateColorCodes('&', "&fPlayers: &b" + playerManager.getLobbyPlayers().size())));
            objects.add(new BufferedTabObject().slot(45).text(ChatColor.translateAlternateColorCodes('&', "&fMax Players: &b" + gameManager.getMaxPlayers())));
            objects.add(new BufferedTabObject().slot(46).text(ChatColor.translateAlternateColorCodes('&', "&fRequired: &b" + gameManager.getRequiredPlayersToJoin())));

            int x = player.getLocation().getBlockX();
            int z = player.getLocation().getBlockZ();
            objects.add(new BufferedTabObject().slot(48).text(PotSG.getInstance().getConfiguration("tablist").getString("isInLobby.location")));
            objects.add(new BufferedTabObject().slot(49).text(ChatColor.translateAlternateColorCodes('&', "&7(&f" + x + "&7, &f" + z + "&7)")));

            objects.add(new BufferedTabObject().slot(51).text(PotSG.getInstance().getConfiguration("tablist").getString("isInLobby.player_info")));
            objects.add(new BufferedTabObject().slot(52).text(ChatColor.translateAlternateColorCodes('&', "&fKills: &b" + data.getKills().getAmount())));
            objects.add(new BufferedTabObject().slot(53).text(ChatColor.translateAlternateColorCodes('&', "&fDeaths: &b" + data.getDeaths().getAmount())));
            objects.add(new BufferedTabObject().slot(54).text(ChatColor.translateAlternateColorCodes('&', "&fKDR: &b" + data.getKdr())));
            objects.add(new BufferedTabObject().slot(55).text(ChatColor.translateAlternateColorCodes('&', "&fWins: &b" + data.getWins().getAmount())));

            objects.add(new BufferedTabObject().slot(20).text(PotSG.getInstance().getConfiguration("tablist").getString("tablist.bottom_left")));
            objects.add(new BufferedTabObject().slot(40).text(PotSG.getInstance().getConfiguration("tablist").getString("tablist.bottom_center")));
            objects.add(new BufferedTabObject().slot(60).text(PotSG.getInstance().getConfiguration("tablist").getString("tablist.bottom_right")));

            /* Top 5 Leaderboard
            objects.add(new BufferedTabObject().slot(8).text(PotSG.getInstance().getConfiguration("tablist").getString("isInLobby.top5wins")));
            for (top5Slot = 9; top5Slot < 14; top5Slot++) {
                objects.add(new BufferedTabObject().slot(top5Slot).text(getTop5Wins()));
            }*/
        }

        if (gameManager.getGameState() == GameState.PREMATCH || gameManager.getGameState() == GameState.INGAME || gameManager.getGameState() == GameState.ENDING) {
            objects.add(new BufferedTabObject().slot(21).text(PotSG.getInstance().getConfiguration("tablist").getString("isInMatch.title")));

            objects.add(new BufferedTabObject().slot(3).text(PotSG.getInstance().getConfiguration("tablist").getString("isInMatch.players_title")));
            objects.add(new BufferedTabObject().slot(4).text(ChatColor.translateAlternateColorCodes('&', "&b" + playerManager.getGamePlayers().size() + "&f players")));

            objects.add(new BufferedTabObject().slot(23).text(PotSG.getInstance().getConfiguration("tablist").getString("isInMatch.server")));
            objects.add(new BufferedTabObject().slot(24).text(ChatColor.translateAlternateColorCodes('&', "&fName: &b" + gameManager.getServerName())));

            objects.add(new BufferedTabObject().slot(43).text(PotSG.getInstance().getConfiguration("tablist").getString("isInMatch.spectators_title")));
            objects.add(new BufferedTabObject().slot(44).text(ChatColor.translateAlternateColorCodes('&', "&b" + playerManager.getSpectatorPlayers().size() + "&f watching")));

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

    /*public String getTop5Wins() {
        int pos = 1;
        List<Document> top5Wins = MongoManager.getInstance().getStatsCollection().find().limit(5).sort(new BasicDBObject("wins", -1)).into(new ArrayList<>());

        for (Document document : top5Wins) {
            String formattedTop5 = Color.translate(
                    "&f"
                    + pos
                    + ". &b"
                    + document.getString("name") == null ? "null" : document.getString("name")
                    + " &8(" + document.get("wins").toString() + "&8)");
            pos++;
        }
        return getTop5Wins();
    }*/

}
