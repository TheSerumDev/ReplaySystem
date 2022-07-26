package me.tim.replaysystem.recordables;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import me.tim.replaysystem.session.ReplayPlayer;
import me.tim.replaysystem.session.ReplaySession;

@RequiredArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class RecEntityDestroy implements EntityState {

    @NonNull
    private int entityId;


    @Override
    public void write(DataOutputStream buffer) throws IOException {
        buffer.writeInt(this.entityId);
    }

    @Override
    public void read(DataInputStream buffer) throws IOException {
        this.entityId = buffer.readInt();
    }

    @Override
    public void play(ReplaySession session) {
        if (!session.isPlayer(this.entityId)) {
            return;
        }

        ReplayPlayer player = session.getPlayer(this.entityId);

        session.removeEntity(this.entityId);
        player.destroy(session);
    }

    @Override
    public int getId() {
        return 1;
    }
}
