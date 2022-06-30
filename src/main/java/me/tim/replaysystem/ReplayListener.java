package me.tim.replaysystem;

import me.tim.replaysystem.recordables.RecordableHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public final class ReplayListener implements Listener {

    private final ReplayManager replayManager;

    public ReplayListener(ReplayManager replayManager) {
        this.replayManager = replayManager;
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent evt) {
        Player player = evt.getPlayer();
        RecordableHandler.spawn(this.replayManager.getReplay(), player);
    }

    @EventHandler
    private void onPlayerMove(PlayerMoveEvent evt) {
        Player player = evt.getPlayer();
        RecordableHandler.move(this.replayManager.getReplay(), player, player.getLocation());
    }

    @EventHandler
    private void onPlayerToggleSneak(PlayerToggleSneakEvent evt) {
        Player player = evt.getPlayer();
        RecordableHandler.sneak(this.replayManager.getReplay(), player, evt.isSneaking());
    }
}
