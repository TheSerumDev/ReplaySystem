package me.tim.replaysystem;

import lombok.RequiredArgsConstructor;
import org.bukkit.scheduler.BukkitRunnable;

@RequiredArgsConstructor
public final class ReplayWatcher extends BukkitRunnable {

    private final ReplayManager replayManager;

    @Override
    public void run() {
        Replay replay = this.replayManager.getReplay();

        if (replay != null && this.replayManager.isRecording()) {
            replay.newTick();
        }
    }
}
