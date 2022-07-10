package me.tim.replaysystem;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
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

    private static final long HEAP_WIPER_TIMEOUT = 10_000;

    private final String world;
    private final String id;

    private int tick;
    private final Map<Integer, List<EntityState>> data;

    public Replay(String world, String id) {
        this.world = world;
        this.id = id;
        this.data = new ConcurrentHashMap<>();

    }

    public void addEntityState(EntityState entityState) {
        this.data.computeIfAbsent(this.tick, x -> new CopyOnWriteArrayList<>());
        this.data.get(this.tick).add(entityState);
    }

    public void newTick() {
        this.tick++;
        this.data.put(tick, new CopyOnWriteArrayList<>());
    }

    public void saveTick(int tick, DataOutputStream dos) throws IOException  {
        List<EntityState> records = this.data.get(tick);

        for (EntityState entityState : records) {
            dos.writeUTF(entityState.getId() + ":");
            entityState.write(dos);
        }
    }

    public void onStart() {
        this.data.put(0, new CopyOnWriteArrayList<>());
        newTick();

        for (Player player : Bukkit.getOnlinePlayers()) {
            RecordableHandler.spawn(this, player);
            RecordableHandler.move(this, player.getEntityId(), player.getLocation());
        }

        Bukkit.broadcastMessage("Replay " + this.id + " started! watching world \"" + this.world + "\"");
    }

    public void onTick() {
    }

    public void onStop() {
        for (int i = 0; i < 100; i++) {
            Bukkit.broadcastMessage("");
        }

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

        this.data.clear();

        Bukkit.broadcastMessage("Replay stopped! " + this.id);
    }
}
