package me.tim.replaysystem.recordables;

import java.lang.reflect.InvocationTargetException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum EntityStateRegistry {

    SPAWN(RecEntitySpawn.class),
    DESTROY(RecEntityDestroy.class),
    MOVE(RecEntityMove.class),
    SNEAK(RecEntitySneaking.class);
    ;

    private final Class<? extends EntityState> entityClass;

    public static EntityState createEmptyStateById(int id) {
        for (EntityStateRegistry value : values()) {
            try {
                EntityState entityState = value.entityClass.getConstructor().newInstance();
                if (entityState.getId() == id) {
                    return entityState;
                }
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException ex) {
                throw new RuntimeException(ex);
            }
        }

        throw new IllegalArgumentException("Id could not be mapped to entity state");
    }
}
