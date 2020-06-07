
package club.frozed.frozedsg.utils.tasks;

import club.frozed.frozedsg.PotSG;
import club.frozed.frozedsg.utils.chat.Color;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.BlockFace;

import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class SplitedRoadProcessor extends BukkitRunnable {
    private Location center;
    private Processor processor;
    private JavaPlugin plugin;
    private int delay;

    public SplitedRoadProcessor(final JavaPlugin plugin, final Location center, final int maxPerTick, final int delay) {
        this.center = center;
        this.processor = new Processor(center, maxPerTick);
        this.delay = delay;
        this.plugin = plugin;
    }

    private SplitedRoadProcessor(final JavaPlugin plugin, final Location center, final int delay, final Processor procesor) {
        this.center = center;
        this.processor = procesor;
        this.delay = delay;
        this.plugin = plugin;
    }

    public void run() {
        if (this.processor.run()) {
            return;
        }
        new SplitedRoadProcessor(this.plugin, this.center, this.delay, this.processor).runTaskLater(this.plugin, (long) this.delay);
    }

    public enum Type {
        X("X", 0),
        Z("Z", 1);

        Type(final String s, final int n) {

        }
    }

    public static class Processor {
        private World world;
        private Location center;
        private int phase;
        private int processedBlockThisTick;
        private int processingZ;
        private int processingX;
        private int maxPerTick;
        private int length;
        private int y;

        public Processor(final Location center, final int maxPerTick) {
            this.phase = 0;
            this.length = 600;
            this.y = 63;
            this.world = center.getWorld();
            this.center = center;
            this.maxPerTick = maxPerTick;
        }

        public boolean run() {
            final Chunk centerChunk = this.center.getChunk();
            final Location centerLocation = new Location(centerChunk.getWorld(), 8.0, (double) this.y, 8.0);
            this.processedBlockThisTick = 0;
            if (this.phase == 0) {
                this.processingZ = -1 * this.length;
                this.processingX = 0;
                this.phase = 1;
                Bukkit.getConsoleSender().sendMessage(club.frozed.frozedsg.utils.chat.Color.translate("&b[FrozedSG] &aPhase 1 of road process has begun."));
            }
            if (this.phase == 1) {
                final Chunk chunk = centerChunk;
                for (int x = 0; x < 16; ++x) {
                    final Block block1 = this.world.getBlockAt(x, chunk.getX() + 1, chunk.getZ() - 1);
                    final Block block2 = this.world.getBlockAt(x, chunk.getX() + 1, chunk.getZ() + 16);
                    if (!block1.getChunk().isLoaded()) {
                        block1.getChunk().load(true);
                    }
                    if (!block2.getChunk().isLoaded()) {
                        block2.getChunk().load(true);
                    }
                }
                for (int z = 0; z < 16; ++z) {
                    final Block block1 = this.world.getBlockAt(chunk.getX() - 1, 1, chunk.getZ() + z);
                    final Block block2 = this.world.getBlockAt(chunk.getX() + 16, 1, chunk.getZ() + z);
                    if (!block1.getChunk().isLoaded()) {
                        block1.getChunk().load(true);
                    }
                    if (!block2.getChunk().isLoaded()) {
                        block2.getChunk().load(true);
                    }
                }
                for (int x = -32; x < 32; ++x) {
                    for (int z2 = -32; z2 < 32; ++z2) {
                        ++this.processedBlockThisTick;
                        final Location newlocation = new Location(centerChunk.getWorld(), (double) (8 + x), (double) this.y, (double) (8 + z2));
                        final int diff = (int) (newlocation.distance(centerLocation) - 24.0);
                        if (diff < 0) {
                            this.clearAbove(chunk.getWorld(), x + 8, this.y, z2 + 8);
                            this.setRoad(chunk.getWorld().getBlockAt(x + 8, this.y, z2 + 8));
                        } else {
                            this.clearAbove(chunk.getWorld(), x + 8, this.y + diff, z2 + 8);
                        }
                    }
                }
                this.phase = 2;
                Bukkit.getConsoleSender().sendMessage(club.frozed.frozedsg.utils.chat.Color.translate("&b[FrozedSG] &aPhase 2 of road process has begun."));
            }
            if (this.phase == 2) {
                while (this.processingZ < this.length) {
                    final Chunk chunk = this.world.getChunkAt(new Location(this.world, this.center.getX(), 100.0, this.processingZ + this.center.getZ()));
                    if (chunk.getZ() != centerChunk.getZ()) {
                        this.fillRow(this.y, Type.Z, this.processingZ);
                        if (this.processedBlockThisTick > this.maxPerTick) {
                            return false;
                        }
                    }
                    for (final Entity entity : chunk.getWorld().getEntities()) {
                        if (entity instanceof Item) {
                            entity.remove();
                        }
                    }
                    ++this.processingZ;
                }
                this.phase = 3;
                this.processingZ = 0;
                this.processingX = -1 * this.length;
                Bukkit.getConsoleSender().sendMessage(club.frozed.frozedsg.utils.chat.Color.translate("&b[FrozedSG] &aPhase 3 of road process has begun."));
            }
            if (this.phase == 3) {
                while (this.processingX < this.length) {
                    final Chunk chunk = this.world.getChunkAt(new Location(this.world, this.center.getX() + this.processingX, 100.0, this.center.getZ()));
                    if (chunk.getX() != centerChunk.getX()) {
                        this.fillRow(this.y, Type.X, this.processingX);
                        if (this.processedBlockThisTick > this.maxPerTick) {
                            return false;
                        }
                    }
                    for (final Entity entity : chunk.getWorld().getEntities()) {
                        if (entity instanceof Item) {
                            entity.remove();
                        }
                    }
                    ++this.processingX;
                }
                this.phase = 4;
                Bukkit.getConsoleSender().sendMessage(Color.translate("&b[FrozedSG] &aRoad process has been finished. &bPLAYERS ARE NOW ABLE TO JOIN THE SERVER."));
                PotSG.getInstance().setPluginLoading(false);
            }
            return true;
        }

        public void fillRow(final int y, final Type modifierType, final int modifier) {
            if (modifierType == Type.X) {
                for (int z = 0; z < 16; ++z) {
                    ++this.processedBlockThisTick;
                    this.clearAbove(this.world, modifier + this.center.getBlockX(), y, this.center.getBlockZ() + z);
                    this.setRoad(this.world.getBlockAt(modifier + this.center.getBlockX(), y, this.center.getBlockZ() + z));
                }
                for (int i = 1; i < 6; ++i) {
                    final Block one = this.world.getBlockAt(modifier + this.center.getBlockX(), y + i - 1, this.center.getBlockZ() + 15 + i);
                    final Block two = this.world.getBlockAt(modifier + this.center.getBlockX(), y + i - 1, this.center.getBlockZ() - i);
                    if (!one.getRelative(BlockFace.UP).isEmpty() && one.getRelative(BlockFace.UP).getType().isOccluding()) {
                        one.setType(this.getBiomeMaterial(one.getBiome()));
                    }
                    if (!two.getRelative(BlockFace.UP).isEmpty() && two.getRelative(BlockFace.UP).getType().isOccluding()) {
                        two.setType(this.getBiomeMaterial(two.getBiome()));
                    }
                    this.clearAbove(this.world, modifier + this.center.getBlockX(), y + i - 1, this.center.getBlockZ() + 15 + i);
                    this.clearAbove(this.world, modifier + this.center.getBlockX(), y + i - 1, this.center.getBlockZ() - i);
                }
            } else {
                for (int x = 0; x < 16; ++x) {
                    ++this.processedBlockThisTick;
                    this.clearAbove(this.world, this.center.getBlockX() + x, y, modifier + this.center.getBlockZ());
                    this.setRoad(this.world.getBlockAt(this.center.getBlockX() + x, y, modifier + this.center.getBlockZ()));
                }
                for (int i = 1; i < 6; ++i) {
                    final Block one = this.world.getBlockAt(this.center.getBlockX() + 15 + i, y + i - 1, modifier + this.center.getBlockZ());
                    final Block two = this.world.getBlockAt(this.center.getBlockX() - i, y + i - 1, modifier + this.center.getBlockZ());
                    if (!one.getRelative(BlockFace.UP).isEmpty() && one.getRelative(BlockFace.UP).getType().isOccluding()) {
                        one.setType(this.getBiomeMaterial(one.getBiome()));
                    }
                    if (!two.getRelative(BlockFace.UP).isEmpty() && two.getRelative(BlockFace.UP).getType().isOccluding()) {
                        two.setType(this.getBiomeMaterial(two.getBiome()));
                    }
                    this.clearAbove(this.world, this.center.getBlockX() + 15 + i, y + i - 1, modifier + this.center.getBlockZ());
                    this.clearAbove(this.world, this.center.getBlockX() - i, y + i - 1, modifier + this.center.getBlockZ());
                }
            }
        }

        public void setRoad(Block block) {
            block.setType(this.getRoadMaterial());
            for (int i = 0; i < 10; ++i) {
                (block = block.getRelative(BlockFace.DOWN)).setType(Material.BEDROCK);
            }
        }

        public void clearAbove(final World world, final int bx, final int by, final int bz) {
            for (int y = by + 1; y < 256; ++y) {
                ++this.processedBlockThisTick;
                world.getBlockAt(bx, y, bz).setType(Material.AIR);
            }
        }

        public Material getBiomeMaterial(final Biome biome) {
            switch (biome) {
                case DESERT: {
                    return Material.SAND;
                }
                default: {
                    return Material.GRASS;
                }
            }
        }

        public Material getRoadMaterial() {
            List<String> materials = PotSG.getInstance().getConfiguration("config").getStringList("ROADS.ROAD-MATERIALS");
            final int r = RandomUtils.nextInt(materials.size());
            return Material.valueOf(materials.get(r));
        }
    }
}
