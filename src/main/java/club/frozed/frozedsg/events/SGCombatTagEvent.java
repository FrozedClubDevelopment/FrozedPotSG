package club.frozed.frozedsg.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@Setter
public class SGCombatTagEvent extends Event {
    public Player target;
    public Player player;
    public static HandlerList handlers = new HandlerList();


    public SGCombatTagEvent(Player player, Player target) {
        this.player = player;
        this.target = target;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
