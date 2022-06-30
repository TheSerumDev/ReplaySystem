package me.tim.replaysystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import lombok.Getter;
import me.tim.replaysystem.recordables.EntityState;
import me.tim.replaysystem.recordables.RecEntityMove;
import me.tim.replaysystem.recordables.RecEntitySneaking;
import me.tim.replaysystem.recordables.RecEntitySpawn;
import me.tim.replaysystem.recordables.RecordableHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@Getter
public final class Replay {

    private final String world;
    private final String id;

    private int tick;
    private final Map<Integer, List<EntityState>> data;

    public Replay(String world, String id) {
        this.world = world;
        this.id = id;
        this.data = new HashMap<>();
    }

    public void addEntityState(EntityState entityState) {
        this.data.get(this.tick).add(entityState);
    }

    public void newTick() {
        this.tick++;
        this.data.put(tick, new ArrayList<>());

        if (this.tick % 10 == 0) {
            int min = this.tick / 20 / 60;
            int sec = (this.tick - (min * 20 * 60)) / 20;
            String secStr = ("" + sec).length() == 1 ? "0" + sec : "" + sec;

            double bits = 0;
            bits += 798;
            bits /= 1000;

            double cm = 100 - bits + Math.random() - 3.25;
            cm *= 100;
            cm = Math.floor(cm);
            cm /= 100.0;

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!player.isOp()) {
                    continue;
                }

                PlayerUtil.sendActionBar(player, "§cAufnahme " + min + "§7:§c" + secStr + " §8| §7Bitrate: " + bits + " KiB/s §8| §7Cmp: " + cm + "%");
            }
        }

        Bukkit.broadcastMessage("Current tick: " + this.tick);
    }

    public void onStart() {
        this.data.put(0, new ArrayList<>());

        for (Player player : Bukkit.getOnlinePlayers()) {
            RecordableHandler.spawn(this, player);
            RecordableHandler.move(this, player, player.getLocation());
        }

        Bukkit.broadcastMessage("Replay " + this.id + " started! watching world \"" + this.world + "\"");
    }

    public void onStop() {
        for (int i = 0; i < 100; i++) {
            Bukkit.broadcastMessage("");
        }

        Bukkit.broadcastMessage("Replay stopped! " + this.id);

        for (Entry<Integer, List<EntityState>> entry : this.data.entrySet()) {
            Integer savedTick = entry.getKey();
            List<EntityState> records = entry.getValue();

            Bukkit.broadcastMessage(savedTick + " savedTick");

            for (EntityState entityState : records) {
                if (entityState instanceof RecEntitySpawn) {
                    RecEntitySpawn rec = (RecEntitySpawn) entityState;
                    Bukkit.broadcastMessage(rec.getEntityId() + " - " + rec.getName() + " spawned! texture: " + rec.getTexture() + " signature: " + rec.getSignature());
                }

                if (entityState instanceof RecEntityMove) {
                    RecEntityMove rec = (RecEntityMove) entityState;
                    Bukkit.broadcastMessage(rec.getEntityId() + " - moved! X: " + rec.getX() + " Y: " + rec.getY() + " Z: " + rec.getZ() + " Yaw: " + rec.getYaw() + " Pitch: " + rec.getPitch());
                }

                if (entityState instanceof RecEntitySneaking) {
                    RecEntitySneaking rec = (RecEntitySneaking) entityState;
                    Bukkit.broadcastMessage(rec.getEntityId() + " - sneaked! Status: " + rec.isSneaking());
                }
            }
        }
    }
}
