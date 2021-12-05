package me.earth.phobos.features.modules.client;

import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.ClientEvent;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.features.gui.PhobosGui;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public
class ClickGui
        extends Module {
    private static ClickGui INSTANCE = new ClickGui ( );
    public Setting < Boolean > colorSync = this.register ( new Setting <> ( "Sync" , false ) );
    public Setting < Boolean > outline = this.register ( new Setting <> ( "Outline" , false ) );
    public Setting < Boolean > rainbowRolling = this.register ( new Setting < Object > ( "RollingRainbow" , Boolean.FALSE , v -> this.colorSync.getValue ( ) && Colors.INSTANCE.rainbow.getValue ( ) ) );
    public Setting < String > prefix = this.register ( new Setting <> ( "Prefix" , "." ).setRenderName ( true ) );
    public Setting < Boolean > snowing = this.register(new Setting <> ("Snowing", true));
    public Setting < Boolean > gears = this.register(new Setting <> ("Gears", true));
    public Setting < Integer > red = this.register ( new Setting <> ( "Red" , 40 , 0 , 255 ) );
    public Setting < Integer > green = this.register ( new Setting <> ( "Green" , 160 , 0 , 255 ) );
    public Setting < Integer > blue = this.register ( new Setting <> ( "Blue" , 0 , 0 , 255 ) );
    public Setting < Integer > hoverAlpha = this.register ( new Setting <> ( "Alpha" , 180 , 0 , 255 ) );
    public Setting < Integer > alpha = this.register ( new Setting <> ( "HoverAlpha" , 240 , 0 , 255 ) );
    public Setting < Boolean > blurEffect = this.register ( new Setting <> ( "Blur" , true ) );
    public Setting < Boolean > customFov = this.register ( new Setting <> ( "CustomFov" , false ) );
    public Setting < Float > fov = this.register ( new Setting < Object > ( "Fov" , 150.0f , - 180.0f , 180.0f , v -> this.customFov.getValue ( ) ) );
    public Setting < Boolean > openCloseChange = this.register ( new Setting <> ( "Open/Close" , false ) );
    public Setting < String > open = this.register ( new Setting < Object > ( "Open:" , "" , v -> this.openCloseChange.getValue ( ) ).setRenderName ( true ) );
    public Setting < String > close = this.register ( new Setting < Object > ( "Close:" , "" , v -> this.openCloseChange.getValue ( ) ).setRenderName ( true ) );
    public Setting < String > moduleButton = this.register ( new Setting < Object > ( "Buttons:" , "" , v -> ! this.openCloseChange.getValue ( ) ).setRenderName ( true ) );
    public Setting < Boolean > devSettings = this.register ( new Setting <> ( "DevSettings" , true ) );
    public Setting < Integer > topRed = this.register ( new Setting < Object > ( "TopRed" , 50 , 0 , 255 , v -> this.devSettings.getValue ( ) ) );
    public Setting < Integer > topGreen = this.register ( new Setting < Object > ( "TopGreen" , 120 , 0 , 255 , v -> this.devSettings.getValue ( ) ) );
    public Setting < Integer > topBlue = this.register ( new Setting < Object > ( "TopBlue" , 0 , 0 , 255 , v -> this.devSettings.getValue ( ) ) );
    public Setting < Integer > topAlpha = this.register ( new Setting < Object > ( "TopAlpha" , 255 , 0 , 255 , v -> this.devSettings.getValue ( ) ) );

    public
    ClickGui ( ) {
        super ( "ClickGui" , "Opens the ClickGui" , Module.Category.CLIENT , true , false , false );
        setBind(Keyboard.KEY_RSHIFT);
        this.setInstance ( );
    }

    public static
    ClickGui getInstance ( ) {
        if ( INSTANCE == null ) {
            INSTANCE = new ClickGui ( );
        }
        return INSTANCE;
    }

    public static void drawParticle() {
    }

    private
    void setInstance ( ) {
        INSTANCE = this;
    }

    @Override
    public
    void onUpdate ( ) {
        if ( this.customFov.getValue ( ) ) {
            ClickGui.mc.gameSettings.setOptionFloatValue ( GameSettings.Options.FOV , this.fov.getValue ( ) );
        }
    }

    @SubscribeEvent
    public
    void onSettingChange ( ClientEvent event ) {
        if ( event.getStage ( ) == 2 && event.getSetting ( ).getFeature ( ).equals ( this ) ) {
            if ( event.getSetting ( ).equals ( this.prefix ) ) {
                Phobos.commandManager.setPrefix ( this.prefix.getPlannedValue ( ) );
                Command.sendMessage ( "Prefix set to \u00a7a" + Phobos.commandManager.getPrefix ( ) );
            }
            Phobos.colorManager.setColor ( this.red.getPlannedValue ( ) , this.green.getPlannedValue ( ) , this.blue.getPlannedValue ( ) , this.hoverAlpha.getPlannedValue ( ) );
        }
    }

    @Override
    public
    void onEnable ( ) {
        mc.displayGuiScreen ( new PhobosGui ( ) );
        if ( this.blurEffect.getValue ( ) ) {
            ClickGui.mc.entityRenderer.loadShader ( new ResourceLocation ( "shaders/post/blur.json" ) );
        }
    }

    @Override
    public
    void onLoad ( ) {
        if ( this.colorSync.getValue ( ) ) {
            Phobos.colorManager.setColor ( Colors.INSTANCE.getCurrentColor ( ).getRed ( ) , Colors.INSTANCE.getCurrentColor ( ).getGreen ( ) , Colors.INSTANCE.getCurrentColor ( ).getBlue ( ) , this.hoverAlpha.getValue ( ) );
        } else {
            Phobos.colorManager.setColor ( this.red.getValue ( ) , this.green.getValue ( ) , this.blue.getValue ( ) , this.hoverAlpha.getValue ( ) );
        }
        Phobos.commandManager.setPrefix ( this.prefix.getValue ( ) );
    }

    @Override
    public
    void onTick ( ) {
        if ( ! ( ClickGui.mc.currentScreen instanceof PhobosGui ) ) {
            this.disable ( );
            if ( mc.entityRenderer.getShaderGroup ( ) != null )
                mc.entityRenderer.getShaderGroup ( ).deleteShaderGroup ( );
        }
    }

    @Override
    public
    void onDisable ( ) {
        if ( ClickGui.mc.currentScreen instanceof PhobosGui ) {
            mc.displayGuiScreen ( null );
        }
    }
}

