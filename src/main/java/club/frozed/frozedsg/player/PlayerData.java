package club.frozed.frozedsg.player;

import club.frozed.frozedsg.PotSG;
import club.frozed.frozedsg.enums.PlayerState;
import club.frozed.frozedsg.managers.GameManager;
import club.frozed.frozedsg.utils.ItemBuilder;
import com.mongodb.client.model.Filters;
import lombok.Data;
import club.frozed.frozedsg.managers.MongoManager;
import club.frozed.frozedsg.utils.Cooldown;
import club.frozed.frozedsg.utils.RespawnInfo;
import club.frozed.frozedsg.utils.Setting;
import club.frozed.frozedsg.utils.chat.Color;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@Data
public class PlayerData {
    private PlayerState state = PlayerState.LOBBY;
    private UUID uuid;
    private String name;
    private Stat kills = new Stat();
    private Stat deaths = new Stat();
    private Stat wins = new Stat();
    private Stat gamesPlayed = new Stat();
    private Stat points = new Stat();
    private Stat goldenApplesEaten = new Stat();
    private Stat bowShots = new Stat();
    private Stat killStreak = new Stat();
    private Stat gameKills = new Stat();
    private Stat chestBroke = new Stat();
    private Stat potionSplashed = new Stat();
    private Stat potionDrank = new Stat();
    private Location lastChestOpenedLocation = null;
    private Cooldown enderpearlCooldown;
    private Cooldown combatCooldown;
    private RespawnInfo respawnInfo = null;
    private boolean specChat = true;
    private List<Setting> settings = new ArrayList<>();


    public PlayerData(UUID uuid) {
        this.uuid = uuid;
        this.name = Bukkit.getOfflinePlayer(uuid).getName();

        settings.add(new Setting("ChestClick", Material.CHEST, true, 0, "You should not see this"));
        settings.add(new Setting("GlassBorder", Material.STAINED_GLASS, true, 14,0,"You should not see this"));
        settings.add(new Setting("Scoreboard", Material.ITEM_FRAME, true, 0,"You should not see this"));
        settings.add(new Setting("Lightning-On-Kill", Material.SKULL_ITEM, true, 1, 0,"You should not see this"));
        settings.add(new Setting("Chest-Auto-Pickup", Material.ENDER_CHEST, false, 50, "You should not see this"));
    }

    public Setting getSettingByName(String name) {
        return settings.stream().filter(setting -> setting.getName().equals(name)).findFirst().orElse(null);
    }

    public Inventory getSettingsInventory() {
        Inventory inv = Bukkit.createInventory(null, PotSG.getInstance().getConfiguration("inventory").getInt("settings-inventory.size"), Color.translate(PotSG.getInstance().getConfiguration("inventory").getString("settings-inventory.title")));

        inv.addItem(new ItemStack(Material.AIR));
        settings.forEach(setting -> {
            ItemBuilder item = new ItemBuilder(setting.getMaterial());
            //int required = setting.getRequiredPoints() - getPoints().getAmount();
            item.setDurability(setting.getData());
            item.setName(PotSG.getInstance().getConfiguration("inventory").getString("settings-inventory.name").replaceAll("<name>", setting.getName()));

            for (String string : PotSG.getInstance().getConfiguration("inventory").getStringList("settings-inventory.settings-line")) {
                item.addLoreLine(string);
            }

            if (setting.getDescription().length > 0) {
                if (setting.getName().equals("ChestClick")) {
                    for (String description : PotSG.getInstance().getConfiguration("inventory").getStringList("settings-inventory.description.chestclick")) {
                        item.addLoreLine(description);
                    }
                    settingFiller(setting, item);
                    inv.setItem(0, item.toItemStack());
                }

                if (setting.getName().equals("GlassBorder")) {
                    for (String description : PotSG.getInstance().getConfiguration("inventory").getStringList("settings-inventory.description.glass-border")) {
                        item.addLoreLine(description);
                    }
                    settingFiller(setting, item);
                    inv.setItem(2, item.toItemStack());
                }

                if (setting.getName().equals("Scoreboard")) {
                    for (String description : PotSG.getInstance().getConfiguration("inventory").getStringList("settings-inventory.description.scoreboard")) {
                        item.addLoreLine(description);
                    }
                    settingFiller(setting, item);
                    inv.setItem(4, item.toItemStack());
                }

                if (setting.getName().equals("Lightning-On-Kill")) {
                    for (String description : PotSG.getInstance().getConfiguration("inventory").getStringList("settings-inventory.description.lightning-on-kill")) {
                        item.addLoreLine(description);
                    }
                    settingFiller(setting, item);
                    inv.setItem(6, item.toItemStack());
                }

                if (setting.getName().equals("Chest-Auto-Pickup")) {
                    for (String description : PotSG.getInstance().getConfiguration("inventory").getStringList("settings-inventory.description.chest-auto-pickup")) {
                        item.addLoreLine(description);
                    }
                    settingWithPointsFiller(setting, item);
                    inv.setItem(8, item.toItemStack());
                }
            }
        });
        return inv;
    }

