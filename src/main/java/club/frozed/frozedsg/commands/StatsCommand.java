package club.frozed.frozedsg.commands;

import club.frozed.frozedsg.managers.GameManager;
import club.frozed.frozedsg.player.PlayerData;
import club.frozed.frozedsg.utils.chat.Color;
import club.frozed.frozedsg.managers.InventoryManager;
import club.frozed.frozedsg.managers.PlayerDataManager;
import club.frozed.frozedsg.utils.command.BaseCommand;
import club.frozed.frozedsg.utils.command.Command;
import club.frozed.frozedsg.utils.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class StatsCommand extends BaseCommand {

    @Command(name = "stats")
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length == 0) {
            player.sendMessage(Color.translate("&cCorrect usage: /stats <player>."));
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(Color.translate(GameManager.getInstance().getGamePrefix() + "&cThat player is currently offline."));
            return;
        }

        PlayerData targetData = PlayerDataManager.getInstance().getByUUID(target.getUniqueId());
        if (targetData == null) {
            player.sendMessage(Color.translate(GameManager.getInstance().getGamePrefix() + "&cData of that player is not loaded."));
            return;
        }
        player.openInventory(InventoryManager.getInstance().getStatsInventory(targetData));
    }
}
