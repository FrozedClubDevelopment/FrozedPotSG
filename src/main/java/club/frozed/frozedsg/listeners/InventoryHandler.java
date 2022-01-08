package club.frozed.frozedsg.listeners;

import club.frozed.frozedsg.PotSG;
import club.frozed.frozedsg.enums.PlayerState;
import club.frozed.frozedsg.managers.*;
import club.frozed.frozedsg.player.PlayerData;
import club.frozed.frozedsg.utils.Utils;
import club.frozed.frozedsg.utils.chat.Color;
import club.frozed.frozedsg.utils.inventories.AlivePlayersInventory;
import club.frozed.frozedsg.utils.inventories.LobbyInventoryPlayers;
import club.frozed.frozedsg.utils.leaderboards.LeaderboardManager;
import lombok.Getter;
import lombok.Setter;
import club.frozed.frozedsg.managers.*;
import club.frozed.frozedsg.utils.Setting;
import club.frozed.frozedsg.utils.Chest;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

@Getter
@Setter
public class InventoryHandler implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();
        PlayerData data = PlayerDataManager.getInstance().getByUUID(player.getUniqueId());

        if (item == null) {
            return;
        }

        if (event.getClickedInventory().getTitle().contains(Color.translate(PotSG.getInstance().getConfiguration("inventory").getString("player-alive-inventory.title")))) {
            if (item.getType().equals(Material.SKULL_ITEM)) {
                String name = ChatColor.stripColor(item.getItemMeta().getDisplayName());

                if (Bukkit.getPlayer(name) != null) {
                    player.teleport(Bukkit.getPlayer(name));
                    player.sendMessage(Color.translate(GameManager.getInstance().getGamePrefix() + PotSG.getInstance().getConfiguration("messages").getString("teleport-success"))
                            .replaceAll("<target>", name)
                    );
                }
            }
        }

        if (data != null) {
            if (event.getInventory().getTitle().equalsIgnoreCase(data.getSettingsInventory().getTitle())) {
                event.setCancelled(true);
                if (!event.getClickedInventory().getTitle().equalsIgnoreCase(data.getSettingsInventory().getTitle())) {
                    return;
                }
                if (!item.hasItemMeta()) {
                    return;
                }
                if (!item.getItemMeta().hasDisplayName()) {
                    return;
                }
                Setting setting = data.getSettingByName(ChatColor.stripColor(item.getItemMeta().getDisplayName()));
                if (setting != null) {
                    if (data.getPoints().getAmount() < setting.getRequiredPoints()) {
                        player.closeInventory();
                        player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1F, 1F);
                        player.sendMessage(GameManager.getInstance().getGamePrefix() + Color.translate(PotSG.getInstance().getConfiguration("messages").getString("not-enough-point")));
                        return;
                    }
                    setting.setEnabled(!setting.isEnabled());
                    player.getOpenInventory().getTopInventory().setContents(data.getSettingsInventory().getContents());
                    player.updateInventory();
                    player.playSound(player.getLocation(), Sound.BAT_TAKEOFF, 1F, 1F);
                }
            }
        }

        if (!data.getState().equals(PlayerState.INGAME)) {
            if (event.getAction().equals(InventoryAction.SWAP_WITH_CURSOR) ||
                    event.getAction().equals(InventoryAction.HOTBAR_SWAP)) {
                event.setCancelled(true);
            }
            if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                event.setCancelled(true);
            }
        }
        if (PlayerManager.getInstance().isSpectator(player)) {
            event.setCancelled(true);
        }
        if (event.getInventory().getTitle().equalsIgnoreCase(ChestsManager.getInstance().chestsInventory().getTitle())) {
            event.setCancelled(true);
        }
        if (event.getInventory().getTitle().equalsIgnoreCase(LeaderboardManager.getInstance().getInventory(player).getTitle())) {
            event.setCancelled(true);
        }
        if (event.getInventory().getTitle().contains(Color.translate(PotSG.getInstance().getConfiguration("inventory").getString("stats-inventory.title").replaceAll("<target>", "")))) {
            event.setCancelled(true);
        }

        if (event.getClickedInventory().getTitle().equalsIgnoreCase(ChestsManager.getInstance().chestsInventory().getTitle())) {
            if (item.getType().equals(Material.CHEST)) {
                int number = Integer.parseInt(ChatColor.stripColor(item.getItemMeta().getDisplayName()));
                Chest chest = ChestsManager.getInstance().getChest(number);
                player.openInventory(ChestsManager.getInstance().chestInventory(chest));
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handlePlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        PlayerData data = PlayerDataManager.getInstance().getByUUID(player.getUniqueId());

        if (item == null) {
            return;
        }

        if (!item.hasItemMeta() && !item.getItemMeta().hasDisplayName()) {
            return;
        }
        if (!event.getAction().equals(Action.RIGHT_CLICK_AIR) && !event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        if (PlayerManager.getInstance().isSpectator(player)) {
            if (item.getType().equals(Material.ITEM_FRAME)) {
                event.setCancelled(true);
            }
        }
        if (data.getState().equals(PlayerState.LOBBY)) {
            if (item.getType().equals(Material.EMERALD)) {
                player.openInventory(LeaderboardManager.getInstance().getInventory(player));
            }
            if (item.getType().equals(Material.CHEST)) {
                player.performCommand("gamesettings");
            }
            if (item.getType().equals(Material.WATCH)) {
                new LobbyInventoryPlayers().openMenu(player);
            }
            if (item.getType().equals(Material.SKULL_ITEM)) {
                player.openInventory(InventoryManager.getInstance().getStatsInventory(data));
            }
        }
        if (PlayerManager.getInstance().isSpectator(player)) {
            if (item.getType().equals(Material.ITEM_FRAME)) {
                if (PlayerManager.getInstance().getGamePlayers().size() > 0) {
                    new AlivePlayersInventory().openMenu(player);
                } else {
                    player.sendMessage(Color.translate(GameManager.getInstance().getGamePrefix() + PotSG.getInstance().getConfiguration("messages").getString("no-alive-players")));
                }
            }
            if (item.getType().equals(Material.REDSTONE)) {
                if (!GameManager.getInstance().isToUseLobby()) {
                    player.kickPlayer(Color.translate(GameManager.getInstance().getGamePrefix() + PotSG.getInstance().getConfiguration("messages").getString("kicked-from-server"))
                            .replaceAll("<server_name>", GameManager.getInstance().getServerName())
                    );
                } else {
                    Utils.connectPlayer(player, GameManager.getInstance().getLobbyFallbackServer());
                    player.sendMessage(Color.translate(GameManager.getInstance().getGamePrefix() + PotSG.getInstance().getConfiguration("messages").getString("bungeecord-send-to-lobby"))
                            .replaceAll("<lobby_server_name>", GameManager.getInstance().getLobbyFallbackServer())
                    );
                }
            }
            if (item.getType().equals(Material.WATCH)) {
                Utils.randomPlayer(player);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleInspectInventory(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();
        ItemStack item = player.getItemInHand();
        if (item == null) {
            return;
        }
        if (!item.hasItemMeta()) {
            return;
        }
        if (!item.getItemMeta().hasDisplayName()) {
            return;
        }
        if (entity instanceof Player) {
            if (PlayerManager.getInstance().isSpectator(player)) {
                Player rightClicked = (Player) entity;
                if (item.getType() == Material.BOOK) {
                    player.sendMessage(GameManager.getInstance().getGamePrefix() + Color.translate(PotSG.getInstance().getConfiguration("messages").getString("open-player-inventory"))
                            .replaceAll("<target>", rightClicked.getName())
                    );
                    player.openInventory(Utils.createInventoryOther(rightClicked));
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleSpectatorInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        if (PlayerManager.getInstance().isSpectator(player)) {
            if ((action.equals(Action.RIGHT_CLICK_BLOCK))) {
                Block block = event.getClickedBlock();
                if (((block.getType().equals(Material.CHEST)) || (block.getType().equals(Material.TRAPPED_CHEST)))
                        && (!player.isSneaking())) {
                    event.setCancelled(true);
                    player.sendMessage(GameManager.getInstance().getGamePrefix() + Color.translate(PotSG.getInstance().getConfiguration("messages").getString("cannot-open-chest")));
                }
            }
            if (PlayerManager.getInstance().isSpectator(player)) {
                if (action.equals(Action.PHYSICAL)) {
                    event.setCancelled(true);
                }
                if (action.equals(Action.RIGHT_CLICK_BLOCK)) {
                    Block block = event.getClickedBlock();
                    if ((block.getType().equals(Material.LEVER)) || (block.getType().equals(Material.WOOD_BUTTON))
                            || (block.getType().equals(Material.STONE_BUTTON))) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}
