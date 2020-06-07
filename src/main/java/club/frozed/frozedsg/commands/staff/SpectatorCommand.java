package club.frozed.frozedsg.commands.staff;

import club.frozed.frozedsg.enums.PlayerState;
import club.frozed.frozedsg.managers.GameManager;
import club.frozed.frozedsg.managers.PlayerDataManager;
import club.frozed.frozedsg.managers.PlayerManager;
import club.frozed.frozedsg.player.PlayerData;
import club.frozed.frozedsg.utils.Utils;
import club.frozed.frozedsg.utils.chat.Color;
import club.frozed.frozedsg.utils.command.BaseCommand;
import club.frozed.frozedsg.utils.command.Command;
import club.frozed.frozedsg.utils.command.CommandArgs;
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
            player.sendMessage(GameManager.getInstance().getGamePrefix() + Color.translate("&bWe couldn't find player by name &f'" + args[1] + "'&b."));
            return;
        }

        PlayerData data = PlayerDataManager.getInstance().getByUUID(target.getUniqueId());

        if (data == null) {
            player.sendMessage(GameManager.getInstance().getGamePrefix() + Color.translate("&bWe couldn't find profile of '" + args[1] + "'"));
            return;
        }

        if (args[0].equalsIgnoreCase("add")) {
            if (PlayerManager.getInstance().isSpectator(target)) {
                player.sendMessage(GameManager.getInstance().getGamePrefix() + Color.translate("&bIt seems that &f'" + args[1] + "' &bis already spectating!"));
                return;
            }
            Utils.broadcastMessageToSpectators(GameManager.getInstance().getGamePrefix() + "&3&l" + target.getName() + " &bhas been added to spectators by &3&l" + player.getName() + "&b.");
            PlayerManager.getInstance().setSpectating(target);
        }
        if (args[0].equalsIgnoreCase("remove")) {
            if (!PlayerManager.getInstance().isSpectator(target)) {
                player.sendMessage(GameManager.getInstance().getGamePrefix() + Color.translate("&bIt seems that &f'" + args[1] + "' &bis not spectating!"));
                return;
            }
            data.setState(PlayerState.LOBBY);
            Utils.broadcastMessageToSpectators(GameManager.getInstance().getGamePrefix() + "&3&l" + target.getName() + " &bhas been removed from spectators by &3&l" + player.getName() + "&b.");
            Utils.clearPlayer(player);
            player.teleport(GameManager.getInstance().getLobbyLocation());
            player.getInventory().clear();
            GameManager.getInstance().handleLobbyItems(player);
        }
    }
}
