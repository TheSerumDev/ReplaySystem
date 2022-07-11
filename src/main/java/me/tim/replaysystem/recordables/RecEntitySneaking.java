package me.tim.replaysystem.recordables;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lombok.*;

@RequiredArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class RecEntitySneaking implements EntityState {

    @NonNull
    private int entityId;

    @NonNull
    private boolean isSneaking;

    @Override
    public void write(DataOutputStream buffer) throws IOException {
        buffer.writeInt(this.entityId);
        buffer.writeBoolean(this.isSneaking);
    }

    @Override
    public void read(DataInputStream buffer) throws IOException {
        this.entityId = buffer.readInt();
        this.isSneaking = buffer.readBoolean();
    }

    @Override
    public int getId() {
        return 11;
    }
}
