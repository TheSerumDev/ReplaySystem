package me.tim.replaysystem.recordables;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public interface EntityState {
    void write(DataOutputStream buffer) throws IOException;

    void read(DataInputStream buffer) throws IOException;

    int getId();

    Class<?>[] STATE_CLASSES = {
            RecEntityMove.class,
            RecEntitySneaking.class,
            RecEntitySpawn.class
    };

    static EntityState createEmptyStateById(int id) {
        for (Class<?> stateClass : STATE_CLASSES) {
            try {
                EntityState entityState = (EntityState) stateClass.getConstructor().newInstance();
                if (entityState.getId() == id) {
                    return entityState;
                }
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

        throw new IllegalArgumentException("Id could not be mapped to entity state");
    }
}
