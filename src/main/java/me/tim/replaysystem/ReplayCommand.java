package me.tim.replaysystem;

import lombok.RequiredArgsConstructor;
import me.tim.replaysystem.session.ReplayPlayer;
import me.tim.replaysystem.session.ReplaySession;
import me.tim.replaysystem.session.ReplaySessionHolder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public final class ReplayCommand implements CommandExecutor {

    private final ReplayManager replayManager;

    @Override
    public boolean onCommand(CommandSender cs, Command command, String label, String[] args) {
        if (!(cs instanceof Player)) {
            return false;
        }

        Player player = (Player) cs;

        if (!player.isOp()) {
            this.replayManager.msg(player, "§cfailed -- permission level to low");
            return false;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("start")) {
            this.replayManager.startRecording(player.getWorld().getName());
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("stop")) {
            this.replayManager.stopRecording();
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("play")) {
            this.replayManager.msg(player, "Lade Replay Datei...");
            this.replayManager.loadReplay(args[1], replay -> {
                if (replay == null) {
                    this.replayManager.msg(player, "§cEs trat ein Fehler auf beim Laden des Replays!");
                    return;
                }

                this.replayManager.msg(player, "Replay Datei geladen! §e" + (System.currentTimeMillis() - replay.getStart()) + "ms");

                ReplayUtil.sync(this.replayManager.getPlugin(), () -> {
                    ReplaySession session = new ReplaySession(replay);
                    session.join(player);
                    ReplaySessionHolder.addSession(session);
                });
            });
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("tp")) {
            ReplaySession session = ReplaySessionHolder.getSessionByWatcher(player);
            if (session == null) {
                return false;
            }

            int id = Integer.parseInt(args[1]);

            if (!session.isPlayer(id)) {
                for (Integer entityId : session.getPlayers().keySet()) {
                    player.sendMessage(entityId + "");
                }
                return false;
            }

            ReplayPlayer replayPlayer = session.getPlayer(id);
            player.teleport(replayPlayer.getLocation(session.getWorld()));
        }

        return true;
    }
}
