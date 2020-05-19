package club.frozed.frozedsg.commands;

import club.frozed.frozedsg.PotSG;
import club.frozed.frozedsg.managers.GameManager;
import club.frozed.frozedsg.utils.Utils;
import club.frozed.frozedsg.utils.chat.Color;
import club.frozed.frozedsg.enums.GameState;
import club.frozed.frozedsg.utils.command.BaseCommand;
import club.frozed.frozedsg.utils.command.Command;
import club.frozed.frozedsg.utils.command.CommandArgs;
import org.bukkit.entity.Player;

public class AnnounceCommand extends BaseCommand {
    @Command(name = "announce", permission = "frozedsg.announce", aliases = {"ann", "sgannounce"})
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        if (!GameManager.getInstance().getGameState().equals(GameState.LOBBY)) {
            player.sendMessage(Color.translate("&cGame can't be announced since it is already started!"));
            return;
        }
        if (!PotSG.getInstance().getConfiguration("config").getBoolean("ANNOUNCE.ENABLED")) {
            player.sendMessage(Color.translate("&cAnnounce is not enabled!"));
            return;
        }
        String format = PotSG.getInstance().getConfiguration("config").getString("ANNOUNCE.FORMAT");
        format = format.replace("<server_name>", GameManager.getInstance().getServerName());
        format = format.replace("<player_display_name>", player.getDisplayName());

        if (!PotSG.getInstance().getConfiguration("config").getBoolean("ANNOUNCE.BUNGEE")) {
            Utils.broadcastMessage(format);
        } else {
            Utils.sendGlobalMessage(player, format);
        }
    }
}
