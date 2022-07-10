package me.tim.replaysystem;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.tim.replaysystem.dispatcher.EventDispatcher;
import me.tim.replaysystem.dispatcher.TickDispatcher;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
@Getter
public final class ReplayManager {

    private static final ExecutorService POOL = Executors.newCachedThreadPool();

    private final ReplaySystemPlugin plugin;

    private boolean recording;

    private Replay replay;

    public void onEnable() {
        getPlugin().getCommand("replay").setExecutor(new ReplayCommand(this));
        new TickDispatcher(this).runTaskTimer(this.plugin, 1, 1);

        Bukkit.getPluginManager().registerEvents(new EventDispatcher(this), this.plugin);
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

        try (FileOutputStream fos = new FileOutputStream(getFile(this.replay.getId()));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos)
        ) {
            for (int i = 0; i < this.replay.getTick(); i++) {
                this.replay.saveTick(i, dos);
            }

            fos.write(baos.toByteArray());
            fos.flush();

            this.replay.onStop();
        } catch (Exception ex) {
        }

        this.replay = null;

        this.recording = false;
    }

    public void loadReplay(String id, Consumer<Replay> consumer) {
        POOL.execute(new Acceptor<Replay>(consumer) {
            @Override
            public Replay getValue() {
                File file = getFile(id);

                try (FileInputStream fis = new FileInputStream(file)) {
                    DataInputStream dis = new DataInputStream(fis);
                    ByteBuffer buffer = ByteBuffer.allocate(dis.available());

                    
                } catch (Exception ex) {
                    broadcast("Error whilst loading replay: " + id + " err.");
                    ex.printStackTrace();
                }
                return null;
            }
        });
    }

    public File getFile(String id) {
        return new File("plugins/" + id + ".replay");
    }

    public void msg(Player player, String msg) {
        player.sendMessage("§8[§6Replay§8] §7" + msg);
    }

    public void broadcast(String msg) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            msg(player, msg);
        }
    }
}
