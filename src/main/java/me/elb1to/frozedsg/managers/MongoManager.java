package me.elb1to.frozedsg.managers;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import me.elb1to.frozedsg.PotSG;
import me.elb1to.frozedsg.utils.chat.Color;
import org.bson.Document;
import org.bukkit.Bukkit;

import java.util.Collections;

@Getter
public class MongoManager {
    @Getter
    public static MongoManager instance;
    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    private MongoCollection<Document> statsCollection;

    public MongoManager() {
        instance = this;
        try {
            if (PotSG.getInstance().getConfiguration("config").getBoolean("MONGODB.AUTHENTICATION.ENABLED")) {
                final MongoCredential credential = MongoCredential.createCredential(
                        PotSG.getInstance().getConfiguration("config").getString("MONGODB.AUTHENTICATION.USERNAME"),
                        PotSG.getInstance().getConfiguration("config").getString("MONGODB.AUTHENTICATION.DATABASE"),
                        PotSG.getInstance().getConfiguration("config").getString("MONGODB.AUTHENTICATION.PASSWORD").toCharArray()
                );
                mongoClient = new MongoClient(new ServerAddress(PotSG.getInstance().getConfiguration("config").getString("MONGODB.ADDRESS"), PotSG.getInstance().getConfiguration("config").getInt("MONGODB.PORT")), Collections.singletonList(credential));
            } else {
                mongoClient = new MongoClient(PotSG.getInstance().getConfiguration("config").getString("MONGODB.ADDRESS"), PotSG.getInstance().getConfiguration("config").getInt("MONGODB.PORT"));
            }
            mongoDatabase = mongoClient.getDatabase(PotSG.getInstance().getConfiguration("config").getString("MONGODB.DATABASE"));
            statsCollection = mongoDatabase.getCollection("Statistics");
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(Color.translate("&b[FrozedSG] &cFailed to connect to MongoDB"));
            Bukkit.getServer().getPluginManager().disablePlugin(PotSG.getInstance());
        }
    }
}
