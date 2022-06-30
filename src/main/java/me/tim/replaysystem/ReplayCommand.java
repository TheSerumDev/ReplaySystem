package me.tim.replaysystem;

import lombok.RequiredArgsConstructor;
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

        return true;
    }
}
