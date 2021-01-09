package club.frozed.frozedsg.commands.staff;

import club.frozed.frozedsg.enums.PlayerState;
import club.frozed.frozedsg.managers.GameManager;
import club.frozed.frozedsg.managers.PlayerDataManager;
import club.frozed.frozedsg.player.PlayerData;
import club.frozed.frozedsg.utils.Utils;
import club.frozed.frozedsg.utils.chat.Color;
import club.frozed.frozedsg.utils.command.BaseCommand;
import club.frozed.frozedsg.utils.command.Command;
import club.frozed.frozedsg.utils.command.CommandArgs;
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
        target.setFlying(false);
        target.setAllowFlight(false);
        target.updateInventory();
        targetData.setRespawnInfo(null);
        targetData.setState(PlayerState.INGAME);

        player.sendMessage(Color.translate(GameManager.getInstance().getGamePrefix() + "&bYou've respawned &f'" + target.getName() + "'&b."));
        target.sendMessage(Color.translate(GameManager.getInstance().getGamePrefix() + "&bYou've been respawned by &f'" + player.getName() + "'&b."));
    }
}
