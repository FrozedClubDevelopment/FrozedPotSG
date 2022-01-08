package club.frozed.frozedsg.utils.board;

import club.frozed.frozedsg.PotSG;
import lombok.Getter;
import club.frozed.frozedsg.utils.Symbols;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
public class Board {

	private final BoardAdapter adapter;
	private final Player player;
	private List<BoardEntry> entries = new ArrayList<>();
	private Set<BoardTimer> timers = new HashSet<>();
	private Set<String> keys = new HashSet<>();
	private Scoreboard scoreboard;
	private Objective objective;
	private Objective healthName;

	public Board(JavaPlugin plugin, Player player, BoardAdapter adapter) {
		this.adapter = adapter;
		this.player = player;

		this.init(plugin);
	}

	private void init(JavaPlugin plugin) {
		if (!this.player.getScoreboard().equals(plugin.getServer().getScoreboardManager().getMainScoreboard())) {
			this.scoreboard = this.player.getScoreboard();
		} else {
			this.scoreboard = plugin.getServer().getScoreboardManager().getNewScoreboard();
		}

		this.objective = this.scoreboard.registerNewObjective("Default", "dummy");

		this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		this.objective.setDisplayName(this.adapter.getTitle(player));

		if (PotSG.getInstance().getConfiguration("config").getBoolean("BOOLEANS.HEALTH-NAMETAG")) {
			(healthName = getOrCreateObjective(scoreboard, "healthName", "health")).setDisplaySlot(DisplaySlot.BELOW_NAME);
			healthName.setDisplayName(ChatColor.RED + Symbols.HEALTH);
		}
	}

	public String getNewKey(BoardEntry entry) {
		for (ChatColor color : ChatColor.values()) {
			String colorText = color + "" + ChatColor.WHITE;

			if (entry.getText().length() > 16) {
				String sub = entry.getText().substring(0, 16);
				colorText = colorText + ChatColor.getLastColors(sub);
			}

			if (!keys.contains(colorText)) {
				keys.add(colorText);
				return colorText;
			}
		}

		throw new IndexOutOfBoundsException("No more keys available!");
	}

	public Objective getOrCreateObjective(Scoreboard scoreboard, String objective, String type) {
		Objective value = scoreboard.getObjective(objective);
		if (value == null) {
			value = scoreboard.registerNewObjective(objective, type);
		}
		value.setDisplayName(objective);
		return value;
	}

	public List<String> getBoardEntriesFormatted() {
		List<String> toReturn = new ArrayList<>();

		for (BoardEntry entry : new ArrayList<>(entries)) {
			toReturn.add(entry.getText());
		}

		return toReturn;
	}

	public BoardEntry getByPosition(int position) {
		for (int i = 0; i < this.entries.size(); i++) {
			if (i == position) {
				return this.entries.get(i);
			}
		}

		return null;
	}

	public BoardTimer getCooldown(String id) {
		for (BoardTimer cooldown : getTimers()) {
			if (cooldown.getId().equals(id)) {
				return cooldown;
			}
		}

		return null;
	}

	public Set<BoardTimer> getTimers() {
		this.timers.removeIf(cooldown -> System.currentTimeMillis() >= cooldown.getEnd());
		return this.timers;
	}
}