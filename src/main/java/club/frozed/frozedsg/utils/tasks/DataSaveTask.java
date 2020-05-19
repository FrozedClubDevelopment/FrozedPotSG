package me.elb1to.frozedsg.utils.tasks;

import me.elb1to.frozedsg.managers.PlayerDataManager;
import me.elb1to.frozedsg.player.PlayerData;

public class DataSaveTask implements Runnable {

    @Override
    public void run() {
        PlayerDataManager.getInstance().getPlayerDatas().values().forEach(PlayerData::save);
    }
}
