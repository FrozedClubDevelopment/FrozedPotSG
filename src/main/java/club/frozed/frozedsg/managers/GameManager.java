package club.frozed.frozedsg.managers;

import club.frozed.frozedsg.utils.ItemBuilder;
import club.frozed.frozedsg.utils.countdowns.*;
import lombok.Data;
import lombok.Getter;
import club.frozed.frozedsg.PotSG;
import club.frozed.frozedsg.enums.GameState;
import club.frozed.frozedsg.player.PlayerData;
import club.frozed.frozedsg.utils.Utils;
import club.frozed.frozedsg.utils.chat.Color;
import club.frozed.frozedsg.utils.countdowns.*;
import club.frozed.frozedsg.utils.runnables.GameRunnable;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BrewingStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class GameManager {
    @Getter
    public static GameManager instance;
    private GameState gameState = GameState.LOBBY;
    private String gamePrefix = Color.translate(PotSG.getInstance().getConfiguration("config").getString("PREFIXES.GAME"));
    private String borderPrefix = Color.translate(PotSG.getInstance().getConfiguration("config").getString("PREFIXES.BORDER"));
    private String tsInfo = PotSG.getInstance().getConfiguration("config").getString("INFORMATIONS.TS");
    private String webInfo = PotSG.getInstance().getConfiguration("config").getString("INFORMATIONS.WEB");
    private String ipInfo = PotSG.getInstance().getConfiguration("config").getString("INFORMATIONS.IP");
    private String storeInfo = PotSG.getInstance().getConfiguration("config").getString("INFORMATIONS.STORE");
    private String serverName = PotSG.getInstance().getConfiguration("config").getString("INFORMATIONS.SERVER");
    private int maxPlayers = PotSG.getInstance().getConfiguration("config").getInt("MAXIMUM-PLAYERS-PER-GAME");
    private int minPlayers = PotSG.getInstance().getConfiguration("config").getInt("MINIMUM-PLAYERS-TO-START-GAME");
    private int startCountdownValue = PotSG.getInstance().getConfiguration("config").getInt("COUNTDOWNS.IN-SECONDS.START");
    private int prematchCountdownValue = PotSG.getInstance().getConfiguration("config").getInt("COUNTDOWNS.IN-SECONDS.PRE-MATCH");
    private int pvpCountdownValue = PotSG.getInstance().getConfiguration("config").getInt("COUNTDOWNS.IN-SECONDS.PVP-PROT");
    private int feastsCountdownValue = PotSG.getInstance().getConfiguration("config").getInt("COUNTDOWNS.IN-MINUTES.FEASTS-SPAWN");
    private int deathMatchCountdownValue = PotSG.getInstance().getConfiguration("config").getInt("COUNTDOWNS.IN-MINUTES.DEATH-MATCH");
    private int rebootCountdownValue = PotSG.getInstance().getConfiguration("config").getInt("COUNTDOWNS.IN-SECONDS.REBOOT");
    private int pointsPerKill = PotSG.getInstance().getConfiguration("config").getInt("POINTS.PER-KILL");
    private int pointsPerWin = PotSG.getInstance().getConfiguration("config").getInt("POINTS.PER-WIN");
    private String rebootCommand = PotSG.getInstance().getConfiguration("config").getString("REBOOT_COMMAND");
    private StartCountdown startCountdown = null;
    private PrematchCountdown prematchCountdown = null;
    private PvPCountdown pvpCountdown = null;
    private FeastCountdown feastCountdown = null;
    private GameRunnable gameRunnable = null;
    private DeathMatchCountdown deathMatchCountdown = null;
    private RebootCountdown rebootCountdown;
    private boolean forceStarted = false;
    private boolean toUseLobby = PotSG.getInstance().getConfiguration("config").getBoolean("BOOLEANS.LOBBY-ENABLED");
    private boolean serverPremium = PotSG.getInstance().getConfiguration("config").getBoolean("SERVER-PREMIUM");
    private String lobbyFallbackServer = PotSG.getInstance().getConfiguration("config").getString("LOBBY-FALLBACK-SERVER");
    private String winner = "";
    private int winnerKills = 0;
    private int winnerTotalKills = 0;
    private boolean toCancelFirework = false;
    private static MetadataValue META_KEY = new FixedMetadataValue(PotSG.getInstance(), true);
    private boolean deathMatchArenaSpawned = false;
    private Map<Location, BrewingStand> activeBrewingStands = new HashMap<>();

    public GameManager() {
        instance = this;
    }

    public void handleLobbyItems(Player player) {
        ItemBuilder statistics = new ItemBuilder(Material.EMERALD);
        statistics.setNameWithArrows(PotSG.getInstance().getConfiguration("items").getString("join-inventory.leaderboard.name"));
        for (String lore : PotSG.getInstance().getConfiguration("items").getStringList("join-inventory.leaderboard.lore")) {
            statistics.addLoreLine(lore);
        }

        ItemBuilder players = new ItemBuilder(Material.WATCH);
        players.setNameWithArrows(PotSG.getInstance().getConfiguration("items").getString("join-inventory.player-stats.name"));
        for (String lore : PotSG.getInstance().getConfiguration("items").getStringList("join-inventory.player-stats.lore")) {
            players.addLoreLine(lore);
        }

        ItemBuilder settings = new ItemBuilder(Material.CHEST);
        settings.setNameWithArrows(PotSG.getInstance().getConfiguration("items").getString("join-inventory.settings.name"));
        for (String lore : PotSG.getInstance().getConfiguration("items").getStringList("join-inventory.settings.lore")) {
            settings.addLoreLine(lore);
        }

        ItemBuilder stats = new ItemBuilder(Material.SKULL_ITEM).setDurability(3);
        stats.setNameWithArrows(PotSG.getInstance().getConfiguration("items").getString("join-inventory.your-stats.name"));
        for (String lore : PotSG.getInstance().getConfiguration("items").getStringList("join-inventory.your-stats.lore")) {
            stats.addLoreLine(lore);
        }

        player.getInventory().setItem(0, statistics.toItemStack());
        player.getInventory().setItem(3, players.toItemStack());
        player.getInventory().setItem(5, stats.toItemStack());
        player.getInventory().setItem(8, settings.toItemStack());
    }

    public Location getLobbyLocation() {
        Location lobbyLoc = WorldsManager.getInstance().getLobbyWorld().getSpawnLocation();
        lobbyLoc.setYaw(PotSG.getInstance().getConfiguration("config").getInt("LOCATIONS.LOBBY.YAW"));
        lobbyLoc.setPitch(PotSG.getInstance().getConfiguration("config").getInt("LOCATIONS.LOBBY.PITCH"));
        return lobbyLoc;
    }

    public Location getGameWorldCenterLocation() {
        if (!isGameCenterLocationValid()) {
            int x = 0;
            int z = 0;
            double y = WorldsManager.getInstance().getGameWorld().getHighestBlockYAt(x, z);
            return new Location(WorldsManager.getInstance().getGameWorld(), x, y, z);
        }
        String input = PotSG.getInstance().getConfiguration("config").getString("GAME-CENTER-LOCATION");
        String[] coords = input.split(";");
        int x = Integer.valueOf(coords[0]);
        int y = Integer.valueOf(coords[1]);
        int z = Integer.valueOf(coords[2]);
        return new Location(WorldsManager.getInstance().getGameWorld(), x, y, z);
    }

    private boolean isGameCenterLocationValid() {
        String input = PotSG.getInstance().getConfiguration("config").getString("GAME-CENTER-LOCATION");
        String[] coords = input.split(";");

        for (String coord : coords) {
            if (!Utils.isInteger(coord)) {
                return false;
            }
        }
        if (coords.length < 3) {
            return false;
        }
        return true;
    }

    public void startGame() {
        setStartCountdown(new StartCountdown());
    }

    public int getRequiredPlayersToJoin() {
        return getMinPlayers() - PlayerManager.getInstance().getLobbyPlayers().size();
    }

    public ItemStack getChestItem() {
        ItemBuilder item = new ItemBuilder(Material.CHEST);
        item.setName("&aChest");
        item.addLoreLine("&7Place me on some random location and thats all.");
        item.addLoreLine("&7Please don't put items in me.");
        item.addLoreLine("&7I will put random chest items from &f'/game chests'");
        item.addLoreLine("&7You can edit chests there.");
        item.addLoreLine(" ");
        item.addLoreLine("&7&oNote: &4&lI MUST BE EMPTY IF YOU WANT ME TO WORK PROPERLY!");

        return item.toItemStack();
    }

    public List<PlayerData> getTop5GameKills() {
        return PlayerDataManager.getInstance().getPlayerDatas().values().stream().filter(playerData -> playerData.getGameKills().getAmount() > 0).limit(5).sorted().collect(Collectors.toList());
    }

    public void spawnFeast() {
        World world = WorldsManager.getInstance().getGameWorld();

        for (int x = -8; x < 8; ++x) {
            for (int z = -8; z < 8; ++z) {
                if (RandomUtils.nextInt(20) == 0) {
                    final Block block = world.getBlockAt(x + 8, world.getHighestBlockAt(x, z).getY(), z + 8);
                    if (block.getRelative(BlockFace.NORTH).getType() != Material.CHEST) {
                        if (block.getRelative(BlockFace.SOUTH).getType() != Material.CHEST) {
                            if (block.getRelative(BlockFace.EAST).getType() != Material.CHEST) {
                                if (block.getRelative(BlockFace.WEST).getType() != Material.CHEST) {
                                    FallingBlock fallingBlock = world.spawnFallingBlock(block.getLocation().add(0, 30, 0), Material.CHEST, (byte) 54);
                                    fallingBlock.setMetadata("fallingBlock", META_KEY);
                                }
                            }
                        }
                    }
                }
            }
        }
        world.getBlockAt(8, world.getHighestBlockAt(0, 0).getY(), 8).setType(Material.ENCHANTMENT_TABLE);
    }

    public void clearFlat(Location loc, int r) {
        int cx = loc.getBlockX();
        int cy = loc.getBlockY();
        int cz = loc.getBlockZ();
        World w = loc.getWorld();
        for (int x = cx - r; x <= cx + r; x++) {
            for (int y = cy; y <= cy + (r / 2); y++) {
                for (int z = cz - r; z <= cz + r; z++) {
                    w.getBlockAt(x, y, z).setType(Material.AIR);
                }
            }
        }
    }

    public void spawnDeathMatchArena() {
        List<Material> floorMaterials = new ArrayList<>();
        List<Material> WallbeetweenMaterials = new ArrayList<>();
        List<Material> WallupMaterials = new ArrayList<>();
        List<Material> WalldownMaterials = new ArrayList<>();

        try {
            PotSG.getInstance().getConfiguration("config").getStringList("DEATH-MATCH.FLOOR-MATERIALS")
                    .forEach(mat -> floorMaterials.add(Material.valueOf(mat)));
            PotSG.getInstance().getConfiguration("config").getStringList("DEATH-MATCH.WALL-MATERIALS.BEETWEEN")
                    .forEach(mat -> WallbeetweenMaterials.add(Material.valueOf(mat)));
            PotSG.getInstance().getConfiguration("config").getStringList("DEATH-MATCH.WALL-MATERIALS.TOP")
                    .forEach(mat -> WallupMaterials.add(Material.valueOf(mat)));
            PotSG.getInstance().getConfiguration("config").getStringList("DEATH-MATCH.WALL-MATERIALS.BOTTOM")
                    .forEach(mat -> WalldownMaterials.add(Material.valueOf(mat)));
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + e.getCause().getMessage());
            Bukkit.getConsoleSender().sendMessage(Color.translate("&cAn error ocured while spawning deathmatch arena. Please check your materials!"));
        }

        Location oldloc = WorldsManager.getInstance().getGameWorld().getBlockAt(0, WorldsManager.getInstance().getGameWorld().getHighestBlockYAt(0, 0) - (51 / (51 / 2)), 0).getLocation();
        clearFlat(oldloc, 16);

        //MAKING FLOOR
        int r = 15;
        int yTop = WorldsManager.getInstance().getGameWorld().getHighestBlockYAt(0, 0);
        Location loc = WorldsManager.getInstance().getGameWorld().getBlockAt(0, yTop, 0).getLocation();
        World w = loc.getWorld();

        int cx = loc.getBlockX();
        int cy = loc.getBlockY();
        int cz = loc.getBlockZ();
        for (int x = cx - r; x <= cx + r; x++) {
            for (int y = cy; y <= cy; y++) {
                for (int z = cz - r; z <= cz + r; z++) {
                    w.getBlockAt(x, y - 1, z).setType(Material.BEDROCK);
                    w.getBlockAt(x, y, z).setType(getRandomMaterial(floorMaterials));
                }
            }
        }

        //MAKING WALLS

        int size = 15;
        Location centerLocation = new Location(w, 0, w.getHighestBlockYAt(0, 0), 0);

        //MAKING BUTTON LAYER

        for (int i = 1; i < 1 + 1; i++) {
            for (int x = centerLocation.getBlockX() - size; x <= centerLocation.getBlockX() + size; ++x) {
                for (int y = 58; y <= 58; ++y) {
                    for (int z = centerLocation.getBlockZ() - size; z <= centerLocation.getBlockZ() + size; ++z) {
                        if (x == centerLocation.getBlockX() - size || x == centerLocation.getBlockX() + size || z == centerLocation.getBlockZ() - size
                                || z == loc.getBlockZ() + size) {
                            Location loc2 = new Location(w, x, y, z);
                            loc2.setY(w.getHighestBlockYAt(loc2));
                            loc2.getBlock().setType(getRandomMaterial(WalldownMaterials));
                        }
                    }
                }
            }
        }

        //MAKING BEETWEEN

        for (int i = 4; i < 4 + 4; i++) {
            for (int x = centerLocation.getBlockX() - size; x <= centerLocation.getBlockX() + size; ++x) {
                for (int y = 58; y <= 58; ++y) {
                    for (int z = centerLocation.getBlockZ() - size; z <= centerLocation.getBlockZ() + size; ++z) {
                        if (x == centerLocation.getBlockX() - size || x == centerLocation.getBlockX() + size || z == centerLocation.getBlockZ() - size
                                || z == loc.getBlockZ() + size) {
                            Location loc2 = new Location(w, x, y, z);
                            loc2.setY(w.getHighestBlockYAt(loc2));
                            loc2.getBlock().setType(getRandomMaterial(WallbeetweenMaterials));
                        }
                    }
                }
            }
        }

        //MAKING BEETWEEN

        for (int i = 1; i < 1 + 1; i++) {
            for (int x = centerLocation.getBlockX() - size; x <= centerLocation.getBlockX() + size; ++x) {
                for (int y = 58; y <= 58; ++y) {
                    for (int z = centerLocation.getBlockZ() - size; z <= centerLocation.getBlockZ() + size; ++z) {
                        if (x == centerLocation.getBlockX() - size || x == centerLocation.getBlockX() + size || z == centerLocation.getBlockZ() - size
                                || z == loc.getBlockZ() + size) {
                            Location loc2 = new Location(w, x, y, z);
                            loc2.setY(w.getHighestBlockYAt(loc2));
                            loc2.getBlock().setType(getRandomMaterial(WallupMaterials));
                        }
                    }
                }
            }
        }

        //CLEARING ENTITES
        for (Entity entites : w.getEntities()) {
            if (!(entites instanceof Player)) {
                entites.remove();
            }
        }
    }

    public Material getRandomMaterial(List<Material> list) {
        final int r = RandomUtils.nextInt(list.size());
        return list.get(r);
    }
}
