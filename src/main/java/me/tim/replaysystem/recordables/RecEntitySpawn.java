package me.tim.replaysystem.recordables;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lombok.*;

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
    public int getId() {
        return 0;
    }
}
