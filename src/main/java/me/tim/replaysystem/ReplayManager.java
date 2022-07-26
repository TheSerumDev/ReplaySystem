package me.tim.replaysystem;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.tim.replaysystem.dispatcher.EventDispatcher;
import me.tim.replaysystem.dispatcher.TickDispatcher;
import me.tim.replaysystem.recordables.EntityState;
import me.tim.replaysystem.recordables.EntityStateRegistry;
import me.tim.replaysystem.session.ReplaySessionTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
@Getter
public final class ReplayManager {

    private static final ExecutorService POOL = Executors.newCachedThreadPool();

    private final ReplaySystemPlugin plugin;

    private boolean recording;

    private Replay currentReplay;

    public void onEnable() {
        getPlugin().getCommand("replay").setExecutor(new ReplayCommand(this));
        new TickDispatcher(this).runTaskTimer(this.plugin, 1, 1);
        new ReplaySessionTask(this).runTaskTimer(this.plugin, 1, 1);

        Bukkit.getPluginManager().registerEvents(new EventDispatcher(this), this.plugin);
    }

    public void onDisable() {
        stopRecording();
    }

    public void startRecording(String world) {
        this.recording = true;
        this.currentReplay = new Replay(world, UUID.randomUUID().toString());
        this.currentReplay.onStart();
    }

    public void stopRecording() {
        if (this.currentReplay == null || !this.recording) {
            return;
        }

        try (FileOutputStream fos = new FileOutputStream(getFile(this.currentReplay.getId()));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos)
        ) {
            dos.writeInt(this.currentReplay.getTick());
            for (int i = 0; i < this.currentReplay.getTick(); i++) {
                this.currentReplay.saveTick(i, dos);
            }

            fos.write(baos.toByteArray());
            fos.flush();

            this.currentReplay.onStop();
        } catch (Exception ex) {
        }

        this.currentReplay = null;

        this.recording = false;
    }

    public void loadReplay(String id, Consumer<Replay> consumer) {
        Replay replay = new Replay("", id);

        POOL.execute(new Acceptor<Replay>(consumer) {
            @Override
            public Replay getValue() {
                File file = getFile(id);

                if (!file.exists()) {
                    return null;
                }

                try (FileInputStream fis = new FileInputStream(file)) {
                    DataInputStream dis = new DataInputStream(fis);
                    replay.setTick(dis.readInt());

                    while (dis.available() > 0) {
                        int entityStateId = Integer.parseInt(dis.readUTF().replace(":", ""));
                        EntityState entityState = EntityStateRegistry.createEmptyStateById(entityStateId);
                        entityState.read(dis);
                        replay.addEntityState(entityState);
                        System.out.println("READ: " + entityState);
                    }
                    return replay;
                } catch (Exception ex) {
                    broadcast("Error whilst loading replay: " + id + " err.");
                    ex.printStackTrace();
                    return null;
                }
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
