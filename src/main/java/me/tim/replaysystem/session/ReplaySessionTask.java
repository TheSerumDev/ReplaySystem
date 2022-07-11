package me.tim.replaysystem.session;

import me.tim.replaysystem.ReplayManager;
import org.bukkit.scheduler.BukkitRunnable;

public final class ReplaySessionTask extends BukkitRunnable {

    private final ReplayManager replayManager;

    public ReplaySessionTask(ReplayManager replayManager) {
        this.replayManager = replayManager;
    }

    @Override
    public void run() {
        for (ReplaySession session : ReplaySessionHolder.getSessions()) {
            session.onTick();
        }
    }
}
