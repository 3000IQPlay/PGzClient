package me.earth.phobos;

import me.earth.phobos.features.gui.custom.GuiCustomMainScreen;
import me.earth.phobos.features.modules.client.PhobosChat;
import me.earth.phobos.features.modules.misc.RPC;
import me.earth.phobos.manager.*;
import me.earth.phobos.util.Tracker;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;

import java.io.IOException;

@Mod(modid = "pgzclient", name = "PGzClient", version = "2")
public
class Phobos {
    public static final String MODID = "pgzclient";
    public static final String MODNAME = "PGzClient";
    public static final String MODVER = "1";
    public static final String NAME_UNICODE = "3\u1d00\u0280\u1d1b\u029c\u029c4\u1d04\u1d0b";
    public static final String PHOBOS_UNICODE = "\u1d18\u029c\u1d0f\u0299\u1d0f\ua731";
    public static final String CHAT_SUFFIX = " \u23d0 3\u1d00\u0280\u1d1b\u029c\u029c4\u1d04\u1d0b";
    public static final String PHOBOS_SUFFIX = " \u23d0 \u1d18\u029c\u1d0f\u0299\u1d0f\ua731";
    public static final Logger LOGGER = LogManager.getLogger ( "PGzClient" );
    public static ModuleManager moduleManager;
    public static SpeedManager speedManager;
    public static PositionManager positionManager;
    public static RotationManager rotationManager;
    public static CommandManager commandManager;
    public static EventManager eventManager;
    public static ConfigManager configManager;
    public static FileManager fileManager;
    public static FriendManager friendManager;
    public static TextManager textManager;
    public static ColorManager colorManager;
    public static ServerManager serverManager;
    public static PotionManager potionManager;
    public static InventoryManager inventoryManager;
    public static TimerManager timerManager;
    public static PacketManager packetManager;
    public static ReloadManager reloadManager;
    public static TotemPopManager totemPopManager;
    public static HoleManager holeManager;
    public static NotificationManager notificationManager;
    public static SafetyManager safetyManager;
    public static GuiCustomMainScreen customMainScreen;
    public static CosmeticsManager cosmeticsManager;
    public static NoStopManager baritoneManager;
    public static WaypointManager waypointManager;
    @Mod.Instance
    public static Phobos INSTANCE;
    private static boolean unloaded;

    static {
        unloaded = false;
    }

    public static
    void load ( ) {
        LOGGER.info ( "\n\nLoading PGzClient v2" );
        unloaded = false;
        if ( reloadManager != null ) {
            reloadManager.unload ( );
            reloadManager = null;
        }
        baritoneManager = new NoStopManager ( );
        totemPopManager = new TotemPopManager ( );
        timerManager = new TimerManager ( );
        packetManager = new PacketManager ( );
        serverManager = new ServerManager ( );
        colorManager = new ColorManager ( );
        textManager = new TextManager ( );
        moduleManager = new ModuleManager ( );
        speedManager = new SpeedManager ( );
        rotationManager = new RotationManager ( );
        positionManager = new PositionManager ( );
        commandManager = new CommandManager ( );
        eventManager = new EventManager ( );
        configManager = new ConfigManager ( );
        fileManager = new FileManager ( );
        friendManager = new FriendManager ( );
        potionManager = new PotionManager ( );
        inventoryManager = new InventoryManager ( );
        holeManager = new HoleManager ( );
        notificationManager = new NotificationManager ( );
        safetyManager = new SafetyManager ( );
        waypointManager = new WaypointManager ( );
        LOGGER.info ( "Initialized Management" );
        moduleManager.init ( );
        LOGGER.info ( "Modules loaded." );
        configManager.init ( );
        eventManager.init ( );
        LOGGER.info ( "EventManager loaded." );
        textManager.init ( true );
        moduleManager.onLoad ( );
        totemPopManager.init ( );
        timerManager.init ( );
        if ( moduleManager.getModuleByClass ( RPC.class ).isEnabled ( ) ) {
            DiscordPresence.start ( );
        }
        cosmeticsManager = new CosmeticsManager ( );
        LOGGER.info ( "PGzClient initialized!\n" );
    }

    public static
    void unload ( boolean unload ) {
        LOGGER.info ( "\n\nUnloading PGzClient v2" );
        if ( unload ) {
            reloadManager = new ReloadManager ( );
            reloadManager.init ( commandManager != null ? commandManager.getPrefix ( ) : "." );
        }
        if ( baritoneManager != null ) {
            baritoneManager.stop ( );
        }
        Phobos.onUnload ( );
        eventManager = null;
        holeManager = null;
        timerManager = null;
        moduleManager = null;
        totemPopManager = null;
        serverManager = null;
        colorManager = null;
        textManager = null;
        speedManager = null;
        rotationManager = null;
        positionManager = null;
        commandManager = null;
        configManager = null;
        fileManager = null;
        friendManager = null;
        potionManager = null;
        inventoryManager = null;
        notificationManager = null;
        safetyManager = null;
        LOGGER.info ( "PGzClient unloaded!\n" );
    }

    public static
    void reload ( ) {
        Phobos.unload ( false );
        Phobos.load ( );
    }

    public static
    void onUnload ( ) {
        if ( ! unloaded ) {
            try {
                PhobosChat.INSTANCE.disconnect ( );
            } catch ( IOException e ) {
                e.printStackTrace ( );
            }
            eventManager.onUnload ( );
            moduleManager.onUnload ( );
            configManager.saveConfig ( Phobos.configManager.config.replaceFirst ( "pgzclient/" , "" ) );
            moduleManager.onUnloadPost ( );
            timerManager.unload ( );
            unloaded = true;
        }
    }

    @Mod.EventHandler
    public
    void preInit ( FMLPreInitializationEvent event ) {
        new Tracker ( );
        LOGGER.info ( "BerryPGz is UwU!!!" );
        LOGGER.info ( "CA GO BRRRRRRR - Berry" );
        LOGGER.info ( "megyn wins again" );
        LOGGER.info ( "DA FACCCC - Berry" );
    }

    @Mod.EventHandler
    public
    void init ( FMLInitializationEvent event ) {
        customMainScreen = new GuiCustomMainScreen ( );
        Display.setTitle ( "PGzClient - v2" );
        Phobos.load ( );
    }
}
