package me.tim.replaysystem.recordables;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class RecEntitySpawn implements EntityState {

    private final int entityId;
    private final String name;
    private final String signature;
    private final String texture;

}
