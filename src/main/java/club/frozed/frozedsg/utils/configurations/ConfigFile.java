package me.elb1to.frozedsg.utils.configurations;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Data;
import lombok.Getter;
import me.elb1to.frozedsg.PotSG;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

@Data
public class ConfigFile {
    @Getter
    public static ConfigFile instance;
    private YamlConfiguration configuration;
    private String name;
    private File file;

    public ConfigFile(String name) {
        this.name = name;
        this.file = new File(PotSG.getInstance().getDataFolder(), name + ".yml");
        if (!this.file.exists()) {
            PotSG.getInstance().saveResource(name + ".yml", false);
        }
        this.configuration = YamlConfiguration.loadConfiguration(this.file);
        instance = this;
    }

    public boolean getBoolean(String path) {
        return (this.configuration.contains(path)) && (this.configuration.getBoolean(path));
    }

    public double getDouble(String path) {
        if (this.configuration.contains(path)) {
            return this.configuration.getDouble(path);
        }
        return 0.0D;
    }

    public int getInt(String path) {
        if (this.configuration.contains(path)) {
            return this.configuration.getInt(path);
        }
        return 0;
    }

    public String getString(String path) {
        if (this.configuration.contains(path)) {
            return ChatColor.translateAlternateColorCodes('&', this.configuration.getString(path));
        }
        return "String at path: " + path + " not found!";
    }

    public String getUnColoredString(String path) {
        if (this.configuration.contains(path)) {
            return this.configuration.getString(path);
        }
        return "String at path: " + path + " not found!";
    }

    public List<String> getStringList(String path) {
        if (this.configuration.contains(path)) {
            ArrayList<String> strings = new ArrayList<String>();
            for (String string : this.configuration.getStringList(path)) {
                strings.add(ChatColor.translateAlternateColorCodes('&', string));
            }
            return strings;
        }
        return Arrays.asList("String List at path: " + path + " not found!");
    }

    public void load() {
        this.file = new File(PotSG.getInstance().getDataFolder(), name + ".yml");
        if (!this.file.exists()) {
            PotSG.getInstance().saveResource(name + ".yml", false);
        }
        this.configuration = YamlConfiguration.loadConfiguration(this.file);
    }

    public void save() {
        try {
            this.configuration.save(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
