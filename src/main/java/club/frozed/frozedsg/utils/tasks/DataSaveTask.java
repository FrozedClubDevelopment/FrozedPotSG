package club.frozed.frozedsg.utils.tasks;

import club.frozed.frozedsg.managers.PlayerDataManager;
import club.frozed.frozedsg.player.PlayerData;

public class DataSaveTask implements Runnable {

    @Override
    public void run() {
        PlayerDataManager.getInstance().getPlayerDatas().values().forEach(PlayerData::save);
    }
}
