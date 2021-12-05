package me.earth.phobos.features.modules.client;

 import me.earth.phobos.features.modules.Module;
 import me.earth.phobos.features.setting.Setting;

 public class QueueSkip
   extends Module {
   private final Setting<Integer> packets = register(new Setting("Packets", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(4)));
   public Setting<Server> server = register(new Setting("Server", Server.NORMAL));
   public Setting<Mode> mode = register(new Setting("Mode", Mode.NORMAL, v -> (this.server.getValue() == Server.NORMAL)));
   public Setting<Float> prioCount = register(new Setting("PrioPlayerCount", Float.valueOf(0.0F), Float.valueOf(0.0F), Float.valueOf(1000.0F), v -> (this.mode.getValue() == Mode.PRIO)));
   public Setting<Float> playerCount = register(new Setting("PlayerCount", Float.valueOf(0.0F), Float.valueOf(0.0F), Float.valueOf(1000.0F), v -> (this.mode.getValue() == Mode.NORMAL)));

   public QueueSkip() {
     super("QueueSkip", "Skips the QUEUE!", Category.CLIENT, true, false, true);
   }

   public void onUpdate() {}

   public enum Mode {
     PRIO,
     NORMAL; }

   public enum Server {
     NORMAL,
     OLDFAG,
     NINEBEE;
   }
}