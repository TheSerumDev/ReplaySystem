package me.tim.replaysystem;

import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
@Getter
public final class ReplayManager {

    private final ReplaySystemPlugin plugin;

    private boolean recording;

    private Replay replay;

    public void onEnable() {
        getPlugin().getCommand("replay").setExecutor(new ReplayCommand(this));
        new ReplayWatcher(this).runTaskTimer(this.plugin, 1, 1);

        Bukkit.getPluginManager().registerEvents(new ReplayListener(this), this.plugin);
    }

    public void onDisable() {
        stopRecording();
    }

    public void startRecording(String world) {
        this.recording = true;
        this.replay = new Replay(world, UUID.randomUUID().toString());
        this.replay.onStart();
    }

    public void stopRecording() {
        if (this.replay == null || !this.recording) {
            return;
        }

        this.recording = false;
        this.replay.onStop();

        this.replay = null;
    }

    public void msg(Player player, String msg) {
        player.sendMessage("§8[§6Replay§8] §7" + msg);
    }
}
