package me.tim.replaysystem.recordables;

import com.mojang.authlib.properties.Property;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.experimental.UtilityClass;
import me.tim.replaysystem.LocationWrapper;
import me.tim.replaysystem.Replay;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

@UtilityClass
public final class RecordableHandler {

    private static final Map<Entity, LocationWrapper> ENTITY_LOCATION = new HashMap<>();

    public void add(Replay replay, EntityState entityState) {
        if (replay == null) {
            return;
        }

        replay.addEntityState(entityState);
    }

    public void spawn(Replay replay, Player player) {
        CraftPlayer craftPlayer = (CraftPlayer) player;

        Property prop = null;
        for (Property property : craftPlayer.getProfile().getProperties().get("textures")) {
            prop = property;
        }

        add(replay, new RecEntitySpawn(player.getEntityId(), player.getName(), prop == null ? "" : prop.getSignature(), prop == null ? "" : prop.getValue()));
        trackEntity(replay, player);
    }

    public void remove(Replay replay, Player player) {
        add(replay, new RecEntityDestroy(player.getEntityId()));
    }

    public void trackEntity(Replay replay, Entity entity) {
        if (entity instanceof Item) {
            Item item = (Item) entity;
        } else {
        }

        RecordableHandler.moveEntity(replay, entity.getEntityId(), entity.getLocation());
        setEntityLocation(entity, new LocationWrapper(entity.getLocation()));
    }

    public void moveEntity(Replay replay, int entityId, Location loc) {
        if (replay.getTick() % 20 == 0) {
            add(replay, new RecEntityMove(entityId, loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch()));
        }
    }

    public void sneak(Replay replay, int entityId, boolean isSneaking) {
        add(replay, new RecEntitySneaking(entityId, isSneaking));
    }

    public int getBytes(EntityState entityState) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos)) {
            entityState.write(dos);

            return baos.size();
        } catch (Exception ex) {
            return 1;
        }
    }

    public static Map<Entity, LocationWrapper> getEntityLocation() {
        return Collections.unmodifiableMap(ENTITY_LOCATION);
    }

    public static void setEntityLocation(Entity entity, LocationWrapper lw) {
        ENTITY_LOCATION.put(entity, lw);
    }
}
