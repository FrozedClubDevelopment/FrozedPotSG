package club.frozed.frozedsg.utils.leaderboards;

import com.mongodb.BasicDBObject;
import lombok.Getter;
import lombok.Setter;
import club.frozed.frozedsg.PotSG;
import club.frozed.frozedsg.managers.MongoManager;
import club.frozed.frozedsg.managers.PlayerDataManager;
import club.frozed.frozedsg.player.PlayerData;
import club.frozed.frozedsg.utils.ItemBuilder;
import club.frozed.frozedsg.utils.chat.Color;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class LeaderboardManager {
    @Getter public static LeaderboardManager instance;
    private List<Leaderboard> leaderboardList = new ArrayList<>();
    private List<Document> top5Wins = MongoManager.getInstance().getStatsCollection().find().limit(5).sort(new BasicDBObject("wins", -1)).into(new ArrayList<>());
    private List<Document> allStatistics = MongoManager.getInstance().getStatsCollection().find().into(new ArrayList<>());

    public LeaderboardManager() {
        instance = this;
        leaderboardList.addAll(Arrays.asList(
                new Leaderboard(Material.EMERALD, "Kills", "kills", PotSG.getInstance().getConfiguration("inventory").getBoolean("leaderboard.kills.enabled")),
                new Leaderboard(Material.CHEST, "Deaths", "deaths", PotSG.getInstance().getConfiguration("inventory").getBoolean("leaderboard.deaths.enabled")),
                new Leaderboard(Material.NETHER_STAR, "Wins", "wins", PotSG.getInstance().getConfiguration("inventory").getBoolean("leaderboard.wins.enabled")),
                new Leaderboard(Material.GOLD_NUGGET, "Points", "points", PotSG.getInstance().getConfiguration("inventory").getBoolean("leaderboard.points.enabled")),
                new Leaderboard(Material.BOOK, "Games Played", "gamesPlayed", PotSG.getInstance().getConfiguration("inventory").getBoolean("leaderboard.games-played.enabled")),
                new Leaderboard(Material.DIAMOND, "KillStreak", "killStreak", PotSG.getInstance().getConfiguration("inventory").getBoolean("leaderboard.kill-streak.enabled")),
                new Leaderboard(Material.GOLDEN_APPLE, "Golden Apple Eaten", "goldenApplesEaten", PotSG.getInstance().getConfiguration("inventory").getBoolean("leaderboard.golden-apple-eaten.enabled")),
                new Leaderboard(Material.BOW, "Bow Shots", "bowShots", PotSG.getInstance().getConfiguration("inventory").getBoolean("leaderboard.bow-shots.enabled")),
                new Leaderboard(Material.CHEST, "Chest Broke", "chestBroke", PotSG.getInstance().getConfiguration("inventory").getBoolean("leaderboard.chest-broke.enabled")),
                new Leaderboard(Material.POTION, "Potion Splashed", "potionSplashed", PotSG.getInstance().getConfiguration("inventory").getBoolean("leaderboard.potion-splashed.enabled")),
                new Leaderboard(Material.POTION, "Potion Drank", "potionDrank", PotSG.getInstance().getConfiguration("inventory").getBoolean("leaderboard.potion-drank.enabled"))
        ));
    }

    public void updateAllLeaderboards() {
        new BukkitRunnable() {
            public void run() {
                for (Leaderboard leaderboard : getLeaderboardList()) {
                    leaderboard.load();
                }
                allStatistics = MongoManager.getInstance().getStatsCollection().find().into(new ArrayList<>());
                top5Wins = MongoManager.getInstance().getStatsCollection().find().limit(5).sort(new BasicDBObject("wins", -1)).into(new ArrayList<>());
            }
        }.runTaskAsynchronously(PotSG.getInstance());
    }

    public Inventory getInventory(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, Color.translate(PotSG.getInstance().getConfiguration("inventory").getString("leaderboard-inventory.title")));
        PlayerData data = PlayerDataManager.getInstance().getByUUID(player.getUniqueId());

        ItemBuilder stats = new ItemBuilder(Material.SKULL_ITEM);
        stats.setDurability(3);
        stats.setName("&3Your Statistics");
        stats.addLoreLine("&7&m-----------------------");
        stats.addLoreLine("&bKills&7: &f" + data.getKills().getAmount());
        stats.addLoreLine("&bDeaths&7: &f" + data.getDeaths().getAmount());
        stats.addLoreLine("&bWins&7: &f" + data.getWins().getAmount());
        stats.addLoreLine("&bPoints&7: &f" + data.getPoints().getAmount());
        stats.addLoreLine("&bGames Played&7: &f" + data.getGamesPlayed().getAmount());
        stats.addLoreLine("&bKDR&7: &f" + data.getKdr());
        stats.addLoreLine("&7&m-----------------------");

        inv.setItem(13, stats.toItemStack());

        ItemBuilder item;
        int i = 27;
        for (Leaderboard leaderboards : getLeaderboardList()) {
            if (leaderboards.isEnabled()) {
                if (leaderboards.getName().equals("Potion Splashed")) {
                    item = new ItemBuilder(Material.POTION).setDurability(16421);
                } else {
                    item = new ItemBuilder(leaderboards.getMaterial());
                }

                item.setName(PotSG.getInstance().getConfiguration("inventory").getString("leaderboard-inventory.name").replaceAll("<name>", leaderboards.getName()));

                if (leaderboards.getFormats().isEmpty()) {
                    for (String string : PotSG.getInstance().getConfiguration("inventory").getStringList("leaderboard-inventory.empty-leaderboard")) {
                        item.addLoreLine(string);
                    }
                } else {
                    item.addLoreLine("&7&m-----------------------");
                    for (String format : leaderboards.getFormats()) {
                        item.addLoreLine(format);
                    }
                    item.addLoreLine("&7&m-----------------------");
                }

                inv.setItem(i, item.toItemStack());
                i = i + 2;

                if (i == 45) {
                    i = i + 3;
                }
            }
        }
        return inv;
    }
}
