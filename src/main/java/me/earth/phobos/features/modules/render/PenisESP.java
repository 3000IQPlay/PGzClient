package me.earth.phobos.features.modules.render;

import me.earth.phobos.event.events.Render3DEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Cylinder;
import org.lwjgl.util.glu.Sphere;

public
class PenisESP
        extends Module {
    private final Setting < Integer > spin = this.register ( new Setting <> ( "Spin" , 3 , 0 , 6 ) );
    private final Setting < Integer > cumSize = this.register ( new Setting <> ( "Size" , 3 , 0 , 6 ) );

    public
    PenisESP ( ) {
        super ( "PenisESP" , "Draws a dick." , Module.Category.RENDER , false , false , false );
    }

    @Override
    public
    void onRender3D ( Render3DEvent render3DEvent ) {
        for (Object e : PenisESP.mc.world.loadedEntityList) {
            if ( ! ( e instanceof EntityPlayer ) ) continue;
            RenderManager renderManager = Minecraft.getMinecraft ( ).getRenderManager ( );
            EntityPlayer entityPlayer = (EntityPlayer) e;
            double d = entityPlayer.lastTickPosX + ( entityPlayer.posX - entityPlayer.lastTickPosX ) * (double) PenisESP.mc.timer.renderPartialTicks;
            mc.getRenderManager ( );
            double d2 = d - renderManager.renderPosX;
            double d3 = entityPlayer.lastTickPosY + ( entityPlayer.posY - entityPlayer.lastTickPosY ) * (double) PenisESP.mc.timer.renderPartialTicks;
            mc.getRenderManager ( );
            double d4 = d3 - renderManager.renderPosY;
            double d5 = entityPlayer.lastTickPosZ + ( entityPlayer.posZ - entityPlayer.lastTickPosZ ) * (double) PenisESP.mc.timer.renderPartialTicks;
            mc.getRenderManager ( );
            double d6 = d5 - renderManager.renderPosZ;
            GL11.glPushMatrix ( );
            RenderHelper.disableStandardItemLighting ( );
            this.esp ( entityPlayer , d2 , d4 , d6 );
            RenderHelper.enableStandardItemLighting ( );
            GL11.glPopMatrix ( );
        }
    }

    public
    void esp ( EntityPlayer entityPlayer , double d , double d2 , double d3 ) {
        GL11.glDisable ( 2896 );
        GL11.glDisable ( 3553 );
        GL11.glEnable ( 3042 );
        GL11.glBlendFunc ( 770 , 771 );
        GL11.glDisable ( 2929 );
        GL11.glEnable ( 2848 );
        GL11.glDepthMask ( true );
        GL11.glLineWidth ( 1.0f );
        GL11.glTranslated ( d , d2 , d3 );
        GL11.glRotatef ( - entityPlayer.rotationYaw , 0.0f , entityPlayer.height , 0.0f );
        GL11.glTranslated ( - d , - d2 , - d3 );
        GL11.glTranslated ( d , d2 + (double) ( entityPlayer.height / 2.0f ) - (double) 0.225f , d3 );
        GL11.glColor4f ( 1.38f , 0.55f , 2.38f , 1.0f );
        GL11.glRotated ( ( entityPlayer.isSneaking ( ) ? 35 : 0 ) + this.spin.getValue ( ) , 1.0f + (float) this.spin.getValue ( ) , 0.0 , this.cumSize.getValue ( ) );
        GL11.glTranslated ( 0.0 , 0.0 , 0.075f );
        Cylinder cylinder = new Cylinder ( );
        cylinder.setDrawStyle ( 100013 );
        cylinder.draw ( 0.1f , 0.11f , 0.4f , 25 , 20 );
        GL11.glColor4f ( 1.38f , 0.85f , 1.38f , 1.0f );
        GL11.glTranslated ( 0.0 , 0.0 , - 0.12500000298023223 );
        GL11.glTranslated ( - 0.09000000074505805 , 0.0 , 0.0 );
        Sphere sphere = new Sphere ( );
        sphere.setDrawStyle ( 100013 );
        sphere.draw ( 0.14f , 10 , 20 );
        GL11.glTranslated ( 0.16000000149011612 , 0.0 , 0.0 );
        Sphere sphere2 = new Sphere ( );
        sphere2.setDrawStyle ( 100013 );
        sphere2.draw ( 0.14f , 10 , 20 );
        GL11.glColor4f ( 1.35f , 0.0f , 0.0f , 1.0f );
        GL11.glTranslated ( - 0.07000000074505806 , 0.0 , 0.589999952316284 );
        Sphere sphere3 = new Sphere ( );
        sphere3.setDrawStyle ( 100013 );
        sphere3.draw ( 0.13f , 15 , 20 );
        GL11.glDepthMask ( true );
        GL11.glDisable ( 2848 );
        GL11.glEnable ( 2929 );
        GL11.glDisable ( 3042 );
        GL11.glEnable ( 2896 );
        GL11.glEnable ( 3553 );
    }
}