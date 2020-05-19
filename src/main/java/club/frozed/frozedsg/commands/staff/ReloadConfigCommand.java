package me.elb1to.frozedsg.commands.staff;

import me.elb1to.frozedsg.PotSG;
import me.elb1to.frozedsg.utils.chat.Color;
import me.elb1to.frozedsg.utils.command.BaseCommand;
import me.elb1to.frozedsg.utils.command.Command;
import me.elb1to.frozedsg.utils.command.CommandArgs;
import me.elb1to.frozedsg.utils.configurations.ConfigFile;

import java.util.ArrayList;
import java.util.List;

public class ReloadConfigCommand extends BaseCommand {
    @Command(name = "reloadfiles", isAdminOnly = true)

    public void onCommand(CommandArgs command) {
        PotSG.getInstance().getFiles().forEach(ConfigFile::save);

        List<String> names = new ArrayList<>();
        PotSG.getInstance().getFiles().forEach(configFile -> names.add(configFile.getName() + ".yml"));
        command.getPlayer().sendMessage(Color.translate("&aFiles have been reloaded successfully. &7[&e"
        + names.toString().replace("[", "")
                .replace("]", "")
                .replace(",", "&7,&e") + "&7]&a."));
    }
}
