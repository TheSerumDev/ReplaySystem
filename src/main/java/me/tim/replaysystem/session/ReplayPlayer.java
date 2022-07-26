package me.tim.replaysystem.session;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import org.bukkit.Location;
import org.bukkit.World;

@RequiredArgsConstructor
@Getter
@Setter
public final class ReplayPlayer {

    private final EntityPlayer entityPlayer;
    private final ReplaySession replaySession;

    private int ping;

    public void move(Location to) {
        teleport(to);
    }

    public Location getLocation(World world) {
        return new Location(world, this.entityPlayer.locX, this.entityPlayer.locY, this.entityPlayer.locZ, this.entityPlayer.yaw, this.entityPlayer.pitch);
    }

    public void teleport(Location location) {
        this.entityPlayer.locX = location.getX();
        this.entityPlayer.locY = location.getY();
        this.entityPlayer.locZ = location.getZ();
        this.entityPlayer.yaw = location.getYaw();
        this.entityPlayer.pitch = location.getPitch();
    }

    public void destroy(ReplaySession session) {
        session.packet(new PacketPlayOutEntityDestroy(this.entityPlayer.getId()));
        session.packet(new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER, this.entityPlayer));
    }
}
