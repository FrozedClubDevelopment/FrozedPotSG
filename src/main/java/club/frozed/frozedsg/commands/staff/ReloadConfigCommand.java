package club.frozed.frozedsg.commands.staff;

import club.frozed.frozedsg.PotSG;
import club.frozed.frozedsg.utils.chat.Color;
import club.frozed.frozedsg.utils.configurations.ConfigFile;
import club.frozed.frozedsg.utils.command.BaseCommand;
import club.frozed.frozedsg.utils.command.Command;
import club.frozed.frozedsg.utils.command.CommandArgs;

import java.util.ArrayList;
import java.util.List;

public class ReloadConfigCommand extends BaseCommand {
    @Command(name = "reloadfiles", isAdminOnly = true)

    public void onCommand(CommandArgs command) {
        PotSG.getInstance().getFiles().forEach(ConfigFile::save);

        List<String> names = new ArrayList<>();
        PotSG.getInstance().getFiles().forEach(configFile -> names.add(configFile.getName() + ".yml"));
        command.getPlayer().sendMessage(Color.translate("&aFiles have been reloaded successfully. &7[&b"
        + names.toString().replace("[", "")
                .replace("]", "")
                .replace(",", "&7,&b") + "&7]&a."));
    }
}
