package club.frozed.frozedsg.events;

import club.frozed.frozedsg.player.PlayerData;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@Setter
public class SGWorldLoad extends Event {
    public World world;
    public PlayerData playerData = null;
    public static HandlerList handlers = new HandlerList();


    public SGWorldLoad(World world) {
        this.world = world;
    }

    public Location getCenterLocation() {
        int x = 0;
        int z = 0;
        double y = world.getHighestBlockYAt(x, z);
        return new Location(world, x, y, z);
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
