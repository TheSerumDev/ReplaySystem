package me.tim.replaysystem.dispatcher;

import lombok.RequiredArgsConstructor;
import me.tim.replaysystem.Replay;
import me.tim.replaysystem.ReplayManager;
import org.bukkit.scheduler.BukkitRunnable;

@RequiredArgsConstructor
public final class TickDispatcher extends BukkitRunnable {

    private final ReplayManager replayManager;

    private int ticks;

    @Override
    public void run() {
        Replay replay = this.replayManager.getReplay();

        if (this.replayManager.isRecording() && replay != null) {
            this.ticks++;

            replay.newTick();
            replay.onTick();
        }
    }
}
