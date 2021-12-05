package me.earth.phobos.features.modules.player;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.manager.RegisterHack;
import me.earth.phobos.Phobos;
import me.earth.phobos.util.TextUtil;

import java.awt.*;
import java.awt.datatransfer.StringSelection;

@RegisterHack(name = "CopyCoords", description = "ez", category = Module.Category.PLAYER)
public class CopyCoords extends Module {

    public CopyCoords() {
        super ( "CopyCoords" , "ez" , Module.Category.PLAYER , true , false , false );
    }

    @Override
    public void onEnable() {
        if (mc.player != null && mc.world != null) {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(mc.player.getPosition().toString()), null);
            Command.sendMessage("Copied Coords " + ChatFormatting.GRAY + "[" + ChatFormatting.GREEN + "X:" + mc.player.getPosition().getX() + " Y:" + mc.player.getPosition().getY() + " Z:" + mc.player.getPosition().getZ() + ChatFormatting.GRAY + "]" + ChatFormatting.RESET + " to clipboard");
        } else {
            Command.sendMessage("Unable to copy coords?");
        }
        disable();
    }
}