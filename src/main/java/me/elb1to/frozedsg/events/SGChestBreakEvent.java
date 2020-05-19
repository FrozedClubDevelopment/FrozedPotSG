package me.elb1to.frozedsg.events;

import lombok.Getter;
import lombok.Setter;
import me.elb1to.frozedsg.managers.PlayerDataManager;
import me.elb1to.frozedsg.player.PlayerData;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;

@Getter
@Setter
public class SGChestBreakEvent extends Event {
    public Player player;
    public PlayerData playerData = null;
    public Location chestLocation;
    public Inventory inventory;
    public static HandlerList handlers = new HandlerList();


    public SGChestBreakEvent(Player player, Inventory inventory, Location location) {
        this.player = player;
        this.chestLocation = location;
        this.inventory = inventory;
        if (PlayerDataManager.getInstance().getByUUID(player.getUniqueId()) != null) {
            this.playerData = PlayerDataManager.getInstance().getByUUID(player.getUniqueId());
        }
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
