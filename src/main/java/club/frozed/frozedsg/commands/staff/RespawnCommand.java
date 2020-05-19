package me.elb1to.frozedsg.commands.staff;

import me.elb1to.frozedsg.enums.PlayerState;
import me.elb1to.frozedsg.managers.GameManager;
import me.elb1to.frozedsg.managers.PlayerDataManager;
import me.elb1to.frozedsg.player.PlayerData;
import me.elb1to.frozedsg.utils.Utils;
import me.elb1to.frozedsg.utils.chat.Color;
import me.elb1to.frozedsg.utils.command.BaseCommand;
import me.elb1to.frozedsg.utils.command.Command;
import me.elb1to.frozedsg.utils.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class RespawnCommand extends BaseCommand {

    @Command(name = "respawn", permission = "frozedsg.command.respawn.use")
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length == 0) {
            player.sendMessage(Color.translate("&cCorrect usage: /respawn <playerName>"));
            return;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(Color.translate("&cThat player is currently offline."));
            return;
        }
        PlayerData targetData = PlayerDataManager.getInstance().getByUUID(target.getUniqueId());
        if (targetData == null) {
            player.sendMessage(Color.translate("&cData of that player couldn't be found."));
            return;
        }
        if (targetData.getRespawnInfo() == null) {
            player.sendMessage(Color.translate("&cFailed to find respawn informations for '" + args[0] + "'."));
            return;
        }
        Utils.clearPlayer(target);
        target.teleport(targetData.getRespawnInfo().getLocation());
        target.getInventory().setContents(targetData.getRespawnInfo().getInventory());
        target.getInventory().setArmorContents(targetData.getRespawnInfo().getArmor());
        target.updateInventory();
        targetData.setRespawnInfo(null);
        targetData.setState(PlayerState.INGAME);

        player.sendMessage(Color.translate(GameManager.getInstance().getGamePrefix() + "&6You've respawned &f'" + target.getName() + "'&6."));
        target.sendMessage(Color.translate(GameManager.getInstance().getGamePrefix() + "&6You've been respawned by &f'" + player.getName() + "'&6."));
    }
}
