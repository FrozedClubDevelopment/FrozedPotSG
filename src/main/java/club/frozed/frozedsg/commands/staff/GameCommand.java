package club.frozed.frozedsg.commands.staff;

import club.frozed.frozedsg.PotSG;
import club.frozed.frozedsg.managers.ChestsManager;
import club.frozed.frozedsg.managers.GameManager;
import club.frozed.frozedsg.managers.WorldsManager;
import club.frozed.frozedsg.utils.Utils;
import club.frozed.frozedsg.utils.chat.Color;
import club.frozed.frozedsg.enums.GameState;
import club.frozed.frozedsg.utils.Clickable;
import club.frozed.frozedsg.utils.command.BaseCommand;
import club.frozed.frozedsg.utils.command.Command;
import club.frozed.frozedsg.utils.command.CommandArgs;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class GameCommand extends BaseCommand {
    @Command(name = "game", permission = "frozedsg.command.game.use")
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length == 0) {
            player.sendMessage(Color.translate("&7&m-----------------------------"));
            player.sendMessage(Color.translate("&3&lAvailable Commands&7:"));
            player.sendMessage(Color.translate("&7 * &b/game start &7- &fforce start game"));
            player.sendMessage(Color.translate("&7 * &b/game map &7- &fteleport to game map"));
            player.sendMessage(Color.translate("&7 * &b/game savemap &7- &fsave current game map (can't be undone)"));
            player.sendMessage(Color.translate("&7 * &b/game lobby &7- &fteleport to lobby location"));
            player.sendMessage(Color.translate("&7 * &b/game chests &7- &fsetup and edit chests"));
            player.sendMessage(Color.translate("&7 * &b/game spawn darena &7- &fspawn deathmatch arena before game starts (It will not be spawned in game!) (Can't be undone!)"));
            player.sendMessage(Color.translate("&7 * &b/game setgamespawn &7- &fset game map spawn location (also available in config as GAME-CENTER-LOCATION)"));
            player.sendMessage(Color.translate("&7&m-----------------------------"));
            return;
        }
        if (args.length == 1) {
            switch (args[0]) {
                case "start": {
                    if (GameManager.getInstance().getStartCountdown() != null && !GameManager.getInstance().getStartCountdown().hasExpired()) {
                        player.sendMessage(Color.translate("&bStart task is already running! The game will start soon..."));
                        return;
                    }
                    if (!GameManager.getInstance().getGameState().equals(GameState.LOBBY)) {
                        player.sendMessage(Color.translate("&bYou can only execute this command in lobby state."));
                        return;
                    }
                    Utils.broadcastMessage(GameManager.getInstance().getGamePrefix() + Color.translate("&aThe pre-match has been forced to start!"));
                    GameManager.getInstance().setForceStarted(true);
                    GameManager.getInstance().startGame();
                    break;
                }
                case "chests":
                case "chest": {
                    player.openInventory(ChestsManager.getInstance().chestsInventory());
                    break;
                }
                /*case "getchest": {
                    player.getInventory().addItem(GameManager.getInstance().getChestItem());
                    player.sendMessage(Color.translate("&bYou have received a new &f'Custom Chest'. Read lore of chest for instructions!"));
                    break;
                }*/
                case "map": {
                    Location loc = new Location(WorldsManager.getInstance().getGameWorld(), 0, 90, 0);
                    player.teleport(loc);
                    player.sendMessage(Color.translate("&aYou've been teleported to game map. You can edit it and save by typing command &f'/game savemap'&a!"));
                    break;
                }
                case "lobby": {
                    player.teleport(GameManager.getInstance().getLobbyLocation());
                    player.sendMessage(Color.translate("&aYou've been teleported to lobby."));
                    break;
                }
                case "savemap": {
                    if (!player.getWorld().getName().equalsIgnoreCase(WorldsManager.getInstance().getGameWorld().getName())) {
                        player.sendMessage(Color.translate("&bYou are not in game map. Please use &f'/game map' &bto teleport to game map!"));
                        return;
                    }
                    if (!GameManager.getInstance().getGameState().equals(GameState.LOBBY)) {
                        player.sendMessage(Color.translate("&cYou can only execute this argument in lobby mode."));
                        return;
                    }
                    WorldsManager.getInstance().getGameWorld().getPlayers().forEach(online -> {
                        player.teleport(GameManager.getInstance().getLobbyLocation());
                        player.sendMessage(Color.translate("&aYou have been teleport from game map since it is going to save it now."));
                    });
                    WorldsManager.getInstance().saveCurrentWorld();
                    player.sendMessage(Color.translate("&aCurrent map have been saved successfully."));
                    break;
                }
                case "setgamespawn" : {
                    if (!player.getWorld().getName().equalsIgnoreCase(WorldsManager.getInstance().getGameWorld().getName())) {
                        Clickable clickable = new Clickable("&cYou are only allowed to execute this argument in game world. Please use '/game map' to teleport to it or click this message.",
                                "&fClick to teleport to game map", "/game map");
                        clickable.sendToPlayer(player);
                        return;
                    }
                    int x = player.getLocation().getBlockX();
                    int y = player.getLocation().getBlockY();
                    int z = player.getLocation().getBlockZ();
                    String toSave = x + ";" + y + ";" + z;
                    PotSG.getInstance().getConfiguration("config").getConfiguration().set("GAME-CENTER-LOCATION", toSave);
                    PotSG.getInstance().getConfiguration("config").save();
                    player.sendMessage(Color.translate("&bYou have successfully set game map spawn location. &f(" + toSave + ")"));
                    break;
                }
                default: {
                    player.performCommand("game");
                    break;
                }
            }
        }
        if (args.length == 2) {
            switch (args[0]) {
                case "spawn" : {
                    if (args[1].equalsIgnoreCase("darena")) {
                        player.sendMessage(Color.translate(GameManager.getInstance().getGamePrefix() + "&bYou've successfully spawned deathmatch arena"));
                        GameManager.getInstance().spawnDeathMatchArena();
                        GameManager.getInstance().setDeathMatchArenaSpawned(true);
                        break;
                    }
                }
                default: {
                    player.performCommand("game");
                    break;
                }
            }
        }
    }
}
