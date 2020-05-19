package me.elb1to.frozedsg.managers;

import com.mongodb.client.model.Filters;
import lombok.Data;
import lombok.Getter;
import me.elb1to.frozedsg.player.PlayerData;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
public class PlayerDataManager {
    @Getter
    public static PlayerDataManager instance;
    private Map<UUID, PlayerData> playerDatas = new HashMap<>();

    public PlayerDataManager() {
        instance = this;
    }

    public PlayerData getByUUID(UUID uuid) {
        return playerDatas.getOrDefault(uuid, null);
    }

    public void handleCreateData(UUID uuid) {
        if (!playerDatas.containsKey(uuid)) {
            playerDatas.put(uuid, new PlayerData(uuid));
        }
    }

    public void saveData(PlayerData data, String info, String value, int amount) {
        if (!hasData(info)) {
            return;
        }
        Document document = MongoManager.getInstance().getStatsCollection().find(Filters.eq("info", info)).first();
        document.put(value, amount);
        MongoManager.getInstance().getStatsCollection().replaceOne(Filters.eq("info", info), document);
        data.load();
    }

    private boolean hasData(String info) {
        Document document = MongoManager.getInstance().getStatsCollection().find(Filters.eq("info", info)).first();
        return document != null;
    }

}
