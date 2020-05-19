package me.elb1to.frozedsg.managers;

import lombok.Data;
import lombok.Getter;
import me.elb1to.frozedsg.PotSG;
import me.elb1to.frozedsg.player.PlayerData;
import me.elb1to.frozedsg.utils.ItemBuilder;
import me.elb1to.frozedsg.utils.chat.Color;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

@Data
public class InventoryManager {
    @Getter public static InventoryManager instance;

    public InventoryManager() {
        instance = this;
    }

    public Inventory getStatsInventory(PlayerData data) {
        Inventory inv = Bukkit.createInventory(null, 9, Color.translate(PotSG.getInstance().getConfiguration("inventory").getString("stats-inventory.title").replaceAll("<target>", data.getName())));

        ItemBuilder combat = new ItemBuilder(Material.DIAMOND_SWORD);
        combat.setName(PotSG.getInstance().getConfiguration("inventory").getString("stats-inventory.combat.name"));
        for (String string : PotSG.getInstance().getConfiguration("inventory").getStringList("stats-inventory.combat.lore")) {
            combat.addLoreLine(replace(string, data));
        }

        ItemBuilder misc = new ItemBuilder(Material.NETHER_STAR);
        misc.setName(PotSG.getInstance().getConfiguration("inventory").getString("stats-inventory.misc.name"));
        for (String string : PotSG.getInstance().getConfiguration("inventory").getStringList("stats-inventory.misc.lore")) {
            misc.addLoreLine(replace(string, data));
        }

        ItemBuilder potion = new ItemBuilder(Material.POTION);
        potion.setName(PotSG.getInstance().getConfiguration("inventory").getString("stats-inventory.potion.name"));
        for (String string : PotSG.getInstance().getConfiguration("inventory").getStringList("stats-inventory.potion.lore")) {
            potion.addLoreLine(replace(string, data));
        }

        ItemBuilder other = new ItemBuilder(Material.DIAMOND);
        other.setName(PotSG.getInstance().getConfiguration("inventory").getString("stats-inventory.other.name"));
        for (String string : PotSG.getInstance().getConfiguration("inventory").getStringList("stats-inventory.other.lore")) {
            other.addLoreLine(replace(string, data));
        }

        inv.setItem(1,combat.toItemStack());
        inv.setItem(3,misc.toItemStack());
        inv.setItem(5,potion.toItemStack());
        inv.setItem(7,other.toItemStack());

        return inv;
    }

    public String replace(String s, PlayerData data) {
        return s
                .replaceAll("<target_kills>", String.valueOf(data.getKills().getAmount()))
                .replaceAll("<target_deaths>", String.valueOf(data.getDeaths().getAmount()))
                .replaceAll("<target_kdr>", String.valueOf(data.getKdr()))

                .replaceAll("<target_games_played>", String.valueOf(data.getGamesPlayed().getAmount()))
                .replaceAll("<target_wins>", String.valueOf(data.getWins().getAmount()))
                .replaceAll("<target_points>", String.valueOf(data.getPoints().getAmount()))

                .replaceAll("<target_golden_apples_eaten>", String.valueOf(data.getGoldenApplesEaten().getAmount()))
                .replaceAll("<target_bow_shots>", String.valueOf(data.getBowShots().getAmount()))
                .replaceAll("<target_chest_opened>", String.valueOf(data.getChestBroke().getAmount()))

                .replaceAll("<target_potion_splashed>", String.valueOf(data.getPotionSplashed().getAmount()))
                .replaceAll("<target_potion_drank>", String.valueOf(data.getPotionDrank().getAmount()))

                ;
    }
}
