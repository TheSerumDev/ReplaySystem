package me.tim.replaysystem.recordables;

import java.io.DataOutputStream;
import java.io.IOException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class RecEntitySpawn implements EntityState {

    private final int entityId;
    private final String name;
    private final String signature;
    private final String texture;

    @Override
    public void write(DataOutputStream buffer) throws IOException {
        buffer.writeInt(this.entityId);
        buffer.writeChars(this.name);
        buffer.writeChars(this.signature);
        buffer.writeChars(this.texture);
    }

    @Override
    public int getId() {
        return 0;
    }
}
