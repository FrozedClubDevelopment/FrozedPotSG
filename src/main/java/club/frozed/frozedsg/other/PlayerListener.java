package club.frozed.frozedsg.other;

import club.frozed.frozedsg.PotSG;
import club.frozed.frozedsg.border.Border;
import club.frozed.frozedsg.border.BorderManager;
import club.frozed.frozedsg.enums.GameState;
import club.frozed.frozedsg.enums.PlayerState;
import club.frozed.frozedsg.events.*;
import club.frozed.frozedsg.managers.*;
import club.frozed.frozedsg.player.PlayerData;
import club.frozed.frozedsg.utils.Cooldown;
import club.frozed.frozedsg.utils.ItemBuilder;
import club.frozed.frozedsg.utils.RespawnInfo;
import club.frozed.frozedsg.utils.Utils;
import club.frozed.frozedsg.utils.chat.Color;
import club.frozed.frozedsg.utils.countdowns.RebootCountdown;
import club.frozed.frozedsg.utils.tasks.SplitedRoadProcessor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.BrewingStand;
import org.bukkit.block.Chest;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class PlayerListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void handleAsyncLogin(AsyncPlayerPreLoginEvent event) {
        PlayerDataManager.getInstance().handleCreateData(event.getUniqueId());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handlePlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        GameManager gameManager = GameManager.getInstance();
        player.recalculatePermissions();

        if (PotSG.getInstance().isPluginLoading() || WorldsManager.getInstance().getLobbyWorld() == null) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, Color.translate(PotSG.getInstance().getConfiguration("messages").getString("loading-server"))
                    .replaceAll("<server>", GameManager.getInstance().getServerName())
            );
        }
        if (player.hasPermission("frozedsg.join.started")) {
            return;
        }
        if (gameManager.getGameState().equals(GameState.INGAME)) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, Color.translate(PotSG.getInstance().getConfiguration("messages").getString("already-started")));
        } else if (gameManager.getGameState().equals(GameState.ENDING)) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, Color.translate(PotSG.getInstance().getConfiguration("messages").getString("game-end")));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handlePlayerDamageByPlayer(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player player = (Player) event.getEntity();
            Player target = (Player) event.getDamager();
            PlayerData playerData = PlayerDataManager.getInstance().getByUUID(player.getUniqueId());
            PlayerData targetData = PlayerDataManager.getInstance().getByUUID(target.getUniqueId());
            if (playerData.getState().equals(PlayerState.INGAME) && targetData.getState().equals(PlayerState.INGAME)) {
                Bukkit.getServer().getPluginManager().callEvent(new SGCombatTagEvent(player, target));
            }
            if (PlayerManager.getInstance().isSpectator(target)) {
                event.setCancelled(true);
            }
        } else if (event.getEntity() instanceof Player && event.getDamager() instanceof Projectile && !(event.getDamager() instanceof EnderPearl)) {
            Player target = (Player) ((Projectile) event.getDamager()).getShooter();
            Player player = (Player) event.getEntity();
            PlayerData playerData = PlayerDataManager.getInstance().getByUUID(player.getUniqueId());
            PlayerData targetData = PlayerDataManager.getInstance().getByUUID(target.getUniqueId());
            if (playerData.getState().equals(PlayerState.INGAME) && targetData.getState().equals(PlayerState.INGAME)) {
                Bukkit.getServer().getPluginManager().callEvent(new SGCombatTagEvent(player, target));
            }
            if (PlayerManager.getInstance().isSpectator(target)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void handleChat(AsyncPlayerChatEvent event) {
        if (!PotSG.getInstance().getConfiguration("config").getBoolean("CHAT-FORMAT.ENABLED")) {
            return;
        }
        String format = PotSG.getInstance().getConfiguration("config").getString("CHAT-FORMAT.FORMAT");
        format = format.replace("<player_display_name>", event.getPlayer().getDisplayName());
        format = format.replace("<message>", event.getMessage().replaceAll("%", "%%").replaceAll("\\$", "\\\\\\$"));
        event.setFormat(format);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        Player player = event.getPlayer();
        if (PotSG.getInstance().isPluginLoading() || WorldsManager.getInstance().getLobbyWorld() == null) {
            if (!GameManager.getInstance().isToUseLobby()) {
                player.kickPlayer(Color.translate(PotSG.getInstance().getConfiguration("messages").getString("loading-server"))
                        .replaceAll("<server>", GameManager.getInstance().getServerName())
                );
            } else {
                player.sendMessage(Color.translate(PotSG.getInstance().getConfiguration("messages").getString("loading-server"))
                        .replaceAll("<server>", GameManager.getInstance().getServerName())
                );
                Utils.connectPlayer(player, GameManager.getInstance().getLobbyFallbackServer());
            }
        }
        PlayerData data = PlayerDataManager.getInstance().getByUUID(player.getUniqueId());
        data.setName(player.getName());
        data.load();

        if (GameManager.getInstance().getGameState().equals(GameState.LOBBY)) {
            Utils.clearPlayer(player);
            player.teleport(GameManager.getInstance().getLobbyLocation());
            player.getInventory().clear();
            GameManager.getInstance().handleLobbyItems(player);
            PotSG.getInstance().getConfiguration("messages").getStringList("join-message").forEach(message -> player.sendMessage(Color.translate(message.replace("<player>", player.getName()))));
        } else if (GameManager.getInstance().getGameState().equals(GameState.INGAME)) {
            PlayerManager.getInstance().setSpectating(player);
        } else if (GameManager.getInstance().getGameState().equals(GameState.PREMATCH)) {
            Utils.clearPlayer(player);
            player.teleport(GameManager.getInstance().getGameWorldCenterLocation().add(0, 10, 0));
            player.setAllowFlight(true);
            player.setFlying(true);
            data.setState(PlayerState.PREMATCH);
            player.setGameMode(GameMode.SURVIVAL);
            if (GameManager.getInstance().getPrematchCountdown() != null && !GameManager.getInstance().getPrematchCountdown().hasExpired()) {
                player.sendMessage(GameManager.getInstance().getGamePrefix() + Color.translate(PotSG.getInstance().getConfiguration("messages").getString("pre-match-countdown"))
                        .replaceAll("<seconds>", String.valueOf(GameManager.getInstance().getPrematchCountdown().getSecondsLeft()))
                );
            }
        } else if (GameManager.getInstance().getGameState().equals(GameState.ENDING)) {
            PlayerManager.getInstance().setSpectating(player);
        }
    }

    @EventHandler
    public void handlePotionSplash(PotionSplashEvent event) {
        ThrownPotion potion = event.getPotion();
        ProjectileSource player = potion.getShooter();
        if (!(player instanceof Player)) {
            return;
        }

        PlayerData data = PlayerDataManager.getInstance().getByUUID(((Player) player).getUniqueId());
        data.getPotionSplashed().increaseAmount(1);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handleQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        PlayerData data = PlayerDataManager.getInstance().getByUUID(event.getPlayer().getUniqueId());
        data.save();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handleCombatTagEvent(SGCombatTagEvent event) {
        PlayerData playerData = PlayerDataManager.getInstance().getByUUID(event.getPlayer().getUniqueId());
        PlayerData targetData = PlayerDataManager.getInstance().getByUUID(event.getTarget().getUniqueId());
        targetData.setCombatCooldown(new Cooldown(35));
        playerData.setCombatCooldown(new Cooldown(35));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handleSpectatorChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        PlayerData data = PlayerDataManager.getInstance().getByUUID(player.getUniqueId());

        if (!PlayerManager.getInstance().isSpectator(player)) {
            return;
        }
        if (!data.isSpecChat()) {
            return;
        }
        event.setCancelled(true);
        Utils.broadcastMessageToSpectators(PotSG.getInstance().getConfiguration("config").getString("CHAT-FORMAT.SPECTATOR-FORMAT")
                .replaceAll("<player_display_name>", player.getDisplayName())
                .replaceAll("<message>", event.getMessage())
        );
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handleKick(PlayerKickEvent event) {
        event.setLeaveMessage(null);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handleMobSpawn(CreatureSpawnEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handlePlayerDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();
        PlayerData data = PlayerDataManager.getInstance().getByUUID(player.getUniqueId());

        if (!data.getState().equals(PlayerState.INGAME)) {
            event.setCancelled(true);
        }
        if (GameManager.getInstance().getPvpCountdown() != null && !GameManager.getInstance().getPvpCountdown().hasExpired()) {
            if (event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
                return;
            }
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handleBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode().equals(GameMode.CREATIVE) && !PlayerManager.getInstance().isSpectator(player)) {
            return;
        }

        PlayerData data = PlayerDataManager.getInstance().getByUUID(player.getUniqueId());

        if (!data.getState().equals(PlayerState.INGAME)) {
            event.setCancelled(true);
            player.sendMessage(Color.translate(PotSG.getInstance().getConfiguration("messages").getString("cannot-break-block")));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handleBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode().equals(GameMode.CREATIVE) && !PlayerManager.getInstance().isSpectator(player)) {
            return;
        }
        PlayerData data = PlayerDataManager.getInstance().getByUUID(player.getUniqueId());

        if (!data.getState().equals(PlayerState.INGAME)) {
            event.setCancelled(true);
            player.sendMessage(Color.translate(PotSG.getInstance().getConfiguration("messages").getString("cannot-place-block")));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handleFoodFix(FoodLevelChangeEvent event) {
        if (event.getFoodLevel() < ((Player) event.getEntity()).getFoodLevel() && new Random().nextInt(100) > 4) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handlePortalTeleport(PlayerPortalEvent event) {
        event.setCancelled(true);
        event.getPlayer().sendMessage(Color.translate(PotSG.getInstance().getConfiguration("messages").getString("cannot-enter-portal")));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handleItemDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        PlayerData data = PlayerDataManager.getInstance().getByUUID(player.getUniqueId());

        if (!data.getState().equals(PlayerState.INGAME)) {
            event.setCancelled(true);
            player.sendMessage(Color.translate(PotSG.getInstance().getConfiguration("messages").getString("cannot-drop-item")));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handleWeather(WeatherChangeEvent event) {
        event.setCancelled(event.toWeatherState());
    }

    @EventHandler
    public void onItemPickupPotion(PlayerPickupItemEvent event) {
        PlayerData data = PlayerDataManager.getInstance().getByUUID(event.getPlayer().getUniqueId());
        if (!data.getState().equals(PlayerState.INGAME)) {
            event.setCancelled(true);
            return;
        }

        ItemStack stack = event.getItem().getItemStack();
        if (stack.getType() != Material.POTION) {
            return;
        }
        event.setCancelled(true);
        if (event.getPlayer().getInventory().firstEmpty() == -1) {
            return;
        }
        event.getPlayer().getInventory().addItem(new ItemStack[]{stack});
        event.getItem().remove();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void handleChestPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        Player player = event.getPlayer();
        if (!item.equals(GameManager.getInstance().getChestItem())) {
            return;
        }
        if (!player.hasPermission("frozedsg.chests.place")) {
            return;
        }
        if (GameManager.getInstance().getGameState() == GameState.INGAME) {
            return;
        }
        player.sendMessage(Color.translate(GameManager.getInstance().getGamePrefix() + "&bYou have placed game chest. &fPlease don't put items in it if you want it to work properly!"));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handleChestBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        if (!GameManager.getInstance().getGameState().equals(GameState.INGAME)) {
            return;
        }
        if (event.getBlock().getType().equals(Material.CHEST)) {
            Chest chest = (Chest) event.getBlock().getState();
            Inventory inv = chest.getInventory();

            if (!inv.getTitle().equalsIgnoreCase(GameManager.getInstance().getChestItem().getItemMeta().getDisplayName())) {
                return;
            }
            if (!player.hasPermission("frozedsg.chests.place")) {
                event.setCancelled(true);
                return;
            }
            if (!player.getGameMode().equals(GameMode.CREATIVE)) {
                player.sendMessage(Color.translate("&aYou must in a Gamemode Creative to break this chest!"));
                return;
            }
            if (!player.isSneaking()) {
                event.setCancelled(true);
                player.sendMessage(GameManager.getInstance().getGamePrefix() + Color.translate("&bPlease sneak if you want to break this chest"));
                return;
            }
            inv.clear();
            player.sendMessage(GameManager.getInstance().getGamePrefix() + Color.translate("&bYou have broke this chest. Items have been cleared!"));
        }
    }

    @EventHandler
    public void onCraft(PrepareItemCraftEvent e) {

        if (e.getInventory().getResult().getType() == Material.CHEST) {
            e.getInventory().setResult(new ItemBuilder(Material.AIR).toItemStack());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handleChestClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        PlayerData data = PlayerDataManager.getInstance().getByUUID(player.getUniqueId());

        if (!data.getState().equals(PlayerState.INGAME)) {
            return;
        }
        if (!GameManager.getInstance().getGameState().equals(GameState.INGAME)) {
            return;
        }
        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }
        if (!event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            return;
        }
        if (data.getSettingByName("ChestClick") != null && !data.getSettingByName("ChestClick").isEnabled()) {
            return;
        }
        if (event.getClickedBlock().getType().equals(Material.CHEST) || event.getClickedBlock().getType().equals(Material.TRAPPED_CHEST)) {
            Chest chest = (Chest) event.getClickedBlock().getState();
            Inventory inv = chest.getInventory();

            /*if (!inv.getTitle().equalsIgnoreCase(GameManager.getInstance().getChestItem().getItemMeta().getDisplayName())) {
                if (GameManager.getInstance().getFeastCountdown() != null && !GameManager.getInstance().getFeastCountdown().hasExpired()) {
                    return;
                }
            }*/

            ItemStack[] todrop;
            if (Utils.isInventoryEmpty(inv)) {
                if (Math.abs(event.getPlayer().getLocation().getX() - GameManager.getInstance().getGameWorldCenterLocation().getX()) <= 20 && Math.abs(event.getPlayer().getLocation().getZ() - GameManager.getInstance().getGameWorldCenterLocation().getZ()) <= 20) {
                    todrop = ChestsManager.getInstance().getRandomItemsFromChests(true);
                } else {
                    todrop = ChestsManager.getInstance().getRandomItemsFromChests(false);
                }
            } else {
                todrop = inv.getContents();
            }
            if (todrop != null) {
                event.getClickedBlock().setType(Material.AIR);
                World world = player.getWorld();
                Bukkit.getServer().getPluginManager().callEvent(new SGChestBreakEvent(player, inv, chest.getLocation()));
                for (ItemStack items : todrop) {
                    if (items != null && !items.getType().equals(Material.AIR)) {
                        if (data.getSettingByName("Chest-Auto-Pickup") != null && !data.getSettingByName("Chest-Auto-Pickup").isEnabled()) {
                            world.dropItemNaturally(event.getClickedBlock().getLocation().add(0, 1, 0), items);
                        } else {
                            if (player.getInventory().firstEmpty() != -1) {
                                player.getInventory().addItem(items);
                            } else {
                                world.dropItemNaturally(player.getLocation().add(0, 1, 0), items);
                            }
                        }
                    }
                }
                chest.getWorld().playSound(chest.getLocation(), Sound.ZOMBIE_WOODBREAK, 0.2f, 1.0f);
                data.getChestBroke().increaseAmount(1);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void handleCompassInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.PHYSICAL && event.hasItem() && event.getItem().getType() == Material.COMPASS) {
            event.setCancelled(true);
            final Player player = event.getPlayer();
            Player targetp = null;
            double distance = Double.MAX_VALUE;
            for (final Player op : Bukkit.getServer().getOnlinePlayers()) {
                if (op != player) {
                    if (PlayerManager.getInstance().isSpectator(op)) {
                        continue;
                    }
                    if (distance <= player.getLocation().distanceSquared(op.getLocation())) {
                        continue;
                    }
                    targetp = op;
                    distance = player.getLocation().distanceSquared(targetp.getLocation());
                }
            }
            if (targetp != null) {
                player.setCompassTarget(targetp.getLocation());
                player.sendMessage(Color.translate(PotSG.getInstance().getConfiguration("messages").getString("tracking-player"))
                        .replaceAll("<target>", targetp.getName())
                );
            } else {
                player.sendMessage(Color.translate(PotSG.getInstance().getConfiguration("messages").getString("tracking-player-failed")));
            }
        }
    }

    @EventHandler
    public void handleBlockChange(EntityChangeBlockEvent event) {
        Block block = event.getBlock();

        if (block.hasMetadata("fallingBlock")) {
            if (block.getType().equals(Material.CHEST)) {
                block.removeMetadata("fallingBlock", PotSG.getInstance());
                final Chest chest = (Chest) block.getState();
                chest.getInventory().setContents(ChestsManager.getInstance().getRandomItemsFromChests(true));
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handleChestOpen(InventoryOpenEvent event) {
        Inventory inv = event.getView().getTopInventory();

        if (!GameManager.getInstance().getGameState().equals(GameState.INGAME)) {
            return;
        }
        if (event.getInventory().getType() != InventoryType.CHEST) {
            return;
        }
        /*if (!inv.getTitle().equalsIgnoreCase(GameManager.getInstance().getChestItem().getItemMeta().getDisplayName())) {
            if (GameManager.getInstance().getFeastCountdown() != null && !GameManager.getInstance().getFeastCountdown().hasExpired()) {
                return;
            }
        }*/
        ItemStack[] todrop;
        if (Utils.isInventoryEmpty(inv)) {
            if (Math.abs(event.getPlayer().getLocation().getX() - GameManager.getInstance().getGameWorldCenterLocation().getX()) <= 20 && Math.abs(event.getPlayer().getLocation().getZ() - GameManager.getInstance().getGameWorldCenterLocation().getZ()) <= 20) {
                todrop = ChestsManager.getInstance().getRandomItemsFromChests(true);
            } else {
                todrop = ChestsManager.getInstance().getRandomItemsFromChests(false);
            }
        } else {
            todrop = inv.getContents();
        }
        if (todrop != null && todrop.length > 0) {
            inv.clear();
            inv.setContents(todrop);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handleInventoryClose(InventoryCloseEvent event) {
        Inventory inv = event.getView().getTopInventory();
        Player player = (Player) event.getPlayer();
        PlayerData data = PlayerDataManager.getInstance().getByUUID(player.getUniqueId());

        /*if (!inv.getTitle().equalsIgnoreCase(GameManager.getInstance().getChestItem().getItemMeta().getDisplayName())) {
            if (GameManager.getInstance().getFeastCountdown() != null && !GameManager.getInstance().getFeastCountdown().hasExpired()) {
                return;
            }
        }*/

        if (!Utils.isInventoryEmpty(inv)) {
            return;
        }
        if (inv.getSize() < 10) {
            return;
        }
        if (!GameManager.getInstance().getGameState().equals(GameState.INGAME)) {
            return;
        }
        if (data.getLastChestOpenedLocation() != null) {
            if (data.getLastChestOpenedLocation().getBlock().getType() != Material.CHEST) {
                return;
            }

            Location loc = data.getLastChestOpenedLocation();
            loc.getWorld().getBlockAt(loc).setType(Material.AIR);

        }
    }

    @EventHandler
    public void handleEat(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        PlayerData data = PlayerDataManager.getInstance().getByUUID(player.getUniqueId());

        if (event.getItem().getType() == Material.GOLDEN_APPLE) {
            data.getGoldenApplesEaten().increaseAmount(1);
        }

        if (event.getItem().getType() == Material.POTION) {
            data.getPotionDrank().increaseAmount(1);
        }
    }

    @EventHandler
    public void onPlayerShoot(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player) {
            Player shooter = (Player) event.getEntity();
            PlayerData data = PlayerDataManager.getInstance().getByUUID(shooter.getUniqueId());
            data.getBowShots().increaseAmount(1);
        }
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void handleChestOpen(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();

        if (!GameManager.getInstance().getGameState().equals(GameState.INGAME)) {
            return;
        }

        if (action.equals(Action.RIGHT_CLICK_BLOCK)) {
            Block block = event.getClickedBlock();
            if (((block.getType().equals(Material.CHEST)) || (block.getType().equals(Material.TRAPPED_CHEST))) && (!player.isSneaking())) {
                Chest chest = (Chest) block.getState();
                Inventory inv = chest.getInventory();
                /*if (!inv.getTitle().equalsIgnoreCase(GameManager.getInstance().getChestItem().getItemMeta().getDisplayName())) {
                    if (GameManager.getInstance().getFeastCountdown() != null && !GameManager.getInstance().getFeastCountdown().hasExpired()) {
                        return;
                    }
                }*/
                PlayerData data = PlayerDataManager.getInstance().getByUUID(player.getUniqueId());
                data.setLastChestOpenedLocation(block.getLocation());
                Bukkit.getServer().getPluginManager().callEvent(new SGChestOpenEvent(player, inv, chest.getLocation()));
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handleEnderpearl(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        PlayerData data = PlayerDataManager.getInstance().getByUUID(player.getUniqueId());
        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }
        if (!event.hasItem()) {
            return;
        }
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (event.getItem().getType() != Material.ENDER_PEARL) {
            return;
        }
        if (data.getEnderpearlCooldown() != null && !data.getEnderpearlCooldown().hasExpired()) {
            event.setUseItemInHand(Event.Result.DENY);
            player.updateInventory();
            player.sendMessage(GameManager.getInstance().getGamePrefix() + Color.translate(PotSG.getInstance().getConfiguration("messages").getString("cannot-use-enderpearl"))
                    .replaceAll("<enderpearl_cooldown>", String.valueOf(data.getEnderpearlCooldown().getMiliSecondsLeft()))
            );
        } else {
            data.setEnderpearlCooldown(new Cooldown(10));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handleGameDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        PlayerData victimData = PlayerDataManager.getInstance().getByUUID(player.getUniqueId());
        victimData.setRespawnInfo(new RespawnInfo(player.getLocation().add(0, 1, 0), player.getInventory().getContents(), player.getInventory().getArmorContents()));
        if (!GameManager.getInstance().getGameState().equals(GameState.INGAME)) {
            return;
        }

        new BukkitRunnable() {
            public void run() {
                try {
                    /*Object nmsPlayer = player.getClass().getMethod("getHandle").invoke(player);
                    Object con = nmsPlayer.getClass().getDeclaredField("playerConnection").get(nmsPlayer);

                    Class<?> EntityPlayer = Class.forName(nmsPlayer.getClass().getPackage().getName() + ".EntityPlayer");

                    Field minecraftServer = con.getClass().getDeclaredField("minecraftServer");
                    minecraftServer.setAccessible(true);
                    Object mcserver = minecraftServer.get(con);

                    Object playerlist = mcserver.getClass().getDeclaredMethod("getPlayerList").invoke(mcserver);
                    Method moveToWorld = playerlist.getClass().getMethod("moveToWorld", EntityPlayer, int.class, boolean.class);
                    moveToWorld.invoke(playerlist, nmsPlayer, 0, false);*/

                    player.spigot().respawn();

                    PlayerManager.getInstance().setSpectating(player);
                    player.sendMessage(Color.translate(GameManager.getInstance().getGamePrefix() + PotSG.getInstance().getConfiguration("messages").getString("become-spectator")));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }.runTaskLater(PotSG.getInstance(), 2L);

        victimData.getDeaths().increaseAmount(1);
        if (event.getEntity().getKiller() != null) {
            Player killer = event.getEntity().getKiller();
            PlayerData killerData = PlayerDataManager.getInstance().getByUUID(killer.getUniqueId());

            int points = GameManager.getInstance().getPointsPerKill();
            killerData.getPoints().increaseAmount(points);

            if (killerData.getGameKills().getAmount() > killerData.getKillStreak().getAmount()) {
                killerData.getKillStreak().setAmount(killerData.getGameKills().getAmount());
            }

            killer.sendMessage(Color.translate(PotSG.getInstance().getConfiguration("messages").getString("add-points-because-kill"))
                    .replaceAll("<points>", String.valueOf(points))
            );
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handleGameWin(SGGameWinEvent event) {
        GameManager.getInstance().setGameState(GameState.ENDING);

        if (event.getPlayer() == null) {
            if (GameManager.getInstance().getRebootCountdown() != null && !GameManager.getInstance().getRebootCountdown().hasExpired()) {
                GameManager.getInstance().getRebootCountdown().cancelCountdown();
            }
            GameManager.getInstance().setRebootCountdown(new RebootCountdown());
            return;
        }
        if (GameManager.getInstance().getRebootCountdown() != null && !GameManager.getInstance().getRebootCountdown().hasExpired()) {
            GameManager.getInstance().getRebootCountdown().cancelCountdown();
        }
        Player player = event.getPlayer();
        PlayerData data = event.getPlayerData();
        data.getWins().increaseAmount(1);
        player.sendMessage(Color.translate(PotSG.getInstance().getConfiguration("messages").getString("add-points-because-win"))
                .replaceAll("<points>", String.valueOf(GameManager.getInstance().getPointsPerWin()))
        );
        data.getPoints().increaseAmount(GameManager.getInstance().getPointsPerWin());

        for (String s : PotSG.getInstance().getConfiguration("messages").getStringList("win-message")) {
            Utils.broadcastMessage(s
                    .replaceAll("<winner>", player.getName())
                    .replaceAll("<winner_wins>", String.valueOf(data.getWins().getAmount()))
                    .replaceAll("<winner_kills>", String.valueOf(data.getGameKills().getAmount()))
            );
        }

        new BukkitRunnable() {
            public void run() {
                GameManager.getInstance().setToCancelFirework(true);
            }
        }.runTaskLater(PotSG.getInstance(), 20L * 10);
        new BukkitRunnable() {
            public void run() {
                if (GameManager.getInstance().isToCancelFirework()) {
                    this.cancel();
                }
                Player target = Bukkit.getPlayer(player.getName());
                if (target != null) {
                    target.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK);
                }
            }
        }.runTaskTimer(PotSG.getInstance(), 10L, 10L);

        GameManager.getInstance().setWinner(player.getName());
        GameManager.getInstance().setWinnerKills(data.getGameKills().getAmount());
        GameManager.getInstance().setWinnerTotalKills(data.getKills().getAmount());

        GameManager.getInstance().setRebootCountdown(new RebootCountdown());

        PlayerDataManager.getInstance().getPlayerDatas().values().forEach(PlayerData::save);
        int size = PlayerDataManager.getInstance().getPlayerDatas().size();
        Bukkit.getConsoleSender().sendMessage(Color.translate("&7&m-----------------------------------"));
        Bukkit.getConsoleSender().sendMessage(Color.translate("&aSuccessfully saved &f" + size + " data " + (size > 1 ? "files" : "file") + "&a."));
        Bukkit.getConsoleSender().sendMessage(Color.translate("&7&m-----------------------------------"));
    }

    @EventHandler
    public void onSGWorldLoad(SGWorldLoad event) {
        if (PotSG.getInstance().getConfiguration("config").getBoolean("ROADS.MAKE-ROADS")) {
            Bukkit.getConsoleSender().sendMessage(Color.translate("&b[FrozedSG] &aRoad process has begun."));
            new SplitedRoadProcessor(PotSG.getInstance(), event.getCenterLocation(), 15000, 2).run();
        } else {
            Bukkit.getConsoleSender().sendMessage(Color.translate("&b[FrozedSG] &aPlayers are now able to join the server."));
            PotSG.getInstance().setPluginLoading(false);
        }
    }

    @EventHandler
    public void handleBrew(final PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            final BlockState state = event.getClickedBlock().getState();
            if (state instanceof BrewingStand) {
                GameManager.getInstance().getActiveBrewingStands().put(state.getLocation(), (BrewingStand) state);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handlePlayerFood(FoodLevelChangeEvent event) {
        if (!GameManager.getInstance().getGameState().equals(GameState.INGAME)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerTeleportEvent(PlayerTeleportEvent event) {
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            return;
        }
        Player player = event.getPlayer();
        Border border = BorderManager.getInstance().getBorder();
        int size = border.getSize();
        double x = event.getTo().getX();
        double z = event.getTo().getZ();
        if (x >= size) {
            player.teleport(event.getFrom());
            player.sendMessage(GameManager.getInstance().getBorderPrefix() + Color.translate(PotSG.getInstance().getConfiguration("messages").getString("stay-in-border")));
            event.setCancelled(true);
        }
        if (z >= size) {
            player.teleport(event.getFrom());
            player.sendMessage(GameManager.getInstance().getBorderPrefix() + Color.translate(PotSG.getInstance().getConfiguration("messages").getString("stay-in-border")));
            event.setCancelled(true);
        }
        if (x <= -size) {
            player.teleport(event.getFrom());
            player.sendMessage(GameManager.getInstance().getBorderPrefix() + Color.translate(PotSG.getInstance().getConfiguration("messages").getString("stay-in-border")));
            event.setCancelled(true);
        }
        if (z <= -size) {
            player.teleport(event.getFrom());
            player.sendMessage(GameManager.getInstance().getBorderPrefix() + Color.translate(PotSG.getInstance().getConfiguration("messages").getString("stay-in-border")));
            event.setCancelled(true);
        }
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void handleWorldBorderPass(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        int X = event.getTo().getBlockX();
        int Z = event.getTo().getBlockZ();

        Border border = BorderManager.getInstance().getBorder();
        if (border == null) {
            return;
        }
        int size = border.getSize();
        World world = player.getWorld();

        if (!world.getName().equalsIgnoreCase(WorldsManager.getInstance().getGameWorld().getName())) {
            return;
        }
        if (X >= size) {
            player.teleport(event.getFrom());
            player.sendMessage(GameManager.getInstance().getBorderPrefix() + Color.translate(PotSG.getInstance().getConfiguration("messages").getString("stay-in-border")));
            BorderManager.getInstance().checkBorder(player);
        }
        if (Z >= size) {
            player.teleport(event.getFrom());
            player.sendMessage(GameManager.getInstance().getBorderPrefix() + Color.translate(PotSG.getInstance().getConfiguration("messages").getString("stay-in-border")));
            BorderManager.getInstance().checkBorder(player);
        }
        if (X <= -size) {
            player.teleport(event.getFrom());
            player.sendMessage(GameManager.getInstance().getBorderPrefix() + Color.translate(PotSG.getInstance().getConfiguration("messages").getString("stay-in-border")));
            BorderManager.getInstance().checkBorder(player);
        }
        if (Z <= -size) {
            player.teleport(event.getFrom());
            player.sendMessage(GameManager.getInstance().getBorderPrefix() + Color.translate(PotSG.getInstance().getConfiguration("messages").getString("stay-in-border")));
            BorderManager.getInstance().checkBorder(player);
        }
    }
}
