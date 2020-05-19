package me.elb1to.frozedsg.commands;


import me.elb1to.frozedsg.managers.GameManager;
import me.elb1to.frozedsg.managers.PlayerDataManager;
import me.elb1to.frozedsg.player.PlayerData;
import me.elb1to.frozedsg.utils.chat.Color;
import me.elb1to.frozedsg.utils.command.BaseCommand;
import me.elb1to.frozedsg.utils.command.Command;
import me.elb1to.frozedsg.utils.command.CommandArgs;
import org.bukkit.entity.Player;

public class SpecChatCommand extends BaseCommand {
    @Command(name = "spectatorchat", permission = "frozedsg.command.spectatorchat.use", aliases = {"specchat", "spc"})
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        PlayerData data = PlayerDataManager.getInstance().getByUUID(player.getUniqueId());
        data.setSpecChat(!data.isSpecChat());
        player.sendMessage(Color.translate(GameManager.getInstance().getGamePrefix() + "&eYour spectator chat is now "
        + (data.isSpecChat() ? "&aenabled" : "&cdisabled") + "&e."));
    }
}
