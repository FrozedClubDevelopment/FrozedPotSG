package club.frozed.frozedsg.utils.board;

import club.frozed.frozedsg.PotSG;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;

@Getter
@RequiredArgsConstructor
public class BoardManager implements Runnable, Listener {

    private final JavaPlugin plugin;
    private final Map<UUID, Board> playerBoards = new HashMap<>();
    private final BoardAdapter adapter;

    @Override
    public void run() {
        this.adapter.preLoop();

        for (Player player : plugin.getServer().getOnlinePlayers()) {
            Board board = this.playerBoards.get(player.getUniqueId());

            if (board == null) {
                continue;
            }

            try {
                Scoreboard scoreboard = board.getScoreboard();

                List<String> scores = this.adapter.getScoreboard(player, board);

                if (scores != null) {
                    Collections.reverse(scores);

                    Objective objective = board.getObjective();

                    if (!objective.getDisplayName().equals(this.adapter.getTitle(player))) {
                        objective.setDisplayName(this.adapter.getTitle(player));
                    }

                    if (scores.isEmpty()) {
                        Iterator<BoardEntry> iter = board.getEntries().iterator();
                        while (iter.hasNext()) {
                            BoardEntry boardEntry = iter.next();
                            boardEntry.remove();
                            iter.remove();
                        }
                        continue;
                    }

                    forILoop:
                    for (int i = 0; i < scores.size(); i++) {
                        String text = scores.get(i);
                        int position = i + 1;

                        for (BoardEntry boardEntry : new LinkedList<>(board.getEntries())) {
                            Score score = objective.getScore(boardEntry.getKey());

                            if (score != null && boardEntry.getText().equals(text)) {
                                if (score.getScore() == position) {
                                    continue forILoop;
                                }
                            }
                        }

                        Iterator<BoardEntry> iter = board.getEntries().iterator();
                        while (iter.hasNext()) {
                            BoardEntry boardEntry = iter.next();
                            int entryPosition = scoreboard.getObjective(DisplaySlot.SIDEBAR).getScore(boardEntry
                                    .getKey()).getScore();
                            if (entryPosition > scores.size()) {
                                boardEntry.remove();
                                iter.remove();
                            }
                        }

                        int positionToSearch = position - 1;

                        BoardEntry entry = board.getByPosition(positionToSearch);
                        if (entry == null) {
                            new BoardEntry(board, text).send(position);
                        } else {
                            entry.setText(text).setup().send(position);
                        }

                        if (board.getEntries().size() > scores.size()) {
                            iter = board.getEntries().iterator();
                            while (iter.hasNext()) {
                                BoardEntry boardEntry = iter.next();
                                if (!scores.contains(boardEntry.getText()) || Collections.frequency(board
                                        .getBoardEntriesFormatted(), boardEntry.getText()) > 1) {
                                    boardEntry.remove();
                                    iter.remove();
                                }
                            }
                        }
                    }
                } else {
                    if (!board.getEntries().isEmpty()) {
                        board.getEntries().forEach(BoardEntry::remove);
                        board.getEntries().clear();
                    }
                }

                this.adapter.onScoreboardCreate(player, scoreboard);

                player.setScoreboard(scoreboard);
            } catch (Exception e) {
                /*e.printStackTrace();

                plugin.getLogger().severe("Something went wrong while updating " + player.getName() + "'s scoreboard " +
                        "" + board + " - " + board.getAdapter() + ")");*/
            }
        }
    }

    public void addPlayer(Player player) {
        if (!getPlayerBoards().containsKey(player.getUniqueId())) {
            getPlayerBoards().put(player.getUniqueId(), new Board(PotSG.getInstance(), player, getAdapter()));
        }
    }

    public Board getBoardByPlayer(Player player) {
        if (getPlayerBoards().containsKey(player.getUniqueId())) {
            return getPlayerBoards().get(player.getUniqueId());
        }
        return null;
    }

    public void removePlayer(Player player) {
        if (getPlayerBoards().containsKey(player.getUniqueId())) {
            getPlayerBoards().remove(player.getUniqueId());
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (PotSG.getInstance().isPluginLoading()) {
            return;
        }
        addPlayer(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        removePlayer(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerKickEvent event) {
        removePlayer(event.getPlayer());
    }
}
