package me.elb1to.frozedsg.utils.command;

import me.elb1to.frozedsg.PotSG;

public abstract class BaseCommand {

	public PotSG main = PotSG.getInstance();

	public BaseCommand() {
		main.getFramework().registerCommands(this);
	}

	public abstract void onCommand(CommandArgs command);

}
