package me.tim.replaysystem.recordables;

import com.mojang.authlib.properties.Property;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import lombok.experimental.UtilityClass;
import me.tim.replaysystem.Replay;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

@UtilityClass
public final class RecordableHandler {

    public int getBytes(EntityState entityState) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos)) {
            entityState.write(dos);

            return baos.size();
        } catch (Exception ex) {
            return 1;
        }
    }

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
    }

    public void move(Replay replay, int entityId, Location loc) {
        add(replay, new RecEntityMove(entityId, loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch()));
    }

    public void sneak(Replay replay, int entityId, boolean isSneaking) {
        add(replay, new RecEntitySneaking(entityId, isSneaking));
    }
}
