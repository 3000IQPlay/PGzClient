package me.earth.phobos.features.gui;

import me.earth.phobos.Phobos;
import me.earth.phobos.features.Feature;
import me.earth.phobos.features.gui.components.Component;
import me.earth.phobos.features.gui.components.items.Item;
import me.earth.phobos.features.gui.components.items.buttons.ModuleButton;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.modules.client.ClickGui;
import me.earth.phobos.util.RenderUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public
class PhobosGui
        extends GuiScreen {
    private static PhobosGui INSTANCE;
    private ArrayList<Snow> _snowList = new ArrayList<Snow>();


    static {
        INSTANCE = new PhobosGui ( );
    }

    private final ArrayList < Component > components = new ArrayList <> ( );

    public
    PhobosGui ( ) {
        this.setInstance ( );
        this.load ( );
    }

    public static
    PhobosGui getInstance ( ) {
        if ( INSTANCE == null ) {
            INSTANCE = new PhobosGui ( );
        }
        return INSTANCE;
    }

    public static
    PhobosGui getClickGui ( ) {
        return PhobosGui.getInstance ( );
    }

    private
    void setInstance ( ) {
        INSTANCE = this;
    }

    private
    void load ( ) {
        int x = - 90;

        Random random = new Random();
        {

            for (int i = 0; i < 100; ++i) {
                for (int y = 0; y < 3; ++y) {
                    Snow snow = new Snow(25 * i, y * -50, random.nextInt(3) + 1, random.nextInt(2) + 1);
                    _snowList.add(snow);
                }
            }
        }
        for (final Module.Category category : Phobos.moduleManager.getCategories ( )) {
            this.components.add ( new Component ( category.getName ( ) , x += 120 , 4 , true ) {

                @Override
                public
                void setupItems ( ) {
                    Phobos.moduleManager.getModulesByCategory ( category ).forEach ( module -> {
                        if ( ! module.hidden ) {
                            this.addButton ( new ModuleButton ( module ) );
                        }
                    } );
                }
            } );
        }
        this.components.forEach ( components -> components.getItems ( ).sort ( Comparator.comparing ( Feature::getName ) ) );
    }

    public
    void updateModule ( Module module ) {
        block0:
        for (Component component : this.components) {
            for (Item item : component.getItems ( )) {
                if ( ! ( item instanceof ModuleButton ) ) continue;
                ModuleButton button = (ModuleButton) item;
                Module mod = button.getModule ( );
                if ( module == null || ! module.equals ( mod ) ) continue;
                button.initSettings ( );
                continue block0;
            }
        }
    }

    public
    void mouseClicked ( int mouseX , int mouseY , int clickedButton ) {
        this.components.forEach ( components -> components.mouseClicked ( mouseX , mouseY , clickedButton ) );
    }

    public
    void mouseReleased ( int mouseX , int mouseY , int releaseButton ) {
        this.components.forEach ( components -> components.mouseReleased ( mouseX , mouseY , releaseButton ) );
    }

    public
    boolean doesGuiPauseGame ( ) {
        return false;
    }

    public final
    ArrayList < Component > getComponents ( ) {
        return this.components;
    }

    public
    void checkMouseWheel ( ) {
        int dWheel = Mouse.getDWheel ( );
        if ( dWheel < 0 ) {
            this.components.forEach ( component -> component.setY ( component.getY ( ) - 10 ) );
        } else if ( dWheel > 0 ) {
            this.components.forEach ( component -> component.setY ( component.getY ( ) + 10 ) );
        }
    }

    public
    int getTextOffset ( ) {
        return - 6;
    }

    public
    Component getComponentByName ( String name ) {
        for (Component component : this.components) {
            if ( ! component.getName ( ).equalsIgnoreCase ( name ) ) continue;
            return component;
        }
        return null;
    }

    public
    void keyTyped ( char typedChar , int keyCode ) throws IOException {
        super.keyTyped ( typedChar , keyCode );
        this.components.forEach ( component -> component.onKeyTyped ( typedChar , keyCode ) );
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.checkMouseWheel();
        this.drawDefaultBackground();
        this.components.forEach(components -> components.drawScreen(mouseX, mouseY, partialTicks));
        final ScaledResolution res = new ScaledResolution(mc);


        if (!_snowList.isEmpty() && ClickGui.getInstance().snowing.getValue()) {
            _snowList.forEach(snow -> snow.Update(res));
        }

        if (OpenGlHelper.shadersSupported && mc.getRenderViewEntity() instanceof EntityPlayer) {
            RenderUtil.drawGradientSideways(0, 0, 1600, 1000, new Color(0, 100, 0, 0).getRGB(), new Color(0, 0, 0, 0).getRGB());
            if (mc.entityRenderer.getShaderGroup() != null) {
                mc.entityRenderer.getShaderGroup().deleteShaderGroup();
            }
            mc.entityRenderer.loadShader(new ResourceLocation("shaders/post/blur.json"));
        }

    }
}



