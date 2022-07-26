package me.tim.replaysystem.dispatcher;

import me.tim.replaysystem.ReplayManager;
import me.tim.replaysystem.recordables.RecordableHandler;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public final class EventDispatcher implements Listener {

    private final ReplayManager replayManager;

    public EventDispatcher(ReplayManager replayManager) {
        this.replayManager = replayManager;
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent evt) {
        if (!replayManager.isRecording()) {
            return;
        }

        Player player = evt.getPlayer();
        RecordableHandler.spawn(this.replayManager.getCurrentReplay(), player);
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent evt) {
        if (!replayManager.isRecording()) {
            return;
        }

        Player player = evt.getPlayer();
        RecordableHandler.remove(replayManager.getCurrentReplay(), player);
    }

    @EventHandler
    private void onPlayerToggleSneak(PlayerToggleSneakEvent evt) {
        if (!replayManager.isRecording()) {
            return;
        }

        Player player = evt.getPlayer();
        RecordableHandler.sneak(this.replayManager.getCurrentReplay(), player.getEntityId(), evt.isSneaking());
    }

    @EventHandler
    private void onEntitySpawn(EntitySpawnEvent evt) {
        if (!replayManager.isRecording()) {
            return;
        }

        Entity entity = evt.getEntity();
        RecordableHandler.trackEntity(replayManager.getCurrentReplay(), entity);
    }

    @EventHandler
    private void onProjectileLaunch(ProjectileLaunchEvent evt) {
        if (!replayManager.isRecording()) {
            return;
        }

        Entity entity = evt.getEntity();
        RecordableHandler.trackEntity(replayManager.getCurrentReplay(), entity);
    }
}
