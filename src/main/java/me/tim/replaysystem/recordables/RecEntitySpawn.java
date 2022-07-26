package me.tim.replaysystem.recordables;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import me.tim.replaysystem.session.ReplaySession;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_8_R3.PlayerInteractManager;
import net.minecraft.server.v1_8_R3.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

@RequiredArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class RecEntitySpawn implements EntityState {

    @NonNull
    private int entityId;

    @NonNull
    private String name;

    @NonNull
    private String signature;

    @NonNull
    private String texture;

    @Override
    public void write(DataOutputStream buffer) throws IOException {
        buffer.writeInt(this.entityId);
        buffer.writeUTF(this.name);
        buffer.writeUTF(this.signature);
        buffer.writeUTF(this.texture);
    }

    @Override
    public void read(DataInputStream buffer) throws IOException {
        this.entityId = buffer.readInt();
        this.name = buffer.readUTF();
        this.signature = buffer.readUTF();
        this.texture = buffer.readUTF();
    }

    @Override
    public void play(ReplaySession session) {
        if (session.isPlayer(this.entityId)) {
            return;
        }

        Location loc = new Location(session.getWorld(), 0, 0, 0);
        MinecraftServer nmsMc = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer worldServer = ((CraftWorld) loc.getWorld()).getHandle();

        GameProfile profile = new GameProfile(UUID.randomUUID(), this.name);

        EntityPlayer entityPlayer = new EntityPlayer(
            nmsMc,
            worldServer,
            profile,
            new PlayerInteractManager(worldServer)
        );

        profile.getProperties().clear();
        profile.getProperties().put("textures", new Property("textures", this.texture, this.signature));

        entityPlayer.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
        session.addEntity(this.entityId, entityPlayer);

        session.packet(new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, entityPlayer));
        session.packet(new PacketPlayOutNamedEntitySpawn(entityPlayer));
    }

    @Override
    public int getId() {
        return 0;
    }
}
