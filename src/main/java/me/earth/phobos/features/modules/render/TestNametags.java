package me.earth.phobos.features.modules.render;

import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.Render3DEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.modules.client.Colors;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.DamageUtil;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.RotationUtil;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public
class TestNametags
        extends Module {
    public static TestNametags INSTANCE;
    private final Setting < Boolean > health = this.register ( new Setting <> ( "Health" , Boolean.TRUE ) );
    private final Setting < Boolean > armor = this.register ( new Setting <> ( "Armor" , Boolean.TRUE ) );
    private final Setting < Boolean > reverseArmor = this.register ( new Setting <> ( "ReverseArmor" , Boolean.FALSE ) );
    private final Setting < Boolean > topEnchant = this.register ( new Setting <> ( "TopEnchant" , Boolean.FALSE ) );
    private final Setting < Boolean > altEnchantNames = this.register ( new Setting <> ( "AltEnchantNames" , Boolean.FALSE ) );
    private final Setting < Float > scaling = this.register ( new Setting <> ( "Size" , 0.3f , 0.1f , 20.0f ) );
    private final Setting < Boolean > invisibles = this.register ( new Setting <> ( "Invisibles" , Boolean.FALSE ) );
    private final Setting < Boolean > ping = this.register ( new Setting <> ( "Ping" , Boolean.TRUE ) );
    private final Setting < Boolean > totemPops = this.register ( new Setting <> ( "TotemPops" , Boolean.TRUE ) );
    private final Setting < Boolean > gamemode = this.register ( new Setting <> ( "Gamemode" , Boolean.FALSE ) );
    private final Setting < Boolean > entityID = this.register ( new Setting <> ( "ID" , Boolean.FALSE ) );
    private final Setting < Boolean > rect = this.register ( new Setting <> ( "Rectangle" , Boolean.TRUE ) );
    private final Setting < Boolean > outline = this.register ( new Setting <> ( "Outline" , Boolean.FALSE , v -> this.rect.getValue ( ) ) );
    private final Setting < Boolean > colorSync = this.register ( new Setting <> ( "Sync" , Boolean.FALSE , v -> this.outline.getValue ( ) ) );
    private final Setting < Integer > redSetting = this.register ( new Setting <> ( "Red" , 255 , 0 , 255 , v -> this.outline.getValue ( ) ) );
    private final Setting < Integer > greenSetting = this.register ( new Setting <> ( "Green" , 255 , 0 , 255 , v -> this.outline.getValue ( ) ) );
    private final Setting < Integer > blueSetting = this.register ( new Setting <> ( "Blue" , 255 , 0 , 255 , v -> this.outline.getValue ( ) ) );
    private final Setting < Integer > alphaSetting = this.register ( new Setting <> ( "Alpha" , 255 , 0 , 255 , v -> this.outline.getValue ( ) ) );
    private final Setting < Float > lineWidth = this.register ( new Setting <> ( "LineWidth" , 1.5f , 0.1f , 5.0f , v -> this.outline.getValue ( ) ) );
    private final Setting < Boolean > sneak = this.register ( new Setting <> ( "SneakColor" , Boolean.FALSE ) );
    private final Setting < Boolean > heldStackName = this.register ( new Setting <> ( "StackName" , Boolean.FALSE ) );
    private final Setting < Boolean > whiter = this.register ( new Setting <> ( "White" , Boolean.FALSE ) );
    private final Setting < Boolean > onlyFov = this.register ( new Setting <> ( "OnlyFov" , Boolean.FALSE ) );
    private final Setting < Boolean > scaleing = this.register ( new Setting <> ( "Scale" , Boolean.FALSE ) );
    private final Setting < Float > factor = this.register ( new Setting <> ( "Factor" , 0.3f , 0.1f , 1.0f , v -> this.scaleing.getValue ( ) ) );
    private final Setting < Boolean > smartScale = this.register ( new Setting <> ( "SmartScale" , Boolean.FALSE , v -> this.scaleing.getValue ( ) ) );

    public
    TestNametags ( ) {
        super ( "TestNametags" , "Let's try to fix nametags!" , Module.Category.RENDER , true , false , true );
        INSTANCE = this;
    }

    public static
    TestNametags getInstance ( ) {
        if ( INSTANCE == null ) {
            INSTANCE = new TestNametags ( );
        }
        return INSTANCE;
    }

    public
    void onRender3D ( Render3DEvent event ) {
        if ( ! TestNametags.fullNullCheck ( ) ) {
            for (EntityPlayer player : TestNametags.mc.world.playerEntities) {
                if ( player == null || player.equals ( TestNametags.mc.player ) || ! player.isEntityAlive ( ) || player.isInvisible ( ) && ! this.invisibles.getValue ( ) || this.onlyFov.getValue ( ) && ! RotationUtil.isInFov ( player ) )
                    continue;
                double x = this.interpolate ( player.lastTickPosX , player.posX , event.getPartialTicks ( ) ) - TestNametags.mc.getRenderManager ( ).renderPosX;
                double y = this.interpolate ( player.lastTickPosY , player.posY , event.getPartialTicks ( ) ) - TestNametags.mc.getRenderManager ( ).renderPosY;
                double z = this.interpolate ( player.lastTickPosZ , player.posZ , event.getPartialTicks ( ) ) - TestNametags.mc.getRenderManager ( ).renderPosZ;
                this.renderProperNameTag ( player , x , y , z , event.getPartialTicks ( ) );
            }
        }
    }

    private
    void renderProperNameTag ( EntityPlayer player , double x , double y , double z , float delta ) {
        double tempY = y;
        tempY += player.isSneaking ( ) ? 0.5 : 0.7;
        Entity camera = mc.getRenderViewEntity ( );
        assert ( camera != null );
        double originalPositionX = camera.posX;
        double originalPositionY = camera.posY;
        double originalPositionZ = camera.posZ;
        camera.posX = this.interpolate ( camera.prevPosX , camera.posX , delta );
        camera.posY = this.interpolate ( camera.prevPosY , camera.posY , delta );
        camera.posZ = this.interpolate ( camera.prevPosZ , camera.posZ , delta );
        String displayTag = this.getDisplayTag ( player );
        double distance = camera.getDistance ( x + TestNametags.mc.getRenderManager ( ).viewerPosX , y + TestNametags.mc.getRenderManager ( ).viewerPosY , z + TestNametags.mc.getRenderManager ( ).viewerPosZ );
        int width = this.renderer.getStringWidth ( displayTag ) / 2;
        double scale = ( 0.0018 + (double) this.scaling.getValue ( ) * ( distance * (double) this.factor.getValue ( ) ) ) / 1000.0;
        if ( distance <= 8.0 && this.smartScale.getValue ( ) ) {
            scale = 0.0245;
        }
        if ( ! this.scaleing.getValue ( ) ) {
            scale = (double) this.scaling.getValue ( ) / 100.0;
        }
        GlStateManager.pushMatrix ( );
        RenderHelper.enableStandardItemLighting ( );
        GlStateManager.enablePolygonOffset ( );
        GlStateManager.doPolygonOffset ( 1.0f , - 1500000.0f );
        GlStateManager.disableLighting ( );
        GlStateManager.translate ( (float) x , (float) tempY + 1.4f , (float) z );
        GlStateManager.rotate ( - TestNametags.mc.getRenderManager ( ).playerViewY , 0.0f , 1.0f , 0.0f );
        GlStateManager.rotate ( TestNametags.mc.getRenderManager ( ).playerViewX , TestNametags.mc.gameSettings.thirdPersonView == 2 ? - 1.0f : 1.0f , 0.0f , 0.0f );
        GlStateManager.scale ( - scale , - scale , scale );
        GlStateManager.disableDepth ( );
        GlStateManager.enableBlend ( );
        GlStateManager.disableAlpha ( );
        if ( this.rect.getValue ( ) ) {
            this.drawRect ( - width - 2 , - ( TestNametags.mc.fontRenderer.FONT_HEIGHT + 1 ) , (float) width + 2.0f , 1.5f , 0x55000000 );
            if ( this.outline.getValue ( ) ) {
                int color = this.colorSync.getValue ( ) ? Colors.INSTANCE.getCurrentColorHex ( ) : new Color ( this.redSetting.getValue ( ) , this.greenSetting.getValue ( ) , this.blueSetting.getValue ( ) , this.alphaSetting.getValue ( ) ).getRGB ( );
                this.drawOutlineRect ( - width - 2 , - ( TestNametags.mc.fontRenderer.FONT_HEIGHT + 1 ) , (float) width + 2.0f , 1.5f , color );
            }
        }
        GlStateManager.enableAlpha ( );
        ItemStack renderMainHand = player.getHeldItemMainhand ( ).copy ( );
        if ( ! renderMainHand.hasEffect ( ) || renderMainHand.getItem ( ) instanceof ItemTool || renderMainHand.getItem ( ) instanceof ItemArmor ) {
            // empty if block
        }
        if ( this.heldStackName.getValue ( ) && ! renderMainHand.isEmpty && renderMainHand.getItem ( ) != Items.AIR ) {
            String stackName = renderMainHand.getDisplayName ( );
            int stackNameWidth = this.renderer.getStringWidth ( stackName ) / 2;
            GL11.glPushMatrix ( );
            GL11.glScalef ( 0.75f , 0.75f , 0.0f );
            this.renderer.drawStringWithShadow ( stackName , (float) ( - stackNameWidth ) , - ( this.getBiggestArmorTag ( player ) + 20.0f ) , - 1 );
            GL11.glScalef ( 1.5f , 1.5f , 1.0f );
            GL11.glPopMatrix ( );
        }
        ArrayList < ItemStack > armorInventory = new ArrayList <> ( player.inventory.armorInventory );
        if ( this.reverseArmor.getValue ( ) ) {
            Collections.reverse ( armorInventory );
        }
        GlStateManager.pushMatrix ( );
        int xOffset = - 8;
        for (final ItemStack stack : armorInventory) {
            if ( stack != null ) {
                xOffset -= 8;
            }
        }
        xOffset -= 8;
        ItemStack renderOffhand = player.getHeldItemOffhand ( ).copy ( );
        if ( ! renderOffhand.hasEffect ( ) || renderOffhand.getItem ( ) instanceof ItemTool || renderOffhand.getItem ( ) instanceof ItemArmor ) {
            // empty if block
        }
        this.renderItemStack ( player , renderOffhand , xOffset , - 26 , this.armor.getValue ( ) );
        xOffset += 16;
        for (ItemStack stack : armorInventory) {
            if ( stack == null ) continue;
            ItemStack armourStack = stack.copy ( );
            if ( ! armourStack.hasEffect ( ) || armourStack.getItem ( ) instanceof ItemTool || armourStack.getItem ( ) instanceof ItemArmor ) {
                // empty if block
            }
            this.renderItemStack ( player , armourStack , xOffset , - 26 , this.armor.getValue ( ) );
            xOffset += 16;
        }
        this.renderItemStack ( player , renderMainHand , xOffset , - 26 , this.armor.getValue ( ) );
        GlStateManager.popMatrix ( );
        this.renderer.drawStringWithShadow ( displayTag , (float) ( - width ) , (float) ( - ( this.renderer.getFontHeight ( ) - 1 ) ) , this.getDisplayColour ( player ) );
        camera.posX = originalPositionX;
        camera.posY = originalPositionY;
        camera.posZ = originalPositionZ;
        GlStateManager.enableDepth ( );
        GlStateManager.enableLighting ( );
        GlStateManager.disableBlend ( );
        GlStateManager.enableLighting ( );
        GlStateManager.disablePolygonOffset ( );
        GlStateManager.doPolygonOffset ( 1.0f , 1500000.0f );
        GlStateManager.popMatrix ( );
    }

    private
    void renderNameTag ( EntityPlayer player , double x , double y , double z , float partialTicks ) {
        double tempY = y + ( player.isSneaking ( ) ? 0.5 : 0.7 );
        Entity camera = mc.getRenderViewEntity ( );
        assert ( camera != null );
        double originalPositionX = camera.posX;
        double originalPositionY = camera.posY;
        double originalPositionZ = camera.posZ;
        camera.posX = this.interpolate ( camera.prevPosX , camera.posX , partialTicks );
        camera.posY = this.interpolate ( camera.prevPosY , camera.posY , partialTicks );
        camera.posZ = this.interpolate ( camera.prevPosZ , camera.posZ , partialTicks );
        double distance = camera.getDistance ( x + TestNametags.mc.getRenderManager ( ).viewerPosX , y + TestNametags.mc.getRenderManager ( ).viewerPosY , z + TestNametags.mc.getRenderManager ( ).viewerPosZ );
        int width = TestNametags.mc.fontRenderer.getStringWidth ( this.getDisplayTag ( player ) ) / 2;
        double scale = ( 0.0018 + (double) this.scaling.getValue ( ) * distance ) / 50.0;
        GlStateManager.pushMatrix ( );
        RenderHelper.enableStandardItemLighting ( );
        GlStateManager.enablePolygonOffset ( );
        GlStateManager.doPolygonOffset ( 1.0f , - 1500000.0f );
        GlStateManager.disableLighting ( );
        GlStateManager.translate ( (float) x , (float) tempY + 1.4f , (float) z );
        GlStateManager.rotate ( - TestNametags.mc.getRenderManager ( ).playerViewY , 0.0f , 1.0f , 0.0f );
        float thirdPersonOffset = TestNametags.mc.gameSettings.thirdPersonView == 2 ? - 1.0f : 1.0f;
        GlStateManager.rotate ( TestNametags.mc.getRenderManager ( ).playerViewX , thirdPersonOffset , 0.0f , 0.0f );
        GlStateManager.scale ( - scale , - scale , scale );
        GlStateManager.disableDepth ( );
        GlStateManager.enableBlend ( );
        GlStateManager.disableAlpha ( );
        this.drawRect ( - width - 2 , - ( TestNametags.mc.fontRenderer.FONT_HEIGHT + 1 ) , (float) width + 2.0f , 1.5f , 0x55000000 );
        GlStateManager.enableAlpha ( );
        TestNametags.mc.fontRenderer.drawStringWithShadow ( this.getDisplayTag ( player ) , (float) ( - width ) , (float) ( - ( TestNametags.mc.fontRenderer.FONT_HEIGHT - 1 ) ) , this.getNameColor ( player ).getRGB ( ) );
        if ( this.armor.getValue ( ) ) {
            GlStateManager.pushMatrix ( );
            double changeValue = 16.0;
            int xOffset = 0;
            xOffset = (int) ( (double) xOffset - changeValue / 2.0 * (double) player.inventory.armorInventory.size ( ) );
            xOffset = (int) ( (double) xOffset - changeValue / 2.0 );
            xOffset = (int) ( (double) xOffset - changeValue / 2.0 );
            if ( ! player.getHeldItemMainhand ( ).isEmpty ( ) ) {
                // empty if block
            }
            xOffset = (int) ( (double) xOffset + changeValue );
            for (ItemStack stack : player.inventory.armorInventory) {
                if ( ! stack.isEmpty ( ) ) {
                    // empty if block
                }
                xOffset = (int) ( (double) xOffset + changeValue );
            }
            if ( ! player.getHeldItemOffhand ( ).isEmpty ( ) ) {
                // empty if block
            }
            GlStateManager.popMatrix ( );
        }
        camera.posX = originalPositionX;
        camera.posY = originalPositionY;
        camera.posZ = originalPositionZ;
        GlStateManager.enableDepth ( );
        GlStateManager.enableLighting ( );
        GlStateManager.disableBlend ( );
        GlStateManager.enableLighting ( );
        GlStateManager.disablePolygonOffset ( );
        GlStateManager.doPolygonOffset ( 1.0f , 1500000.0f );
        GlStateManager.popMatrix ( );
    }

    public
    void drawRect ( float x , float y , float w , float h , int color ) {
        float alpha = (float) ( color >> 24 & 0xFF ) / 255.0f;
        float red = (float) ( color >> 16 & 0xFF ) / 255.0f;
        float green = (float) ( color >> 8 & 0xFF ) / 255.0f;
        float blue = (float) ( color & 0xFF ) / 255.0f;
        Tessellator tessellator = Tessellator.getInstance ( );
        BufferBuilder bufferbuilder = tessellator.getBuffer ( );
        GlStateManager.enableBlend ( );
        GlStateManager.disableTexture2D ( );
        GlStateManager.glLineWidth ( this.lineWidth.getValue ( ) );
        GlStateManager.tryBlendFuncSeparate ( 770 , 771 , 1 , 0 );
        bufferbuilder.begin ( 7 , DefaultVertexFormats.POSITION_COLOR );
        bufferbuilder.pos ( x , h , 0.0 ).color ( red , green , blue , alpha ).endVertex ( );
        bufferbuilder.pos ( w , h , 0.0 ).color ( red , green , blue , alpha ).endVertex ( );
        bufferbuilder.pos ( w , y , 0.0 ).color ( red , green , blue , alpha ).endVertex ( );
        bufferbuilder.pos ( x , y , 0.0 ).color ( red , green , blue , alpha ).endVertex ( );
        tessellator.draw ( );
        GlStateManager.enableTexture2D ( );
        GlStateManager.disableBlend ( );
    }

    public
    void drawOutlineRect ( float x , float y , float w , float h , int color ) {
        float alpha = (float) ( color >> 24 & 0xFF ) / 255.0f;
        float red = (float) ( color >> 16 & 0xFF ) / 255.0f;
        float green = (float) ( color >> 8 & 0xFF ) / 255.0f;
        float blue = (float) ( color & 0xFF ) / 255.0f;
        Tessellator tessellator = Tessellator.getInstance ( );
        BufferBuilder bufferbuilder = tessellator.getBuffer ( );
        GlStateManager.enableBlend ( );
        GlStateManager.disableTexture2D ( );
        GlStateManager.glLineWidth ( this.lineWidth.getValue ( ) );
        GlStateManager.tryBlendFuncSeparate ( 770 , 771 , 1 , 0 );
        bufferbuilder.begin ( 2 , DefaultVertexFormats.POSITION_COLOR );
        bufferbuilder.pos ( x , h , 0.0 ).color ( red , green , blue , alpha ).endVertex ( );
        bufferbuilder.pos ( w , h , 0.0 ).color ( red , green , blue , alpha ).endVertex ( );
        bufferbuilder.pos ( w , y , 0.0 ).color ( red , green , blue , alpha ).endVertex ( );
        bufferbuilder.pos ( x , y , 0.0 ).color ( red , green , blue , alpha ).endVertex ( );
        tessellator.draw ( );
        GlStateManager.enableTexture2D ( );
        GlStateManager.disableBlend ( );
    }

    private
    Color getNameColor ( Entity entity ) {
        return Color.WHITE;
    }

    private
    void renderItemStack ( EntityPlayer player , ItemStack stack , int x , int y , boolean item ) {
        GlStateManager.pushMatrix ( );
        GlStateManager.depthMask ( true );
        GlStateManager.clear ( 256 );
        RenderHelper.enableStandardItemLighting ( );
        TestNametags.mc.getRenderItem ( ).zLevel = - 150.0f;
        GlStateManager.disableAlpha ( );
        GlStateManager.enableDepth ( );
        GlStateManager.disableCull ( );
        if ( item ) {
            mc.getRenderItem ( ).renderItemAndEffectIntoGUI ( stack , x , y );
            mc.getRenderItem ( ).renderItemOverlays ( TestNametags.mc.fontRenderer , stack , x , y );
        }
        TestNametags.mc.getRenderItem ( ).zLevel = 0.0f;
        RenderHelper.disableStandardItemLighting ( );
        GlStateManager.enableCull ( );
        GlStateManager.enableAlpha ( );
        GlStateManager.scale ( 0.5f , 0.5f , 0.5f );
        GlStateManager.disableDepth ( );
        this.renderEnchantmentText ( player , stack , x , y );
        GlStateManager.enableDepth ( );
        GlStateManager.scale ( 2.0f , 2.0f , 2.0f );
        GlStateManager.popMatrix ( );
    }

    private
    boolean shouldMoveArmor ( EntityPlayer player ) {
        for (ItemStack stack : player.inventory.armorInventory) {
            NBTTagList enchants = stack.getEnchantmentTagList ( );
            if ( enchants.tagCount ( ) == 0 ) continue;
            return true;
        }
        ItemStack renderMainHand = player.getHeldItemMainhand ( ).copy ( );
        if ( renderMainHand.hasEffect ( ) ) {
            return true;
        }
        ItemStack renderOffHand = player.getHeldItemOffhand ( ).copy ( );
        return renderMainHand.hasEffect ( );
    }

    private
    void renderEnchantmentText ( EntityPlayer player , ItemStack stack , int x , int y ) {
        int enchantmentY = (int) ( (float) ( y - 8 ) - ( this.topEnchant.getValue ( ) ? this.getBiggestArmorTag ( player ) - this.getEnchantHeight ( stack ) : 0.0f ) );
        if ( stack.getItem ( ) == Items.GOLDEN_APPLE && stack.hasEffect ( ) ) {
            this.renderer.drawStringWithShadow ( "god" , (float) ( x * 2 ) , (float) enchantmentY , - 3977919 );
            enchantmentY -= 8;
        }
        NBTTagList enchants = stack.getEnchantmentTagList ( );
        for (int index = 0; index < enchants.tagCount ( ); ++ index) {
            String encName = null;
            short id = enchants.getCompoundTagAt ( index ).getShort ( "id" );
            short level = enchants.getCompoundTagAt ( index ).getShort ( "lvl" );
            Enchantment enc = Enchantment.getEnchantmentByID ( id );
            if ( enc == null ) continue;
            String string = enc.isCurse ( ) ? ( this.altEnchantNames.getValue ( ) ? enc.getTranslatedName ( level ).substring ( 11 ).substring ( 0 , this.altEnchantNames.getValue ( ) ? 3 : 1 ) : TextFormatting.RED + enc.getTranslatedName ( level ).substring ( 11 ).substring ( 0 , this.altEnchantNames.getValue ( ) ? ( enc.getMaxLevel ( ) == 1 ? 3 : 2 ) : 1 ) ) : ( encName = enc.getTranslatedName ( level ).substring ( 0 , this.altEnchantNames.getValue ( ) ? ( enc.getMaxLevel ( ) == 1 ? 3 : 2 ) : 1 ) );
            if ( enc.getMaxLevel ( ) != 1 ) {
                encName = encName + level;
            }
            if ( ! this.altEnchantNames.getValue ( ) ) {
                encName = Objects.requireNonNull ( encName ).toLowerCase ( );
            }
            this.renderer.drawStringWithShadow ( encName , (float) ( x * 2 ) , (float) enchantmentY , - 1 );
            enchantmentY -= 8;
        }
        if ( DamageUtil.hasDurability ( stack ) ) {
            int percent = DamageUtil.getRoundedDamage ( stack );
            String color = percent >= 60 ? "\u00a7a" : ( percent >= 25 ? "\u00a7e" : "\u00a7c" );
            this.renderer.drawStringWithShadow ( color + percent + "%" , (float) ( x * 2 ) , (float) enchantmentY , - 1 );
        }
    }

    private
    float getEnchantHeight ( ItemStack stack ) {
        float enchantHeight = 0.0f;
        NBTTagList enchants = stack.getEnchantmentTagList ( );
        for (int index = 0; index < enchants.tagCount ( ); ++ index) {
            short id = enchants.getCompoundTagAt ( index ).getShort ( "id" );
            Enchantment enc = Enchantment.getEnchantmentByID ( id );
            if ( enc == null ) continue;
            enchantHeight += 8.0f;
        }
        return enchantHeight;
    }

    private
    float getBiggestArmorTag ( EntityPlayer player ) {
        ItemStack renderOffHand;
        Enchantment enc;
        int index;
        float enchantmentY = 0.0f;
        boolean arm = false;
        for (ItemStack stack : player.inventory.armorInventory) {
            float encY = 0.0f;
            if ( stack != null ) {
                NBTTagList enchants = stack.getEnchantmentTagList ( );
                for (index = 0; index < enchants.tagCount ( ); ++ index) {
                    short id = enchants.getCompoundTagAt ( index ).getShort ( "id" );
                    enc = Enchantment.getEnchantmentByID ( id );
                    if ( enc == null ) continue;
                    encY += 8.0f;
                    arm = true;
                }
            }
            if ( ! ( encY > enchantmentY ) ) continue;
            enchantmentY = encY;
        }
        ItemStack renderMainHand = player.getHeldItemMainhand ( ).copy ( );
        if ( renderMainHand.hasEffect ( ) ) {
            float encY = 0.0f;
            NBTTagList enchants = renderMainHand.getEnchantmentTagList ( );
            for (int index2 = 0; index2 < enchants.tagCount ( ); ++ index2) {
                short id = enchants.getCompoundTagAt ( index2 ).getShort ( "id" );
                Enchantment enc2 = Enchantment.getEnchantmentByID ( id );
                if ( enc2 == null ) continue;
                encY += 8.0f;
                arm = true;
            }
            if ( encY > enchantmentY ) {
                enchantmentY = encY;
            }
        }
        if ( ( renderOffHand = player.getHeldItemOffhand ( ).copy ( ) ).hasEffect ( ) ) {
            float encY = 0.0f;
            NBTTagList enchants = renderOffHand.getEnchantmentTagList ( );
            for (index = 0; index < enchants.tagCount ( ); ++ index) {
                short id = enchants.getCompoundTagAt ( index ).getShort ( "id" );
                enc = Enchantment.getEnchantmentByID ( id );
                if ( enc == null ) continue;
                encY += 8.0f;
                arm = true;
            }
            if ( encY > enchantmentY ) {
                enchantmentY = encY;
            }
        }
        return (float) ( arm ? 0 : 20 ) + enchantmentY;
    }

    private
    String getDisplayTag ( EntityPlayer player ) {
        String name = player.getDisplayName ( ).getFormattedText ( );
        if ( name.contains ( mc.getSession ( ).getUsername ( ) ) ) {
            name = "You";
        }
        if ( ! this.health.getValue ( ) ) {
            return name;
        }
        float health = EntityUtil.getHealth ( player );
        String color = health > 18.0f ? "\u00a7a" : ( health > 16.0f ? "\u00a72" : ( health > 12.0f ? "\u00a7e" : ( health > 8.0f ? "\u00a76" : ( health > 5.0f ? "\u00a7c" : "\u00a74" ) ) ) );
        String pingStr = "";
        if ( this.ping.getValue ( ) ) {
            try {
                int responseTime = ( (NetHandlerPlayClient) Objects.requireNonNull ( (Object) mc.getConnection ( ) ) ).getPlayerInfo ( player.getUniqueID ( ) ).getResponseTime ( );
                pingStr = pingStr + responseTime + "ms ";
            } catch ( Exception responseTime ) {
                // empty catch block
            }
        }
        String popStr = " ";
        if ( this.totemPops.getValue ( ) ) {
            popStr = popStr + Phobos.totemPopManager.getTotemPopString ( player );
        }
        String idString = "";
        if ( this.entityID.getValue ( ) ) {
            idString = idString + "ID: " + player.getEntityId ( ) + " ";
        }
        String gameModeStr = "";
        if ( this.gamemode.getValue ( ) ) {
            gameModeStr = player.isCreative ( ) ? gameModeStr + "[C] " : ( player.isSpectator ( ) || player.isInvisible ( ) ? gameModeStr + "[I] " : gameModeStr + "[S] " );
        }
        name = Math.floor ( health ) == (double) health ? name + color + " " + ( health > 0.0f ? Integer.valueOf ( (int) Math.floor ( health ) ) : "dead" ) : name + color + " " + ( health > 0.0f ? Integer.valueOf ( (int) health ) : "dead" );
        return pingStr + idString + gameModeStr + name + popStr;
    }

    private
    int getDisplayColour ( EntityPlayer player ) {
        int colour = - 5592406;
        if ( this.whiter.getValue ( ) ) {
            colour = - 1;
        }
        if ( Phobos.friendManager.isFriend ( player ) ) {
            return - 11157267;
        }
        if ( player.isInvisible ( ) ) {
            colour = - 1113785;
        } else if ( player.isSneaking ( ) && this.sneak.getValue ( ) ) {
            colour = - 6481515;
        }
        return colour;
    }

    private
    double interpolate ( double previous , double current , float partialTicks ) {
        return previous + ( current - previous ) * (double) partialTicks;
    }
}
