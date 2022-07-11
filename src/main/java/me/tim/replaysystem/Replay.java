package me.tim.replaysystem;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.Getter;
import lombok.Setter;
import me.tim.replaysystem.recordables.EntityState;
import me.tim.replaysystem.recordables.RecEntityMove;
import me.tim.replaysystem.recordables.RecEntitySneaking;
import me.tim.replaysystem.recordables.RecEntitySpawn;
import me.tim.replaysystem.recordables.RecordableHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

@Getter
public final class Replay {

    private static final long HEAP_WIPER_TIMEOUT = 10_000;

    private final String world;
    private final String id;

    @Setter
    private int tick;
    private final Map<Integer, List<EntityState>> data;

    private final long start;

    public Replay(String world, String id) {
        this.world = world;
        this.id = id;
        this.data = new ConcurrentHashMap<>();
        this.start = System.currentTimeMillis();
    }

    public void addEntityState(int tick, EntityState entityState) {
        this.data.computeIfAbsent(tick, x -> new CopyOnWriteArrayList<>());
        this.data.get(tick).add(entityState);
    }

    public void addEntityState(EntityState entityState) {
        addEntityState(this.tick, entityState);
    }

    public void nextTick() {
        this.tick++;
        this.data.put(tick, new CopyOnWriteArrayList<>());
    }

    public void saveTick(int tick, DataOutputStream dos) throws IOException {
        List<EntityState> records = this.data.get(tick);

        for (EntityState entityState : records) {
            System.out.println("WRITE: " + entityState);
            dos.writeUTF(entityState.getId() + ":");
            entityState.write(dos);
        }
    }

    public void onStart() {
        this.data.put(0, new CopyOnWriteArrayList<>());
        nextTick();

        for (Player player : Bukkit.getOnlinePlayers()) {
            RecordableHandler.spawn(this, player);
        }

        for (Entity entity : Bukkit.getWorld(this.world).getEntities()) {
            RecordableHandler.trackEntity(this, entity);
        }

        Bukkit.broadcastMessage("Replay " + this.id + " started! watching world \"" + this.world + "\"");
    }

    public void onTick() {
        for (Entry<Entity, LocationWrapper> entry : RecordableHandler.getEntityLocation().entrySet()) {
            Entity entity = entry.getKey();

            if (!entity.isValid()) {
                // remove entity
            }

            LocationWrapper to = new LocationWrapper(entity.getLocation());
            LocationWrapper from = entry.getValue();

            if (!to.compare(from)) {
                RecordableHandler.setEntityLocation(entity, to);
                RecordableHandler.moveEntity(this, entity.getEntityId(), entity.getLocation());
            }
        }
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
