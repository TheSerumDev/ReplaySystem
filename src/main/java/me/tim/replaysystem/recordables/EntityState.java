package me.tim.replaysystem.recordables;

import java.io.DataOutputStream;
import java.io.IOException;

public interface EntityState {
    void write(DataOutputStream buffer) throws IOException;

    int getId();
}
