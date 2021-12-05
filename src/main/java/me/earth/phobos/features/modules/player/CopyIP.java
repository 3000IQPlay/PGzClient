package me.earth.phobos.features.modules.player;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.manager.RegisterHack;
import net.minecraft.client.multiplayer.ServerData;

import java.awt.*;
import java.awt.datatransfer.StringSelection;

@RegisterHack(name = "CopyIP", description = "Copies the current server IP to clipboard", category = Module.Category.PLAYER)
public class CopyIP extends Module {

    public CopyIP() {
        super ( "CopyIP" , "Copies the current server IP to clipboard" , Module.Category.PLAYER , true , false , false );
    }

    @Override
    public void onEnable() {
        if (mc.getConnection() != null && mc.getCurrentServerData() != null && mc.getCurrentServerData().serverIP != null) {
            final ServerData data = mc.getCurrentServerData();
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(data.serverIP), null);
            Command.sendMessage("Copied IP " + ChatFormatting.GRAY + "[" + ChatFormatting.GREEN + data.serverIP + ChatFormatting.GRAY + "]" + ChatFormatting.RESET + " to clipboard");
        } else {
            Command.sendMessage("Unable to copy server IP.");
        }
        disable();
    }
}