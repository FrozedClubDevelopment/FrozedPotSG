package me.elb1to.frozedsg.utils.runnables;


import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import lombok.Getter;
import lombok.Setter;
import me.elb1to.frozedsg.PotSG;
import me.elb1to.frozedsg.border.BorderManager;
import me.elb1to.frozedsg.managers.GameManager;
import me.elb1to.frozedsg.managers.MongoManager;
import me.elb1to.frozedsg.managers.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
@Setter
public class DataRunnable extends BukkitRunnable {
    @Getter
    public static DataRunnable instance;
    public String name;
    public DBCollection informationCollection;


    public DataRunnable() {
        informationCollection = MongoManager.getInstance().getMongoClient().getDB(PotSG.getInstance().getConfiguration("config").getString("MONGODB.DATABASE")).getCollection("FrozedSGStats");
        instance = this;
        name = Bukkit.getServerName();
        this.runTaskTimerAsynchronously(PotSG.getInstance(), 2L, 2L);

        loadDataIfExists();
    }

    @Override
    public void run() {
        saveData();
    }

    public boolean hasData() {
        DBObject r = new BasicDBObject("name", name);
        DBObject found = informationCollection.findOne(r);

        if (found != null) {
            return true;
        }

        return false;
    }

    public void saveData() {
        DBObject r = new BasicDBObject("name", name);
        DBObject found = informationCollection.findOne(r);

        if (hasData()) {
            DBObject obj = new BasicDBObject("name", name);

            obj.put("map", PotSG.getInstance().isPluginLoading());
            if (GameManager.getInstance().getGameRunnable() != null) {
                obj.put("gametime", GameManager.getInstance().getGameRunnable().getTime());
            } else {
                obj.put("gametime", "00:00");
            }
            if (BorderManager.getInstance().getBorder() != null) {
                obj.put("border", BorderManager.getInstance().getBorder().getSize() + " " + BorderManager.getInstance().getBorderInfo());
            }
            obj.put("players", PlayerManager.getInstance().getGamePlayers().size());

            informationCollection.update(found, obj);
        }
    }

    public int getInt(Object o) {
        return Integer.parseInt(String.valueOf(o));
    }

    public void createData() {
        DBObject obj = new BasicDBObject("name", name);

        obj.put("map", PotSG.getInstance().isPluginLoading());
        if (GameManager.getInstance().getGameRunnable() != null) {
            obj.put("gametime", GameManager.getInstance().getGameRunnable().getTime());
        } else {
            obj.put("gametime", "00:00");
        }
        if (BorderManager.getInstance().getBorder() != null) {
            obj.put("border", BorderManager.getInstance().getBorder().getSize() + " " + BorderManager.getInstance().getBorderInfo());
        }
        obj.put("players", PlayerManager.getInstance().getGamePlayers().size());

        informationCollection.insert(obj);
    }

    public void loadDataIfExists() {
        if (!hasData()) {
            createData();
        }
    }
}
