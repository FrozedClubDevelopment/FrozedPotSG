package me.elb1to.frozedsg;

import lombok.Getter;
import lombok.Setter;
import me.allen.ziggurat.Ziggurat;
import me.elb1to.frozedsg.border.BorderManager;
import me.elb1to.frozedsg.commands.*;
import me.elb1to.frozedsg.commands.staff.*;
import me.elb1to.frozedsg.layout.BoardLayout;
import me.elb1to.frozedsg.layout.TablistLayout;
import me.elb1to.frozedsg.listeners.*;
import me.elb1to.frozedsg.managers.*;
import me.elb1to.frozedsg.utils.Utils;
import me.elb1to.frozedsg.utils.chat.Color;
import me.elb1to.frozedsg.utils.leaderboards.LeaderboardManager;
import me.elb1to.frozedsg.other.PlayerListener;
import me.elb1to.frozedsg.utils.board.BoardManager;
import me.elb1to.frozedsg.utils.command.CommandFramework;
import me.elb1to.frozedsg.utils.configurations.ConfigFile;
import me.elb1to.frozedsg.utils.runnables.DataRunnable;
import me.elb1to.frozedsg.utils.tasks.BrewingTask;
import me.elb1to.frozedsg.utils.tasks.DataSaveTask;
import me.elb1to.frozedsg.utils.tasks.LobbyTask;
import me.elb1to.frozedsg.utils.tasks.PlayerTask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class PotSG extends JavaPlugin {

    @Getter
    public static PotSG instance;
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

        if (!this.getDescription().getAuthors().contains("Elb1to") || !this.getDescription().getAuthors().contains("Scalebound") || !this.getDescription().getName().equals("FrozedSG")) {
            System.exit(0);
            Bukkit.shutdown();
        }

        if (!isBorderShrinksStreamValid()) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "[FrozedSG]" + ChatColor.RED + " The plugin could not be enabled. Please check your configuration for " + ChatColor.YELLOW + "Border Shrinks Stream.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        //From listeners package
        Bukkit.getPluginManager().registerEvents(new ButtonListener(), this);
        Bukkit.getPluginManager().registerEvents(new ChunkListener(), this);
        Bukkit.getPluginManager().registerEvents(new GlassListener(), this);
        Bukkit.getPluginManager().registerEvents(new InventoryHandler(), this);
        Bukkit.getPluginManager().registerEvents(new DeathMessagesListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);

        //From managers package
        new ChestsManager();
        new GameManager();
        new InventoryManager();
        new MongoManager();
        new PlayerDataManager();
        new PlayerManager();
        new WorldsManager();

        //From commands package
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

        checkLicense();

        Bukkit.getConsoleSender().sendMessage(Color.translate("&7&m--------------------------------------------------------------"));
        Bukkit.getConsoleSender().sendMessage(Color.translate("&7This server is using &bFrozedSG"));
        Bukkit.getConsoleSender().sendMessage(Color.translate("&7Authors&8: &b" + getDescription().getAuthors()));
        Bukkit.getConsoleSender().sendMessage(Color.translate("&7Version&8: &b" + getDescription().getVersion()));
        Bukkit.getConsoleSender().sendMessage(Color.translate("&7&m--------------------------------------------------------------"));
        Bukkit.getConsoleSender().sendMessage(" ");
        Bukkit.getConsoleSender().sendMessage(Color.translate("&eChecking your spigot version..."));
        Bukkit.getConsoleSender().sendMessage(Color.translate("&aSuccess! &eYour Server NMS version: " + getNmsVersion()));

        new LeaderboardManager();
        new DataRunnable();
    }

    private String getNmsVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
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

    public void checkLicense() {

    }
}
