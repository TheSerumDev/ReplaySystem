package me.tim.replaysystem.recordables;

import me.tim.replaysystem.Replay;

public interface EntityState {

    default void add(Replay replay) {
        if (replay == null) {
            return;
        }

        replay.addEntityState(this);
    }
}
