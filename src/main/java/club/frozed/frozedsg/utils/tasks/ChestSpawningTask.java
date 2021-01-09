package club.frozed.frozedsg.utils.tasks;

/**
 * Created by Elb1to
 * Project: FrozedSG
 * Date: 09/22/2020 @ 12:05
 */
public class ChestSpawningTask implements Runnable {

    @Override
    public void run() {

        /*
         * Chest Spawner Logic
         *
         * 1. Get name_MAP_FOR_USE world
         * 2. Get all blocks in name_MAP_FOR_USE
         * 3. Get starting border -> private int startBorder = PotSG.getInstance().getConfiguration("config").getInt("BORDER.START");
         * 4. Get highest blocks at Y position in name_MAP_FOR_USE
         * 5. Do setBlock method at highest Y block location
         */
    }
}
