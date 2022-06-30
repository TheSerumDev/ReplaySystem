package me.tim.replaysystem;

import org.bukkit.plugin.java.JavaPlugin;

public final class ReplaySystemPlugin extends JavaPlugin {

    private ReplayManager replayManager;

    @Override
    public void onEnable() {
        this.replayManager = new ReplayManager(this);
        this.replayManager.onEnable();
    }

    @Override
    public void onDisable() {
        if (this.replayManager != null) {
            this.replayManager.onDisable();
        }
    }
}
