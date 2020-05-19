package club.frozed.frozedsg.utils.leaderboards;

import com.mongodb.BasicDBObject;
import lombok.Getter;
import lombok.Setter;
import club.frozed.frozedsg.PotSG;
import club.frozed.frozedsg.managers.MongoManager;
import org.bson.Document;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Leaderboard {
    private String name;
    private String mongoValue;
    private Material material;
    private List<String> formats = new ArrayList<>();
    private boolean enabled;

    public Leaderboard(Material material, String name, String mongoValue, boolean enabled) {
        this.name = name;
        this.mongoValue = mongoValue;
        this.material = material;
        this.enabled = enabled;

        load();
    }

    public void load() {
        formats.clear();
        List<Document> documents = MongoManager.getInstance().getStatsCollection().find().limit(10).sort(new BasicDBObject(mongoValue, -1)).into(new ArrayList<>());
        int pos = 1;
        if (documents == null) {
            return;
        }
        for (Document document : documents) {
            String format = PotSG.getInstance().getConfiguration("config").getString("LEADERBOARD-FORMAT");
            format = format.replace("<pos>", String.valueOf(pos));
            format = format.replace("<name>", document.getString("name") == null ? "null" : document.getString("name"));
            format = format.replace("<amount>", document.get(mongoValue).toString());
            if (document.getInteger(mongoValue) > 0) {
                formats.add(format);
            }
            pos++;
        }
    }
}
