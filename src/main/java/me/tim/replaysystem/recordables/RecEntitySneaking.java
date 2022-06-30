package me.tim.replaysystem.recordables;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class RecEntitySneaking implements EntityState {

    private final int entityId;
    private final boolean isSneaking;
}
