package me.earth.phobos.features.modules.render;

import me.earth.phobos.util.RusherHackUtil;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.event.events.Render3DEvent;
import me.earth.phobos.features.modules.Module;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

public class NewChunk
        extends Module {
    public static ArrayList<Chunk> coords = new ArrayList();

    public NewChunk() {
        super("NewChunk", "nw", Category.RENDER, true, false, false);
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        for (Chunk chunk : coords) {
            int x = chunk.x * 16;
            int y = 0;
            int z = chunk.z * 16;
            chunkESP ( x , y , z );
        }

    }

    @SubscribeEvent
    public void eventSPacketChunk(PacketEvent e) {
        if (e.getPacket() instanceof SPacketChunkData && !((SPacketChunkData)e.getPacket()).isFullChunk()) {
            coords.add(NewChunk.mc.world.getChunk(((SPacketChunkData)e.getPacket()).getChunkX(), ((SPacketChunkData)e.getPacket()).getChunkZ()));
        }
    }

    public static void chunkESP(double x, double y, double z) {
        double posX = x - RusherHackUtil.getRenderPosX();
        double posY = y - RusherHackUtil.getRenderPosY();
        double posZ = z - RusherHackUtil.getRenderPosZ();
        GL11.glPushMatrix();
        GL11.glEnable( 2848 );
        GL11.glDisable( 2929 );
        GL11.glDisable( 3553 );
        GL11.glDepthMask( false );
        GL11.glBlendFunc( 770 , 771 );
        GL11.glEnable( 3042 );
        GL11.glLineWidth( 1.0f );
        GL11.glColor3f( 189.0f , 0.0f , 0.0f );
        GL11.glBegin( 2 );
        GL11.glVertex3d( posX , posY , posZ );
        GL11.glVertex3d( posX + 16.0 , posY , posZ );
        GL11.glVertex3d( posX + 16.0 , posY , posZ );
        GL11.glVertex3d( posX , posY , posZ );
        GL11.glEnd();
        GL11.glBegin( 2 );
        GL11.glVertex3d( posX , posY , posZ );
        GL11.glVertex3d( posX , posY , posZ + 16.0 );
        GL11.glEnd();
        GL11.glBegin( 2 );
        GL11.glVertex3d( posX , posY , posZ + 16.0 );
        GL11.glVertex3d( posX + 16.0 , posY , posZ + 16.0 );
        GL11.glVertex3d( posX + 16.0 , posY , posZ + 16.0 );
        GL11.glVertex3d( posX , posY , posZ + 16.0 );
        GL11.glEnd();
        GL11.glBegin( 2 );
        GL11.glVertex3d( posX + 16.0 , posY , posZ + 16.0 );
        GL11.glVertex3d( posX + 16.0 , posY , posZ );
        GL11.glVertex3d( posX + 16.0 , posY , posZ );
        GL11.glVertex3d( posX + 16.0 , posY , posZ + 16.0 );
        GL11.glColor3f( 189.0f , 0.0f , 0.0f );
        GL11.glEnd();
        GL11.glDisable( 3042 );
        GL11.glDepthMask( true );
        GL11.glEnable( 3553 );
        GL11.glEnable( 2929 );
        GL11.glDisable( 2848 );
        GL11.glPopMatrix();
        GL11.glColor4f( 1.0f , 1.0f , 1.0f , 1.0f );
    }
}
