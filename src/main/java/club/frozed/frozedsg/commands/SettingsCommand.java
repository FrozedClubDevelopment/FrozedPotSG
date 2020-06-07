package club.frozed.frozedsg.commands;

import club.frozed.frozedsg.player.PlayerData;
import club.frozed.frozedsg.utils.chat.Color;
import club.frozed.frozedsg.managers.PlayerDataManager;
import club.frozed.frozedsg.utils.command.BaseCommand;
import club.frozed.frozedsg.utils.command.Command;
import club.frozed.frozedsg.utils.command.CommandArgs;
import org.bukkit.entity.Player;

public class SettingsCommand extends BaseCommand {

    @Command(name = "settings")
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        PlayerData data = PlayerDataManager.getInstance().getByUUID(player.getUniqueId());
        try {
            player.openInventory(data.getSettingsInventory());
        } catch (Exception e) {
            player.sendMessage(Color.translate("&cError while performing this command. Seems like your data is not loaded. Try joining again."));
        }
    }
}
