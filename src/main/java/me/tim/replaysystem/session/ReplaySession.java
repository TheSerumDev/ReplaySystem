package me.tim.replaysystem.session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import lombok.Getter;
import lombok.Setter;
import me.tim.replaysystem.PlayerUtil;
import me.tim.replaysystem.Replay;
import me.tim.replaysystem.ReplaySystemPlugin;
import me.tim.replaysystem.ReplayUtil;
import me.tim.replaysystem.recordables.EntityState;
import me.tim.replaysystem.recordables.RecEntityMove;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public final class ReplaySession {

    private final List<Player> watchers;
    private final Map<Integer, EntityPlayer> entities;

    private final Replay replay;

    @Getter
    private final World world;

    private long lastHearbeat;

    @Setter
    @Getter
    private boolean forward;

    @Getter
    private int ticks;

    private boolean paused;
    private double speed;

    public ReplaySession(Replay replay) {
        this.replay = replay;
        this.watchers = new ArrayList<>();
        this.entities = new HashMap<>();

        this.speed = 1;

        this.lastHearbeat = System.currentTimeMillis();

        this.world = WorldCreator.name(this.replay.getId())
            .environment(Environment.NORMAL)
            .type(WorldType.FLAT)
            .generatorSettings("2;0;1;")
            .createWorld();
    }

    public void join(Player player) {
        addWatcher(player);
        player.setGameMode(GameMode.CREATIVE);

        PlayerInventory inventory = player.getInventory();
        inventory.clear();

        inventory.setItem(8, new ItemStack(Material.SLIME_BALL));

        RecEntityMove rec = null;
        for (Entry<Integer, List<EntityState>> entry : this.replay.getData().entrySet()) {
            for (EntityState entityState : entry.getValue()) {
                if (entityState instanceof RecEntityMove) {
                    rec = (RecEntityMove) entityState;
                    break;
                }
            }
        }

        if (rec != null) {
            player.teleport(rec.getLocation(getWorld()));
        } else {
            player.teleport(getWorld().getSpawnLocation());
        }

        player.sendMessage(replay.getTick() + " max len");
    }

    public void leave(Player player) {
        PlayerInventory inventory = player.getInventory();
        inventory.clear();

        player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
        player.setGameMode(GameMode.SURVIVAL);
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);

        player.setLevel(0);
        player.setExp(0);
        player.setFoodLevel(20);
        player.setHealth(20);
    }

    public void cleanup() {
        for (Player watcher : this.watchers) {
            leave(watcher);
        }

        ReplaySessionHolder.removeSession(this);
    }

    public void addWatcher(Player player) {
        if (this.watchers.contains(player)) {
            return;
        }

        this.watchers.add(player);
    }

    public void removeWatcher(Player player) {
        if (!this.watchers.contains(player)) {
            return;
        }

        this.watchers.remove(player);
    }

    public EntityPlayer getEntity(int id) {
        return this.entities.get(id);
    }

    public Map<Integer, EntityPlayer> getEntities() {
        return Collections.unmodifiableMap(this.entities);
    }

    public void addEntity(int entityId, EntityPlayer entity) {
        if (isEntity(entityId)) {
            return;
        }

        this.entities.put(entityId, entity);
    }

    public void removeEntity(int entityId) {
        if (!isEntity(entityId)) {
            return;
        }

        this.entities.remove(entityId);
    }

    public boolean isEntity(int id) {
        return this.entities.containsKey(id);
    }

    public void revert() {

    }

    public void forward() {

    }

    public void onTick() {
        if (this.paused) {
            return;
        }

        this.ticks += this.speed;

        int seconds = this.ticks / 20;
        int mins = seconds / 60;

        String sec = ("" + seconds).length() == 1 ? "0" + seconds : "" + seconds;
        String min = ("" + mins).length() == 1 ? "0" + mins : "" + mins;

        for (Player watcher : this.watchers) {
            PlayerUtil.sendActionBar(watcher, "§7Aufnahme läuft §8| §7" + min + ":" + sec);
        }

        if (this.ticks == this.replay.getTick()) {
            msg("Replay finished.");
            for (Player watcher : this.watchers) {
                PlayerUtil.sendActionBar(watcher, "§7Aufnahme vorbei");
            }
            cleanup();
            return;
        }

        if (this.ticks % 1 != 0) {
            return;
        }

        for (Entry<Integer, List<EntityState>> entry : this.replay.getData().entrySet()) {
            for (EntityState entityState : entry.getValue()) {
                ReplayUtil.sync(ReplaySystemPlugin.getPlugin(ReplaySystemPlugin.class), () -> {
                    entityState.play(this);
                });
            }
        }
    }

    public void packet(Packet<?> packet) {
        for (Player watcher : this.watchers) {
            ((CraftPlayer) watcher).getHandle().playerConnection.sendPacket(packet);
        }
    }

    public void msg(String msg) {
        for (Player watcher : getWatchers()) {
            watcher.sendMessage(msg);
        }
    }

    public List<Player> getWatchers() {
        return Collections.unmodifiableList(this.watchers);
    }

    public Player getWatcher(Player player) {
        for (Player watcher : this.watchers) {
            if (player == watcher) {
                return watcher;
            }
        }
        return null;
    }
}
