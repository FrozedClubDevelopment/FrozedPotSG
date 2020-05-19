package me.elb1to.frozedsg.listeners;

import com.google.common.base.Preconditions;
import me.elb1to.frozedsg.PotSG;
import me.elb1to.frozedsg.enums.GameState;
import me.elb1to.frozedsg.managers.GameManager;
import me.elb1to.frozedsg.managers.PlayerDataManager;
import me.elb1to.frozedsg.player.PlayerData;
import me.elb1to.frozedsg.utils.chat.Color;
import net.minecraft.server.v1_8_R3.EntityLiving;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

//import net.minecraft.server.v1_7_R4.EntityLiving;
//import org.bukkit.craftbukkit.v1_7_R4.entity.CraftEntity;
//import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;

public class DeathMessagesListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void handlePlayerDeathEvent(PlayerDeathEvent e) {
        if (GameManager.getInstance().getGameState() != GameState.INGAME) {
            e.setDeathMessage(null);
            return;
        }

        String message = e.getDeathMessage();
        if (message == null || message.isEmpty()) {
            return;
        }

        Player player = e.getEntity();
        if (player.getKiller() != null) {
            Player playerKiller = e.getEntity().getKiller();
            PlayerData killerData = PlayerDataManager.getInstance().getByUUID(playerKiller.getUniqueId());
            killerData.getGameKills().increaseAmount(1);
            killerData.getKills().increaseAmount(1);

            if (killerData.getSettingByName("Lightning-On-Kill") != null && killerData.getSettingByName("Lightning-On-Kill").isEnabled()) {
                Location location = player.getLocation();
                World world = player.getWorld();
                world.strikeLightningEffect(location);
            }
        }
        e.setDeathMessage(Color.translate(this.getDeathMessage(message, e.getEntity(), this.getKiller(e))));
    }

    public CraftEntity getKiller(PlayerDeathEvent event) {
        EntityLiving lastAttacker = ((CraftPlayer) event.getEntity()).getHandle().lastDamager;
        return (lastAttacker == null) ? null : lastAttacker.getBukkitEntity();
    }

    public String getDeathMessage(String input, LivingEntity entity, Entity killer) {
        String name;
        if (entity instanceof Player) {
            Player player = (Player) entity;
            name = PotSG.getInstance().getConfiguration("messages").getString("death-messages.victim-name-format")
                    .replaceAll("<victim>", player.getName())
                    .replaceAll("<victim_kills>", String.valueOf(PlayerDataManager.getInstance().getByUUID(entity.getUniqueId()).getGameKills().getAmount()));
        } else {
            name = entity.getCustomName();
        }

        if (entity.getLastDamageCause() != null) {
            String killerName = "";
            if (killer != null) {
                killerName = (killer instanceof Player) ? "&a" + PotSG.getInstance().getConfiguration("messages").getString("death-messages.killer-name-format")
                        .replaceAll("<killer>", ((Player) killer).getName())
                        .replaceAll("<killer_kills>", String.valueOf(PlayerDataManager.getInstance().getByUUID(killer.getUniqueId()).getGameKills().getAmount())) : getEntityName(killer);
            }
            switch (entity.getLastDamageCause().getCause()) {
                case BLOCK_EXPLOSION:
                    input = (PotSG.getInstance().getConfiguration("messages").getString("death-messages.block-explosion").replaceAll("<victim_format>", name));
                    break;
                case CONTACT:
                    input = (PotSG.getInstance().getConfiguration("messages").getString("death-messages.contact").replaceAll("<victim_format>", name));
                    break;
                case DROWNING:
                    if (killer != null) {
                        input = (PotSG.getInstance().getConfiguration("messages").getString("death-messages.drowning-contains-killer")
                                .replaceAll("<victim_format>", name)
                                .replaceAll("<killer_format>", killerName));
                    } else {
                        input = (PotSG.getInstance().getConfiguration("messages").getString("death-messages.drowning").replaceAll("<victim_format>", name));
                    }
                    break;
                case ENTITY_ATTACK:
                    if (killer != null) {
                        input = (PotSG.getInstance().getConfiguration("messages").getString("death-messages.entity-attack")
                                .replaceAll("<victim_format>", name)
                                .replaceAll("<killer_format>", killerName));
                    }
                    break;
                case FALL:
                    if (killer != null) {
                        input = (PotSG.getInstance().getConfiguration("messages").getString("death-messages.fall-contains-killer")
                                .replaceAll("<victim_format>", name)
                                .replaceAll("<killer_format>", killerName));
                    } else {
                        input = (PotSG.getInstance().getConfiguration("messages").getString("death-messages.fall").replaceAll("<victim_format>", name));
                    }
                    break;
                case FALLING_BLOCK:
                    input = (PotSG.getInstance().getConfiguration("messages").getString("death-messages.falling-block")
                            .replaceAll("<victim_format>", name));
                    break;
                case FIRE:
                    if (killer != null) {
                        input = (PotSG.getInstance().getConfiguration("messages").getString("death-messages.fire-contains-killer")
                                .replaceAll("<victim_format>", name)
                                .replaceAll("<killer_format>", killerName));
                    } else {
                        input = (PotSG.getInstance().getConfiguration("messages").getString("death-messages.fire").replaceAll("<victim_format>", name));
                    }
                    break;
                case FIRE_TICK:
                    if (killer != null) {
                        input = (PotSG.getInstance().getConfiguration("messages").getString("death-messages.fire-tick-contains-killer")
                                .replaceAll("<victim_format>", name)
                                .replaceAll("<killer_format>", killerName));
                    } else {
                        input = (PotSG.getInstance().getConfiguration("messages").getString("death-messages.fire-tick").replaceAll("<victim_format>", name));
                    }
                    break;
                case LAVA:
                    if (killer != null) {
                        input = (PotSG.getInstance().getConfiguration("messages").getString("death-messages.lava-contains-killer")
                                .replaceAll("<victim_format>", name)
                                .replaceAll("<killer_format>", killerName));
                    } else {
                        input = (PotSG.getInstance().getConfiguration("messages").getString("death-messages.lava").replaceAll("<victim_format>", name));
                    }
                    break;
                case LIGHTNING:
                    input = (PotSG.getInstance().getConfiguration("messages").getString("death-messages.lightning")
                            .replaceAll("<victim_format>", name));
                    break;
                case POISON:
                    input = (PotSG.getInstance().getConfiguration("messages").getString("death-messages.poison")
                            .replaceAll("<victim_format>", name));
                    break;
                case PROJECTILE:
                    if (killer != null) {
                        input = (PotSG.getInstance().getConfiguration("messages").getString("death-messages.projectile-contains-killer")
                                .replaceAll("<victim_format>", name)
                                .replaceAll("<killer_format>", killerName));
                    } else {
                        input = (PotSG.getInstance().getConfiguration("messages").getString("death-messages.projectile").replaceAll("<victim_format>", name));
                    }
                    break;
                case STARVATION:
                    input = (PotSG.getInstance().getConfiguration("messages").getString("death-messages.starvation")
                            .replaceAll("<victim_format>", name));
                    break;
                case SUFFOCATION:
                    input = (PotSG.getInstance().getConfiguration("messages").getString("death-messages.suffocation")
                            .replaceAll("<victim_format>", name));
                    break;
                case SUICIDE:
                    input = (PotSG.getInstance().getConfiguration("messages").getString("death-messages.suicide")
                            .replaceAll("<victim_format>", name));
                    break;
                case THORNS:
                    input = (PotSG.getInstance().getConfiguration("messages").getString("death-messages.thorns")
                            .replaceAll("<victim_format>", name));
                    break;
                case VOID:
                    if (killer != null) {
                        input = (PotSG.getInstance().getConfiguration("messages").getString("death-messages.void-contains-killer")
                                .replaceAll("<victim_format>", name)
                                .replaceAll("<killer_format>", killerName));
                    } else {
                        input = (PotSG.getInstance().getConfiguration("messages").getString("death-messages.void").replaceAll("<victim_format>", name));
                    }
                    break;
                case WITHER:
                    input = (PotSG.getInstance().getConfiguration("messages").getString("death-messages.wither")
                            .replaceAll("<victim_format>", name));
                    break;
                default:
                    input = (PotSG.getInstance().getConfiguration("messages").getString("death-messages.default")
                            .replaceAll("<victim_format>", name));
                    break;
            }
        } else {
            input = (PotSG.getInstance().getConfiguration("messages").getString("death-messages.default")
                    .replaceAll("<victim_format>", name));
        }

        return input;
    }

    public String getEntityName(Entity entity) {
        Preconditions.checkNotNull(entity, "Entity cannot be null");
        return (entity instanceof Player) ? ((Player) entity).getName() : ((CraftEntity) entity).getHandle().getName();
    }
}
