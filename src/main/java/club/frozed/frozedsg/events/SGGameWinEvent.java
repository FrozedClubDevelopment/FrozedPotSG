package me.elb1to.frozedsg.events;

import lombok.Getter;
import lombok.Setter;
import me.elb1to.frozedsg.managers.PlayerDataManager;
import me.elb1to.frozedsg.player.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@Setter
public class SGGameWinEvent extends Event {
    public Player player;
    public PlayerData playerData = null;
    public static HandlerList handlers = new HandlerList();


    public SGGameWinEvent(Player player) {
        this.player = player;
        if (PlayerDataManager.getInstance().getByUUID(player.getUniqueId()) != null) {
            this.playerData = PlayerDataManager.getInstance().getByUUID(player.getUniqueId());
        }
    }

    public SGGameWinEvent() {
        this.player = null;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
