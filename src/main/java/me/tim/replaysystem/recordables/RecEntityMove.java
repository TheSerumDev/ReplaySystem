package me.tim.replaysystem.recordables;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lombok.*;

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
    public int getId() {
        return 10;
    }
}
