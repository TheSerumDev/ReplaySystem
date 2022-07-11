package me.tim.replaysystem;

import lombok.Getter;
import org.bukkit.Location;

@Getter
public final class LocationWrapper {

    private final double x;
    private final double y;
    private final double z;
    private final float yaw;
    private final float pitch;

    public LocationWrapper(double x, double y, double z, float yaw, float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public LocationWrapper(Location l) {
        this.x = l.getX();
        this.y = l.getY();
        this.z = l.getZ();
        this.yaw = l.getYaw();
        this.pitch = l.getPitch();
    }

    public boolean compare(LocationWrapper lw) {
        return lw.x == this.x && lw.y == this.y && lw.z == this.z && lw.yaw == this.yaw && lw.pitch == this.pitch;
    }

    public LocationWrapper subtract(LocationWrapper from) {
        return new LocationWrapper(x - from.x, y - from.y, z - from.z, yaw, pitch);
    }
}
