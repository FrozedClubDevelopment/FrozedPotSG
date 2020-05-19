package club.frozed.frozedsg.border;

import club.frozed.frozedsg.PotSG;
import club.frozed.frozedsg.managers.GameManager;
import club.frozed.frozedsg.utils.Utils;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;

public class BorderTask extends BukkitRunnable {

    @Override
    public void run() {
        Border border = BorderManager.getInstance().getBorder();
        if (border == null) {
            return;
        }

        if (border.getSize() <= border.getLastBorder()) {
            this.cancel();
        }

        if (border.getSeconds() > 0) {
            border.increaseSeconds();
        }

        if (border.getSeconds() == 0) {
            border.setSeconds(BorderManager.getInstance().getShrinkEvery());
            border.shrinkBorder(border.getNextBorder());
            Utils.playSound(Sound.FIREWORK_BLAST);
            Utils.broadcastMessage(GameManager.getInstance().getBorderPrefix() + PotSG.getInstance().getConfiguration("messages").getString("border-strunk")
                    .replaceAll("<border_size", String.valueOf(border.getSize()))
            );
        }
        int minutes = border.getSeconds() / 60;

        if (Arrays.asList(5, 4, 3, 2, 1).contains(minutes) && border.getSeconds() == (minutes * 60)) {
            Utils.playSound(Sound.NOTE_PLING);
            Utils.broadcastMessage(GameManager.getInstance().getBorderPrefix() + PotSG.getInstance().getConfiguration("messages").getString("border-strink-minutes")
                    .replaceAll("<next_border>", String.valueOf(border.getNextBorder()))
                    .replaceAll("<minutes>", String.valueOf(minutes))
            );
        }
        if (Arrays.asList(30, 20, 10, 5, 4, 3, 2, 1).contains(border.getSeconds())) {
            Utils.playSound(Sound.NOTE_PLING);
            Utils.broadcastMessage(GameManager.getInstance().getBorderPrefix() + PotSG.getInstance().getConfiguration("messages").getString("border-strink-seconds")
                    .replaceAll("<next_border>", String.valueOf(border.getNextBorder()))
                    .replaceAll("<seconds>", String.valueOf(border.getSeconds()))
            );
        }
    }
}
