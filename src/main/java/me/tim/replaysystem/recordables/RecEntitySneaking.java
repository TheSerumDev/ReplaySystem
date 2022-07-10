package me.tim.replaysystem.recordables;

import java.io.DataOutputStream;
import java.io.IOException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class RecEntitySneaking implements EntityState {

    private final int entityId;
    private final boolean isSneaking;

    @Override
    public void write(DataOutputStream buffer) throws IOException {
        buffer.writeInt(this.entityId);
        buffer.writeBoolean(this.isSneaking);
    }

    @Override
    public int getId() {
        return 11;
    }
}
