package me.earth.phobos.mixin;

import me.earth.phobos.Phobos;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

import java.util.Map;

public
class PhobosMixinLoader
        implements IFMLLoadingPlugin {

    public
    PhobosMixinLoader ( ) {
        Phobos.LOGGER.info ( "Phobos mixins initialized" );
        MixinBootstrap.init ( );
        Mixins.addConfiguration ( "mixins.phobos.json" );
        MixinEnvironment.getDefaultEnvironment ( ).setObfuscationContext ( "searge" );
        Phobos.LOGGER.info ( MixinEnvironment.getDefaultEnvironment ( ).getObfuscationContext ( ) );
    }

    public
    String[] getASMTransformerClass ( ) {
        return new String[0];
    }

    public
    String getModContainerClass ( ) {
        return null;
    }

    public
    String getSetupClass ( ) {
        return null;
    }

    public
    void injectData ( Map < String, Object > data ) {
        data.get ( "runtimeDeobfuscationEnabled" );
    }

    public
    String getAccessTransformerClass ( ) {
        return null;
    }
}

