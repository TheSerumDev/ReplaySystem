package me.tim.replaysystem.session;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;

@UtilityClass
public final class ReplaySessionHolder {

    private static final List<ReplaySession> SESSIONS = new CopyOnWriteArrayList<>();

    public void addSession(ReplaySession session) {
        if (SESSIONS.contains(session)) {
            return;
        }

        SESSIONS.add(session);
    }

    public void removeSession(ReplaySession session) {
        if (!SESSIONS.contains(session)) {
            return;
        }

        SESSIONS.remove(session);
    }

    public ReplaySession getSessionByWatcher(Player player) {
        for (ReplaySession session : SESSIONS) {
            for (Player watcher : session.getWatchers()) {
                if (player == watcher) {
                    return session;
                }
            }
        }
        return null;
    }

    public static List<ReplaySession> getSessions() {
        return Collections.unmodifiableList(SESSIONS);
    }
}
