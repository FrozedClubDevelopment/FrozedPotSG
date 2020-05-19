package me.elb1to.frozedsg.utils.inventories;

import me.elb1to.frozedsg.PotSG;
import me.elb1to.frozedsg.managers.PlayerManager;
import me.elb1to.frozedsg.utils.ItemBuilder;
import me.elb1to.frozedsg.utils.chat.Color;
import me.elb1to.frozedsg.utils.pagination.PaginatedMenu;
import me.elb1to.frozedsg.utils.pagination.buttons.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class AlivePlayersInventory extends PaginatedMenu {

    @Override
    public String getPrePaginatedTitle(Player player) {
        return Color.translate(PotSG.getInstance().getConfiguration("inventory").getString("player-alive-inventory.title"));
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();
        PlayerManager.getInstance().getGamePlayers().forEach(online -> {
            buttons.put(buttons.size(), new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    ItemBuilder item = new ItemBuilder(Material.SKULL_ITEM);
                    item.setDurability(3);
                    item.setName(PotSG.getInstance().getConfiguration("inventory").getString("player-alive-inventory.name").replaceAll("<player_name>", online.getName()));
                    for (String lore : PotSG.getInstance().getConfiguration("inventory").getStringList("player-alive-inventory.lore")) {
                        item.addLoreLine(lore
                                .replaceAll("<player_name>", online.getName())
                        );
                    }
                    return item.toItemStack();
                }
            });
        });
        return buttons;
    }
}
