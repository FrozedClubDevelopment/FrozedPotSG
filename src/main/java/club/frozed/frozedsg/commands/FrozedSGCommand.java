package club.frozed.frozedsg.commands;

import club.frozed.frozedsg.PotSG;
import club.frozed.frozedsg.utils.chat.Color;
import club.frozed.frozedsg.utils.command.BaseCommand;
import club.frozed.frozedsg.utils.command.Command;
import club.frozed.frozedsg.utils.command.CommandArgs;
import org.bukkit.entity.Player;

public class FrozedSGCommand extends BaseCommand {
    @Command(name = "frozedsg", aliases = {"sg", "potsg", "info", "scalebound", "elb1to"})
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        if (player.isOp()) {
            player.sendMessage(Color.translate("&7&m----------------------------------"));
            player.sendMessage(Color.translate("&7This server is using &b&lFrozed&f&lSG"));
            player.sendMessage(Color.translate("&7Authors&8: &b" + PotSG.getInstance().getDescription().getAuthors()));
            player.sendMessage(Color.translate("&eVersion&7: &b" + PotSG.getInstance().getDescription().getVersion()));
            player.sendMessage(Color.translate("&8"));
            player.sendMessage(Color.translate("&b&lFrozedSG Help &7- &fUseful Commands"));
            player.sendMessage(Color.translate("&7* &b/game &8- &fShows all cmds for game management"));
            player.sendMessage(Color.translate("&7* &b/data &8- &fShows all cmds for data management"));
            player.sendMessage(Color.translate("&7* &b/stats &8- &fCheck your Stats"));
            player.sendMessage(Color.translate("&7* &b/respawn &8- &fRespawns a player"));
            player.sendMessage(Color.translate("&7* &b/announce &8- &fBungee Announce for the match"));
            player.sendMessage(Color.translate("&7* &b/settings &8- &fChange your SG settings"));
            player.sendMessage(Color.translate("&7* &b/reloadfiles &8- &fReload all SG files"));
            player.sendMessage(Color.translate("&7* &b/spectatorchat &8- &fJoin the Specs chat"));
            player.sendMessage(Color.translate("&7* &b/spectator add:remove <player>"));
            player.sendMessage(Color.translate("&7&m----------------------------------"));
        } else {
            player.sendMessage(Color.translate("&7&m----------------------------------"));
            player.sendMessage(Color.translate("&7This server is using &b&lFrozed&f&lSG"));
            player.sendMessage(Color.translate("&7Authors&8: &b" + PotSG.getInstance().getDescription().getAuthors()));
            player.sendMessage(Color.translate("&7Version&8: &b" + PotSG.getInstance().getDescription().getVersion()));
            player.sendMessage(Color.translate("&8"));
            player.sendMessage(Color.translate("&b&lFrozedSG Help &7- &fUseful Commands"));
            player.sendMessage(Color.translate("&7* &b/stats &8- &fCheck your Stats"));
            player.sendMessage(Color.translate("&7* &b/settings &8- &fChange your SG settings"));
            player.sendMessage(Color.translate("&7&m----------------------------------"));
        }
    }
}