    private void settingFiller(Setting setting, ItemBuilder item) {
        for (String string : PotSG.getInstance().getConfiguration("inventory").getStringList("settings-inventory.info-status")) {
            item.addLoreLine(string
                    .replaceAll("<name>", String.valueOf(setting.getName()))
                    .replaceAll("<settings_status>", (setting.isEnabled() ? PotSG.getInstance().getConfiguration("inventory").getString("enabled") : PotSG.getInstance().getConfiguration("inventory").getString("disabled")))
            );
        }
        for (String string : PotSG.getInstance().getConfiguration("inventory").getStringList("settings-inventory.settings-line")) {
            item.addLoreLine(string);
        }
    }

    private void settingWithPointsFiller(Setting setting, ItemBuilder item) {
        int required = setting.getRequiredPoints() - getPoints().getAmount();
        for (String string : PotSG.getInstance().getConfiguration("inventory").getStringList("settings-inventory.info-status")) {
            item.addLoreLine(string
                    .replaceAll("<name>", String.valueOf(setting.getName()))
                    .replaceAll("<settings_status>", (setting.isEnabled() ? PotSG.getInstance().getConfiguration("inventory").getString("enabled") : PotSG.getInstance().getConfiguration("inventory").getString("disabled")))
            );
        }
        if (getPoints().getAmount() < setting.getRequiredPoints()) {
            for (String string : PotSG.getInstance().getConfiguration("inventory").getStringList("settings-inventory.not-enough-points-lore")) {
                item.addLoreLine(string
                        .replaceAll("<required_points>", String.valueOf(setting.getRequiredPoints()))
                        .replaceAll("<needed_points>", String.valueOf(required))
                );
            }
        }
        for (String string : PotSG.getInstance().getConfiguration("inventory").getStringList("settings-inventory.settings-line")) {
            item.addLoreLine(string);
        }
    }

    public double getKdr() {
        double kd;
        if (kills.getAmount() > 0 && deaths.getAmount() == 0) {
            kd = kills.getAmount();
        } else if (kills.getAmount() == 0 && deaths.getAmount() == 0) {
            kd = 0.0;
        } else {
            kd = kills.getAmount() / deaths.getAmount();
        }
        return kd;
    }

    public boolean hasData() {
        Document document = MongoManager.getInstance().getStatsCollection().find(Filters.eq("info", (GameManager.getInstance().isServerPremium() ? uuid : name))).first();
        return document != null;
    }

    public void load() {
        if (!hasData()) {
            return;
        }
        Document document = MongoManager.getInstance().getStatsCollection().find(Filters.eq("info", (GameManager.getInstance().isServerPremium() ? uuid : name))).first();

        kills.setAmount(document.getInteger("kills"));
        deaths.setAmount(document.getInteger("deaths"));
        wins.setAmount(document.getInteger("wins"));
        gamesPlayed.setAmount(document.getInteger("gamesPlayed"));
        points.setAmount(document.getInteger("points"));
        goldenApplesEaten.setAmount(document.getInteger("goldenApplesEaten"));
        bowShots.setAmount(document.getInteger("bowShots"));
        killStreak.setAmount(document.getInteger("killStreak"));
        chestBroke.setAmount(document.getInteger("chestBroke"));
        potionSplashed.setAmount(document.getInteger("potionSplashed"));
        potionDrank.setAmount(document.getInteger("potionDrank"));
        try {
            settings.forEach(setting -> setting.setEnabled(document.getBoolean(setting.getName())));
        } catch (Exception e) {
            save();
        }
    }

    public void save() {
        Document document = new Document("info", (GameManager.getInstance().isServerPremium() ? uuid : name));

        if (!hasData()) {
            document.put("name", name);
            document.put("kills", kills.getAmount());
            document.put("deaths", deaths.getAmount());
            document.put("wins", wins.getAmount());
            document.put("gamesPlayed", gamesPlayed.getAmount());
            document.put("points", points.getAmount());
            document.put("goldenApplesEaten", goldenApplesEaten.getAmount());
            document.put("bowShots", bowShots.getAmount());
            document.put("killStreak", killStreak.getAmount());
            document.put("chestBroke", chestBroke.getAmount());
            document.put("potionSplashed", potionSplashed.getAmount());
            document.put("potionDrank", potionDrank.getAmount());
            settings.forEach(setting -> document.put(setting.getName(), setting.isEnabled()));

            MongoManager.getInstance().getStatsCollection().insertOne(document);
        } else {
            document.put("name", name);
            document.put("kills", kills.getAmount());
            document.put("deaths", deaths.getAmount());
            document.put("wins", wins.getAmount());
            document.put("gamesPlayed", gamesPlayed.getAmount());
            document.put("points", points.getAmount());
            document.put("goldenApplesEaten", goldenApplesEaten.getAmount());
            document.put("bowShots", bowShots.getAmount());
            document.put("killStreak", killStreak.getAmount());
            document.put("chestBroke", chestBroke.getAmount());
            document.put("potionSplashed", potionSplashed.getAmount());
            document.put("potionDrank", potionDrank.getAmount());
            settings.forEach(setting -> document.put(setting.getName(), setting.isEnabled()));

            MongoManager.getInstance().getStatsCollection().replaceOne(Filters.eq("info", (GameManager.getInstance().isServerPremium() ? uuid : name)), document);
        }
    }
}
