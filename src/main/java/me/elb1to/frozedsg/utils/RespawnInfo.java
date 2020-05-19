package me.elb1to.frozedsg.utils;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

@Getter
@Setter
public class RespawnInfo {

    private Location location = null;
    private ItemStack[] inventory = null;
    private ItemStack[] armor = null;

    public RespawnInfo(Location location, ItemStack[] inventory, ItemStack[] armor) {
        this.location = location;
        this.inventory = inventory;
        this.armor = armor;
    }
}
