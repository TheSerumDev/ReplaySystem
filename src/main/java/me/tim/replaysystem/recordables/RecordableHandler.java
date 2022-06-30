package me.tim.replaysystem.recordables;

import com.mojang.authlib.properties.Property;
import lombok.experimental.UtilityClass;
import me.tim.replaysystem.Replay;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

@UtilityClass
public final class RecordableHandler {

    public void add(Replay replay, EntityState entityState) {
        if (replay == null) {
            return;
        }

        entityState.add(replay);
    }

    public void spawn(Replay replay, Player player) {
        CraftPlayer craftPlayer = (CraftPlayer) player;

        Property prop = null;
        for (Property property : craftPlayer.getProfile().getProperties().get("textures")) {
            prop = property;
        }

        add(replay, new RecEntitySpawn(player.getEntityId(), player.getName(), prop == null ? "" : prop.getSignature(), prop == null ? "" :prop.getValue()));
    }

    public void move(Replay replay, Player player, Location loc) {
        add(replay, new RecEntityMove(player.getEntityId(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch()));
    }

    public void sneak(Replay replay, Player player, boolean isSneaking) {
        add(replay, new RecEntitySneaking(player.getEntityId(), isSneaking));
    }
}
