package me.elb1to.frozedsg.commands;

import me.elb1to.frozedsg.managers.PlayerDataManager;
import me.elb1to.frozedsg.player.PlayerData;
import me.elb1to.frozedsg.utils.chat.Color;
import me.elb1to.frozedsg.utils.command.BaseCommand;
import me.elb1to.frozedsg.utils.command.Command;
import me.elb1to.frozedsg.utils.command.CommandArgs;
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
