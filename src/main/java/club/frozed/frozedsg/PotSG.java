package club.frozed.frozedsg;

import club.frozed.frozedsg.border.BorderManager;
import club.frozed.frozedsg.commands.*;
import club.frozed.frozedsg.commands.staff.*;
import club.frozed.frozedsg.layout.BoardLayout;
import club.frozed.frozedsg.layout.TablistLayout;
import club.frozed.frozedsg.listeners.*;
import club.frozed.frozedsg.managers.*;
import club.frozed.frozedsg.other.PlayerListener;
import club.frozed.frozedsg.utils.Utils;
import club.frozed.frozedsg.utils.board.BoardManager;
import club.frozed.frozedsg.utils.chat.Color;
import club.frozed.frozedsg.utils.command.CommandFramework;
import club.frozed.frozedsg.utils.configurations.ConfigFile;
import club.frozed.frozedsg.utils.leaderboards.LeaderboardManager;
import club.frozed.frozedsg.utils.runnables.DataRunnable;
import club.frozed.frozedsg.utils.tasks.BrewingTask;
import club.frozed.frozedsg.utils.tasks.DataSaveTask;
import club.frozed.frozedsg.utils.tasks.LobbyTask;
import club.frozed.frozedsg.utils.tasks.PlayerTask;
import lombok.Getter;
import lombok.Setter;
import me.allen.ziggurat.Ziggurat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class PotSG extends JavaPlugin {

    @Getter public static PotSG instance;
    private CommandFramework framework;
    private List<ConfigFile> files = new ArrayList<>();
    private BoardManager boardManager;
    private boolean pluginLoading;

    @Override
    public void onEnable() {
        instance = this;
        pluginLoading = true;

        registerConfigurations();

        framework = new CommandFramework(this);
        setBoardManager(new BoardManager(this, new BoardLayout()));
        new Ziggurat(this, new TablistLayout());

        if (!this.getDescription().getAuthors().contains("Elb1to") || !this.getDescription().getName().equals("FrozedSG")
                || !this.getDescription().getAuthors().contains("FrozedClubDevelopment") || !this.getDescription().getDescription().equals("Minemen/Lunar PotSG Replica")) {
            System.exit(0);
            Bukkit.shutdown();
        }

        if (!isBorderShrinksStreamValid()) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "[FrozedSG]" + ChatColor.RED + " The plugin could not be enabled. Please check your configuration for " + ChatColor.AQUA + "Border Shrinks Stream.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        Bukkit.getPluginManager().registerEvents(new ButtonListener(), this);
        Bukkit.getPluginManager().registerEvents(new ChunkListener(), this);
        Bukkit.getPluginManager().registerEvents(new GlassListener(), this);
        Bukkit.getPluginManager().registerEvents(new InventoryHandler(), this);
        Bukkit.getPluginManager().registerEvents(new DeathMessagesListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        Bukkit.getPluginManager().registerEvents(new BossBarListener(), this);

        new ChestsManager();
        new GameManager();
        new InventoryManager();
        new MongoManager();
        new PlayerDataManager();
        new PlayerManager();
        new WorldsManager();

        new DataCommand();
        new GameCommand();
        new ReloadConfigCommand();
        new RespawnCommand();
        new SpectatorCommand();
        new AnnounceCommand();
        new FrozedSGCommand();
        new SettingsCommand();
        new SpecChatCommand();
        new StatsCommand();

        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "Broadcast");
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        new BorderManager();

        getServer().getScheduler().scheduleAsyncRepeatingTask(this, new LobbyTask(), 20L, 20L);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new PlayerTask(), 2L, 2L);
        getServer().getScheduler().scheduleAsyncRepeatingTask(this, new DataSaveTask(), 200L, 200L);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new BrewingTask(), 1L, 1L);

        Bukkit.getConsoleSender().sendMessage(Color.translate("&7&m--------------------------------------------------------------"));
        Bukkit.getConsoleSender().sendMessage(Color.translate("&7This server is using &bFrozedSG"));
        Bukkit.getConsoleSender().sendMessage(Color.translate("&7Authors&8: &b" + getDescription().getAuthors()));
        Bukkit.getConsoleSender().sendMessage(Color.translate("&7Version&8: &b" + getDescription().getVersion()));
        Bukkit.getConsoleSender().sendMessage(Color.translate("&7&m--------------------------------------------------------------"));

        new LeaderboardManager();
        new DataRunnable();
    }

    public ConfigFile getConfiguration(String name) {
        return files.stream().filter(config -> config.getName().equals(name)).findFirst().orElse(null);
    }

    public boolean isBorderShrinksStreamValid() {
        String shrinkStream = PotSG.getInstance().getConfiguration("config").getString("BORDER.SHRINK-STREAM");
        String[] shrinksStream = shrinkStream.split(";");

        for (String shrink : shrinksStream) {
            if (!Utils.isInteger(shrink)) {
                return false;
            }
        }
        return true;
    }

    public void registerConfigurations() {
        files.addAll(Arrays.asList(
                new ConfigFile("config"),
                new ConfigFile("messages"),
                new ConfigFile("items"),
                new ConfigFile("inventory"),
                new ConfigFile("chests"),
                new ConfigFile("scoreboard"),
                new ConfigFile("tablist")
        ));
    }

    public void setBoardManager(BoardManager boardManager) {
        this.boardManager = boardManager;
        long interval = this.boardManager.getAdapter().getInterval();
        this.getServer().getScheduler().runTaskTimerAsynchronously(this, this.boardManager, interval, interval);
        this.getServer().getPluginManager().registerEvents(this.boardManager, this);
    }
}
