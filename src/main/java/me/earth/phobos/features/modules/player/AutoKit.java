package me.earth.phobos.features.modules.player;

import me.earth.phobos.event.events.DeathEvent;
import me.earth.phobos.features.setting.Bind;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.features.modules.Module;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

public class AutoKit extends Module {
    public AutoKit() {
        super("AutoKit", "Automatically does /kit <name>", Module.Category.PLAYER, true, false, false);
        this.setInstance();
    }
    public Setting<String> primaryKit = this.register(new Setting("Primary Kit", "ffa"));
    public Setting<String> secondaryKit = this.register(new Setting("Secondary Kit", "duel"));
    public Setting<Bind> swapBind = this.register(new Setting("SwapBind", new Bind(-1)));
    private boolean toggle = false;
    private boolean runThisLife = false;
    public static AutoKit INSTANCE;

    public static AutoKit getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AutoKit();
        }
        return INSTANCE;
    }

    public void onUpdate() {
        if (mc.player.isEntityAlive() && !this.runThisLife) {
            mc.player.connection.sendPacket(new CPacketChatMessage("/kit " + (this.toggle ? (String)this.secondaryKit.getValue() : (String)this.primaryKit.getValue())));
            this.runThisLife = true;
        }
    }

    @SubscribeEvent(
            priority = EventPriority.NORMAL,
            receiveCanceled = true
    )
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (Keyboard.getEventKeyState() && ((Bind)this.swapBind.getValue()).getKey() == Keyboard.getEventKey()) {
            this.toggle = !this.toggle;
        }
    }

    @SubscribeEvent
    public void onEntityDeath(DeathEvent event) {
        if (event.player == mc.player) {
            this.runThisLife = false;
        }
    }

    private void setInstance() {
        INSTANCE = this;
    }
}
