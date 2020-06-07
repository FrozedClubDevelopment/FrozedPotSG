package club.frozed.frozedsg.managers;

import club.frozed.frozedsg.PotSG;
import club.frozed.frozedsg.utils.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import club.frozed.frozedsg.utils.Chest;
import club.frozed.frozedsg.utils.InventoryToBase64;
import club.frozed.frozedsg.utils.Utils;
import club.frozed.frozedsg.utils.chat.Color;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Getter
@Setter
public class ChestsManager implements Listener {
    @Getter public static ChestsManager instance;
    private List<Chest> chests = new ArrayList<>();

    public ChestsManager() {
        instance = this;
        PotSG.getInstance().getServer().getPluginManager().registerEvents(this, PotSG.getInstance());
        for (int i = 1; i <= 54; i++) {
            if (PotSG.getInstance().getConfiguration("chests").getConfiguration().getConfigurationSection("CHESTS") != null) {
                if (!PotSG.getInstance().getConfiguration("chests").getConfiguration().getConfigurationSection("CHESTS").contains(String.valueOf(i))) {
                    chests.add(new Chest(null, i));
                }
            } else {
                chests.add(new Chest(null, i));
            }
        }
        loadChestsFromConfig();
    }

    public void updateChestItems(int chestNumber, ItemStack[] items) {
        Chest chest = chests.get(chestNumber - 1);
        chest.setItems(items);
    }

    public Chest getChest(int chestNumber) {
        return chests.get(chestNumber - 1);
    }

    public void saveChestsToConfig() {
        PotSG.getInstance().getConfiguration("chests").getConfiguration().createSection("CHESTS");
        for (Chest chest : getChests()) {
            if (chest.getItems() != null) {
                PotSG.getInstance().getConfiguration("chests").getConfiguration().set("CHESTS." + chest.getNumber(), InventoryToBase64.itemToBase64(chest.getItems()));
            }
        }
        PotSG.getInstance().getConfiguration("chests").save();
    }

    public void loadChestsFromConfig() {
        if (PotSG.getInstance().getConfiguration("chests").getConfiguration().getConfigurationSection("CHESTS") == null) {
            return;
        }
        PotSG.getInstance().getConfiguration("chests").getConfiguration().getConfigurationSection("CHESTS").getKeys(false).forEach(key -> {
            if (Utils.isInteger(key)) {
                ItemStack[] items = new ItemStack[0];
                try {
                    items = InventoryToBase64.itemFromBase64(PotSG.getInstance().getConfiguration("chests").getString("CHESTS." + key));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                chests.add(new Chest(items, Integer.parseInt(key)));
            }
        });
    }

    public Inventory chestsInventory() {
        fixOrder();
        Inventory inv = Bukkit.createInventory(null, 54, Color.translate("Chests"));

        chests.forEach(key -> {
            ItemBuilder item = new ItemBuilder(Material.CHEST);
            item.setName("&a" + key.getNumber());
            item.addLoreLine("&7Click this to");
            item.addLoreLine("&7to edit items for this chest");
            if (key.getNumber() >= 46) {
                item.addLoreLine("&cNOTE: This is feast chest");
            }
            inv.addItem(item.toItemStack());
        });

        return inv;
    }

    public ItemStack[] getRandomItemsFromChests(boolean feast) {
        if (PotSG.getInstance().getConfiguration("chests").getConfiguration().getConfigurationSection("CHESTS") == null) {
            return null;
        }
        List<Chest> availableChests = new ArrayList<>();
        PotSG.getInstance().getConfiguration("chests").getConfiguration().getConfigurationSection("CHESTS").getKeys(false).forEach(key -> {
            if (Utils.isInteger(key)) {
                ItemStack[] items = new ItemStack[0];
                try {
                    items = InventoryToBase64.itemFromBase64(PotSG.getInstance().getConfiguration("chests").getString("CHESTS." + key));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (items != null && items.length > 0) {
                    availableChests.add(new Chest(items, 2));
                }
            }
        });
        if (availableChests.size() == 0) {
            return null;
        }
        if (feast) {
            int random = new Random().nextInt(9);
            return availableChests.get(45 + random).getItems();
        } else {
            int random = new Random().nextInt(45);
            return availableChests.get(random).getItems();
        }
    }

    public void fixOrder() {
        List<Chest> fixed = chests.stream().sorted(Comparator.comparing(Chest::getNumber)).collect(Collectors.toList());

        chests.clear();
        chests.addAll(fixed);
    }

    public Inventory chestInventory(Chest chest) {
        Inventory inv = Bukkit.createInventory(null, 27, Color.translate("&bChest " + chest.getNumber()));

        if (chest.getItems() != null) {
            inv.setContents(chest.getItems());
        }

        return inv;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handleInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();

        if (!event.getInventory().getTitle().contains(Color.translate("&aChest "))) {
            return;
        }
        if (event.getInventory().getContents() == null) {
            return;
        }
        ItemStack[] items = event.getInventory().getContents();
        String number = ChatColor.stripColor(event.getInventory().getTitle().split(" ")[1]);
        updateChestItems(Integer.parseInt(number), items);
        saveChestsToConfig();
        player.sendMessage(Color.translate("&bYou have successfully saved items for &f'Chest " + number + "'&b."));
    }
}
