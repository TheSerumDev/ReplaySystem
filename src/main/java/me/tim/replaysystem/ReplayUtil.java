package me.tim.replaysystem;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@UtilityClass
public final class ReplayUtil {

    public void sync(JavaPlugin plugin, Runnable onExecute) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, onExecute);
    }
}
