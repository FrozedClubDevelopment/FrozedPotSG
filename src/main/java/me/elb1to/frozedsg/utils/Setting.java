package me.elb1to.frozedsg.utils;

import lombok.Data;
import org.bukkit.Material;

@Data
public class Setting {
    private boolean enabled;
    private Material material;
    private String name;
    private String[] description;
    private int data;
    private int requiredPoints;

    public Setting(String name, Material material, int requiredPoints, String... description) {
        this.material = material;
        this.name = name;
        this.description = description;
        this.requiredPoints = requiredPoints;
    }

    public Setting(String name, Material material, boolean enabled, int requiredPoints, String... description) {
        this.material = material;
        this.name = name;
        this.description = description;
        this.enabled = enabled;
        this.requiredPoints = requiredPoints;
    }

    public Setting(String name, Material material, boolean enabled, int data, int requiredPoints, String... description) {
        this.material = material;
        this.name = name;
        this.description = description;
        this.enabled = enabled;
        this.data = data;
        this.requiredPoints = requiredPoints;
    }
}
