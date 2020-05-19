package club.frozed.frozedsg.commands;


import club.frozed.frozedsg.managers.GameManager;
import club.frozed.frozedsg.managers.PlayerDataManager;
import club.frozed.frozedsg.player.PlayerData;
import club.frozed.frozedsg.utils.chat.Color;
import club.frozed.frozedsg.utils.command.BaseCommand;
import club.frozed.frozedsg.utils.command.Command;
import club.frozed.frozedsg.utils.command.CommandArgs;
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
