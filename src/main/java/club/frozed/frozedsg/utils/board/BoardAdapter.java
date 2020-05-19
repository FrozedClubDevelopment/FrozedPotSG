package club.frozed.frozedsg.utils.board;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.util.List;

public interface BoardAdapter {

	String getTitle(Player player);

	List<String> getScoreboard(Player player, Board board);

	long getInterval();

	void onScoreboardCreate(Player player, Scoreboard board);

	void preLoop();

}