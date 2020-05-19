package me.elb1to.frozedsg.commands.staff;

import me.elb1to.frozedsg.managers.GameManager;
import me.elb1to.frozedsg.managers.MongoManager;
import me.elb1to.frozedsg.managers.PlayerDataManager;
import me.elb1to.frozedsg.player.PlayerData;
import me.elb1to.frozedsg.utils.Utils;
import me.elb1to.frozedsg.utils.chat.Color;
import me.elb1to.frozedsg.utils.command.BaseCommand;
import me.elb1to.frozedsg.utils.command.Command;
import me.elb1to.frozedsg.utils.command.CommandArgs;
import me.elb1to.frozedsg.utils.leaderboards.LeaderboardManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class DataCommand extends BaseCommand {
    @Command(name = "data", isAdminOnly = true)

    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length == 1 && args[0].equals("update-leaderboards")) {
            player.sendMessage(Color.translate(GameManager.getInstance().getGamePrefix() + "&aUpdating leaderboards..."));
            LeaderboardManager.getInstance().updateAllLeaderboards();
            player.sendMessage(Color.translate(GameManager.getInstance().getGamePrefix() + "&aAll leaderboards have been updated."));
            return;
        }
        if (args.length == 1 && args[0].equals("dropAll")) {
            /*MongoManager.getInstance().getStatsCollection().drop();
            LeaderboardManager.getInstance().updateAllLeaderboards();
            PlayerDataManager.getInstance().getPlayerDatas().clear();
            Utils.getOnlinePlayers().forEach(online -> PlayerDataManager.getInstance().handeCreateData(online.getUniqueId()));
            player.sendMessage(Color.translate(GameManager.getInstance().getGamePrefix() + "&eYou have dropped all data collections."));*/
            player.sendMessage(Color.translate("&7&m-------------------------------------------"));
            player.sendMessage(Color.translate("&4&lWARNING&c: This action is irreversible once done."));
            player.sendMessage(Color.translate("&8"));
            player.sendMessage(Color.translate("&cIf you are sure that you want to drop (erase)"));
            player.sendMessage(Color.translate("&call the data stored on Mongo for SG, please"));
            player.sendMessage(Color.translate("&cexecute the command: &o/data dropConfirm"));
            player.sendMessage(Color.translate("&7&m-------------------------------------------"));
            return;
        }
        if (args.length == 1 && args[0].equals("dropConfirm")) {
            player.sendMessage(Color.translate(GameManager.getInstance().getGamePrefix() + "&cDropping all saved data on Mongo for SG..."));
            MongoManager.getInstance().getStatsCollection().drop();
            LeaderboardManager.getInstance().updateAllLeaderboards();
            PlayerDataManager.getInstance().getPlayerDatas().clear();
            Utils.getOnlinePlayers().forEach(online -> PlayerDataManager.getInstance().handleCreateData(online.getUniqueId()));
            player.sendMessage(Color.translate(GameManager.getInstance().getGamePrefix() + "&eYou have dropped all data collections stored on Mongo for SG."));
            return;
        }
        if (args.length < 4) {
            player.sendMessage(Color.translate("&7&m-------------------------------------"));
            player.sendMessage(Color.translate("&6&lAvailable Commands&7:"));
            player.sendMessage(Color.translate("&e/data update-leaderboards &7- &fupdate leaderboards"));
            player.sendMessage(Color.translate("&e/data dropAll &7- &fdrop all data and statistics (can't be undone)"));
            player.sendMessage(Color.translate("&8"));
            LeaderboardManager.getInstance().getLeaderboardList().forEach(leaderboard -> {
                player.sendMessage(Color.translate("&e/data <player> set " + leaderboard.getMongoValue() + " <number> &7- &fset players " + leaderboard.getMongoValue() + "&a."));

            });
            player.sendMessage(Color.translate("&7&m-------------------------------------"));
        }
        if (args.length == 4) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(Color.translate("&cThat player is currently offline."));
                return;
            }
            PlayerData targetData = PlayerDataManager.getInstance().getByUUID(target.getUniqueId());
            if (targetData == null) {
                player.sendMessage(Color.translate("&cData of player '" + args[0] + "' can't be found!."));
                return;
            }
            if (!args[1].equalsIgnoreCase("set")) {
                player.performCommand("data");
                return;
            }
            if (!isMongoValueAvailable(args[2])) {
                player.sendMessage(Color.translate("&cData value for " + args[2] + " couldn't be found!"));
                return;
            }
            if (!targetData.hasData()) {
                player.sendMessage(Color.translate("&cData of " + args[0] + " is not created. Can't be edited."));
                return;
            }
            if (!Utils.isInteger(args[3])) {
                player.sendMessage(Color.translate("&cPlease use numbers for amount you want to enter!"));
                return;
            }
            PlayerDataManager.getInstance().saveData(targetData, (GameManager.getInstance().isServerPremium() ? targetData.getUuid().toString() : targetData.getName()), args[2], Integer.parseInt(args[3]));
            player.sendMessage(Color.translate(GameManager.getInstance().getGamePrefix() + "&eYou've set " + args[2] + " of &f'" + target.getName() + "' &eto &f" + args[3] + "&e."));
        }
    }

    private boolean isMongoValueAvailable(String value) {
        return LeaderboardManager.getInstance().getLeaderboardList().stream().anyMatch(leaderboard -> leaderboard.getMongoValue().equals(value));
    }
}