package me.elb1to.frozedsg.commands.staff;


import me.elb1to.frozedsg.enums.PlayerState;
import me.elb1to.frozedsg.managers.GameManager;
import me.elb1to.frozedsg.managers.PlayerDataManager;
import me.elb1to.frozedsg.managers.PlayerManager;
import me.elb1to.frozedsg.player.PlayerData;
import me.elb1to.frozedsg.utils.Utils;
import me.elb1to.frozedsg.utils.chat.Color;
import me.elb1to.frozedsg.utils.command.BaseCommand;
import me.elb1to.frozedsg.utils.command.Command;
import me.elb1to.frozedsg.utils.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SpectatorCommand extends BaseCommand {

    @Command(name = "spectator", permission = "frozedsg.command.spectator.use")
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length < 2){
            player.sendMessage(Color.translate("&cCorrect usage &c/spectator add <player>"));
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);

        if (target == null) {
            player.sendMessage(GameManager.getInstance().getGamePrefix() + Color.translate("&eWe couldn't find player by name &f'" + args[1] + "'&e."));
            return;
        }

        PlayerData data = PlayerDataManager.getInstance().getByUUID(target.getUniqueId());

        if (data == null) {
            player.sendMessage(GameManager.getInstance().getGamePrefix() + Color.translate("&eWe couldn't find profile of '" + args[1] + "'"));
            return;
        }

        if (args[0].equalsIgnoreCase("add")) {
            if (PlayerManager.getInstance().isSpectator(target)) {
                player.sendMessage(GameManager.getInstance().getGamePrefix() + Color.translate("&eIt seems that &f'" + args[1] + "' &eis already spectating!"));
                return;
            }
            Utils.broadcastMessageToSpectators(GameManager.getInstance().getGamePrefix() + "&6&l" + target.getName() + " &ehas been added to spectators by &6&l" + player.getName() + "&e.");
            PlayerManager.getInstance().setSpectating(target);
        }
        if (args[0].equalsIgnoreCase("remove")) {
            if (!PlayerManager.getInstance().isSpectator(target)) {
                player.sendMessage(GameManager.getInstance().getGamePrefix() + Color.translate("&eIt seems that &f'" + args[1] + "' &eis not spectating!"));
                return;
            }
            data.setState(PlayerState.LOBBY);
            Utils.broadcastMessageToSpectators(GameManager.getInstance().getGamePrefix() + "&6&l" + target.getName() + " &ehas been removed from spectators by &6&l" + player.getName() + "&e.");
            Utils.clearPlayer(player);
            player.teleport(GameManager.getInstance().getLobbyLocation());
            player.getInventory().clear();
            GameManager.getInstance().handleLobbyItems(player);
        }
    }
}
