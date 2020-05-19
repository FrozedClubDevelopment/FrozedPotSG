package club.frozed.frozedsg.utils.tasks;

import club.frozed.frozedsg.managers.GameManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BrewingStand;

import java.util.Iterator;
import java.util.Map;

public class BrewingTask implements Runnable {

    public void run() {
        Iterator<Map.Entry<Location, BrewingStand>> iter = GameManager.getInstance().getActiveBrewingStands().entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<Location, BrewingStand> entry = iter.next();
            if (!entry.getValue().getChunk().isLoaded() || entry.getKey().getBlock().getType() != Material.BREWING_STAND) {
                iter.remove();
            } else {
                BrewingStand stand = entry.getValue();
                if (stand.getBrewingTime() <= 1) {
                    continue;
                }
                stand.setBrewingTime(Math.max(1, stand.getBrewingTime() - 2));
            }
        }
    }
}
      