package me.tim.replaysystem.recordables;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import me.tim.replaysystem.session.ReplaySession;

public interface EntityState {

    void write(DataOutputStream buffer) throws IOException;

    void read(DataInputStream buffer) throws IOException;

    default void play(ReplaySession session) {
    }

    int getId();

}
