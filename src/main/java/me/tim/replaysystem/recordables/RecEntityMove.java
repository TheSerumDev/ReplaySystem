package me.tim.replaysystem.recordables;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import me.tim.replaysystem.session.ReplaySession;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntity.PacketPlayOutEntityLook;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityHeadRotation;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityTeleport;
import org.bukkit.Location;
import org.bukkit.World;

@RequiredArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class RecEntityMove implements EntityState {

    @NonNull
    private int entityId;

    @NonNull
    private double x;

    @NonNull
    private double y;

    @NonNull
    private double z;

    @NonNull
    private float yaw;

    @NonNull
    private float pitch;

    @Override
    public void write(DataOutputStream buffer) throws IOException {
        buffer.writeInt(this.entityId);
        buffer.writeDouble(this.x);
        buffer.writeDouble(this.y);
        buffer.writeDouble(this.z);
        buffer.writeFloat(this.yaw);
        buffer.writeFloat(this.pitch);
    }

    @Override
    public void read(DataInputStream buffer) throws IOException {
        this.entityId = buffer.readInt();
        this.x = buffer.readDouble();
        this.y = buffer.readDouble();
        this.z = buffer.readDouble();
        this.yaw = buffer.readFloat();
        this.pitch = buffer.readFloat();
    }

    @Override
    public void play(ReplaySession session) {
        EntityPlayer entity = session.getEntity(this.entityId);

        if (entity == null) {
            return;
        }

        if (!session.isForward()) {
            entity.locX = this.x;
            entity.locY = this.y;
            entity.locZ = this.z;
            entity.yaw = this.yaw;
            entity.pitch = this.pitch;

            session.packet(new PacketPlayOutEntityTeleport(entity));
            session.packet(new PacketPlayOutEntityLook(entity.getId(), (byte) yaw, (byte) pitch, true));
            session.packet(new PacketPlayOutEntityHeadRotation(entity, (byte) yaw));
        }
    }

    @Override
    public int getId() {
        return 10;
    }

    public Location getLocation(World world) {
        return new Location(world, x, y, z, yaw, pitch);
    }
}
