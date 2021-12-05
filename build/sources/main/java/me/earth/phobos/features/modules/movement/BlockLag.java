package me.earth.phobos.features.modules.movement;

import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.UpdateWalkingPlayerEvent;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.BlockUtil;
import me.earth.phobos.util.InventoryUtil;
import me.earth.phobos.util.MathUtil;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

public
class BlockLag
        extends Module {
    private static BlockLag INSTANCE;

    private final Setting < Mode > mode = this.register ( new Setting <> ( "Mode" , Mode.OBSIDIAN ) );
    private final Setting < Boolean > smartTp = this.register ( new Setting <> ( "SmartTP" , true ) );
    private final Setting < Integer > tpMin = this.register ( new Setting <> ( "TPMin" , 2 , 2 , 10 , v -> this.smartTp.getValue ( ) ) );
    private final Setting < Integer > tpMax = this.register ( new Setting <> ( "TPMax" , 3 , 3 , 40 , v -> this.smartTp.getValue ( ) ) );
    private final Setting < Boolean > noVoid = this.register ( new Setting <> ( "NoVoid" , false , v -> this.smartTp.getValue ( ) ) );
    private final Setting < Integer > tpHeight = this.register ( new Setting <> ( "TPHeight" , 2 , - 100 , 100 , v -> ! this.smartTp.getValue ( ) ) );
    private final Setting < Boolean > keepInside = this.register ( new Setting <> ( "Center" , true ) );
    private final Setting < Boolean > rotate = this.register ( new Setting <> ( "Rotate" , false ) );
    private final Setting < Boolean > sneaking = this.register ( new Setting <> ( "Sneak" , false ) );
    private final Setting < Boolean > offground = this.register ( new Setting <> ( "Offground" , false ) );
    private final Setting < Boolean > chat = this.register ( new Setting <> ( "Chat Msgs" , true ) );
    private final Setting < Boolean > tpdebug = this.register ( new Setting <> ( "Debug" , false , v -> this.chat.getValue ( ) && this.smartTp.getValue ( ) ) );
    private BlockPos burrowPos;
    private int lastBlock;
    private int blockSlot;

    public
    BlockLag ( ) {
        super ( "BlockLag" , "Makes you better" , Module.Category.MOVEMENT , true , false , false );
        INSTANCE = this;
    }

    public static
    BlockLag getInstance ( ) {
        if ( INSTANCE == null )
            INSTANCE = new BlockLag ( );
        return INSTANCE;
    }

    @Override
    public
    void onEnable ( ) {
        this.burrowPos = new BlockPos ( BlockLag.mc.player.posX , Math.ceil ( BlockLag.mc.player.posY ) , BlockLag.mc.player.posZ );
        this.blockSlot = this.findBlockSlot ( );
        this.lastBlock = BlockLag.mc.player.inventory.currentItem;
        if ( ! doChecks ( ) || this.blockSlot == - 1 ) {
            this.disable ( );
            return;
        }
        if ( this.keepInside.getValue ( ) ) {
            double x = BlockLag.mc.player.posX - Math.floor ( BlockLag.mc.player.posX );
            double z = BlockLag.mc.player.posZ - Math.floor ( BlockLag.mc.player.posZ );
            if ( x <= 0.3 || x >= 0.7 ) {
                x = ( x > 0.5 ? 0.69 : 0.31 );
            }
            if ( z < 0.3 || z > 0.7 ) {
                z = ( z > 0.5 ? 0.69 : 0.31 );
            }
            BlockLag.mc.player.connection.sendPacket ( new CPacketPlayer.Position ( Math.floor ( BlockLag.mc.player.posX ) + x , BlockLag.mc.player.posY , Math.floor ( BlockLag.mc.player.posZ ) + z , BlockLag.mc.player.onGround ) );
            BlockLag.mc.player.setPosition ( Math.floor ( BlockLag.mc.player.posX ) + x , BlockLag.mc.player.posY , Math.floor ( BlockLag.mc.player.posZ ) + z );
            // no fucking clue how this worked i made it drunk
        }
        BlockLag.mc.player.connection.sendPacket ( new CPacketPlayer.Position ( BlockLag.mc.player.posX , BlockLag.mc.player.posY + 0.41999998688698D , BlockLag.mc.player.posZ , ! this.offground.getValue ( ) ) );
        BlockLag.mc.player.connection.sendPacket ( new CPacketPlayer.Position ( BlockLag.mc.player.posX , BlockLag.mc.player.posY + 0.7531999805211997D , BlockLag.mc.player.posZ , ! this.offground.getValue ( ) ) );
        BlockLag.mc.player.connection.sendPacket ( new CPacketPlayer.Position ( BlockLag.mc.player.posX , BlockLag.mc.player.posY + 1.00133597911214D , BlockLag.mc.player.posZ , ! this.offground.getValue ( ) ) );
        BlockLag.mc.player.connection.sendPacket ( new CPacketPlayer.Position ( BlockLag.mc.player.posX , BlockLag.mc.player.posY + 1.16610926093821D , BlockLag.mc.player.posZ , ! this.offground.getValue ( ) ) );
    }

    @SubscribeEvent
    public
    void onUpdateWalkingPlayer ( UpdateWalkingPlayerEvent event ) {
        if ( event.getStage ( ) != 0 ) return;
        if ( this.sneaking.getValue ( ) && ! BlockLag.mc.player.isSneaking ( ) ) {
            BlockLag.mc.player.connection.sendPacket ( new CPacketEntityAction ( BlockLag.mc.player , CPacketEntityAction.Action.START_SNEAKING ) );
        }
        if ( this.rotate.getValue ( ) ) {
            float[] angle = MathUtil.calcAngle ( BlockLag.mc.player.getPositionEyes ( mc.getRenderPartialTicks ( ) ) , new Vec3d ( (float) burrowPos.getX ( ) + 0.5f , (float) burrowPos.getY ( ) + 0.5f , (float) burrowPos.getZ ( ) + 0.5f ) );
            Phobos.rotationManager.setPlayerRotations ( angle[0] , angle[1] );
        } // placeblock rotate actually makes the player look down for some reason
        InventoryUtil.switchToHotbarSlot ( this.blockSlot , false );
        BlockUtil.placeBlock ( this.burrowPos , this.blockSlot == - 2 ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND , false , true , this.sneaking.getValue ( ) );
        InventoryUtil.switchToHotbarSlot ( this.lastBlock , false );
        BlockLag.mc.player.connection.sendPacket ( new CPacketPlayer.Position ( BlockLag.mc.player.posX , ( this.smartTp.getValue ( ) ? this.adaptiveTpHeight ( false ) : this.tpHeight.getValue ( ) + BlockLag.mc.player.posY ) , BlockLag.mc.player.posZ , ! this.offground.getValue ( ) ) );
        BlockLag.mc.player.connection.sendPacket ( new CPacketEntityAction ( BlockLag.mc.player , CPacketEntityAction.Action.STOP_SNEAKING ) );
        this.disable ( );
    }

    private
    int findBlockSlot ( ) {
        for (Class block : this.blockList ( )) {
            if ( block == BlockFalling.class ) { // gravity blocks
                for (int i = 0; i < 9; i++) {
                    ItemStack item = BlockLag.mc.player.inventory.getStackInSlot ( i );
                    if ( ! ( item.getItem ( ) instanceof ItemBlock ) )
                        continue;
                    Block blockFalling = Block.getBlockFromItem ( BlockLag.mc.player.inventory.getStackInSlot ( i ).getItem ( ) );
                    if ( blockFalling instanceof BlockFalling )
                        return i;
                }
            } else { // normal blocks
                int slot = InventoryUtil.findHotbarBlock ( block );
                if ( slot != - 1 )
                    return slot;
                else if ( InventoryUtil.isBlock ( BlockLag.mc.player.getHeldItemOffhand ( ).getItem ( ) , block ) )
                    return - 2;
            }
        }
        if ( this.chat.getValue ( ) )
            Command.sendMessage ( "<" + this.getDisplayName ( ) + "> " + "\u00a7c" + "No blocks to use." );
        return - 1;
    }

    private
    List < Class > blockList ( ) {
        List < Class > blocks = new ArrayList <> ( );
        blocks.add ( this.mode.getValue ( ).getPriorityBlock ( ) );
        for (Mode block : Mode.values ( )) {
            if ( this.mode.getValue ( ) == block ) continue;
            blocks.add ( block.getPriorityBlock ( ) );
        }
        return blocks;
    }

    private
    int adaptiveTpHeight ( boolean first ) {
        int max = ( BlockLag.mc.player.dimension == - 1 && this.noVoid.getValue ( ) && this.tpMax.getValue ( ) + this.burrowPos.getY ( ) > 127 ? Math.abs ( this.burrowPos.getY ( ) - 127 ) : this.tpMax.getValue ( ) );
        int airblock = ( this.noVoid.getValue ( ) && this.tpMax.getValue ( ) * - 1 + this.burrowPos.getY ( ) < 0 ? this.burrowPos.getY ( ) * - 1 : this.tpMax.getValue ( ) * - 1 );
        while ( airblock < max ) {
            if ( Math.abs ( airblock ) < this.tpMin.getValue ( ) || ! BlockLag.mc.world.isAirBlock ( this.burrowPos.offset ( EnumFacing.UP , airblock ) ) || ! BlockLag.mc.world.isAirBlock ( this.burrowPos.offset ( EnumFacing.UP , airblock + 1 ) ) ) {
                airblock++;
            } else {
                if ( this.tpdebug.getValue ( ) && this.chat.getValue ( ) && ! first )
                    Command.sendMessage ( Integer.toString ( airblock ) );
                return this.burrowPos.getY ( ) + airblock;
            }
        }
        return 69420; // if there isn't any room
    }

    private
    boolean
    doChecks ( ) {
        if ( BlockLag.fullNullCheck ( ) ) return false;
        if ( BlockUtil.isPositionPlaceable ( this.burrowPos , false , false ) < 1 ) return false;
        if ( this.smartTp.getValue ( ) && this.adaptiveTpHeight ( true ) == 69420 ) {
            if ( this.chat.getValue ( ) )
                Command.sendMessage ( "<" + this.getDisplayName ( ) + "> " + "\u00a7c" + "Not enough room to rubberband." );
            return false;
        }
        if ( ! BlockLag.mc.world.isAirBlock ( this.burrowPos.offset ( EnumFacing.UP , 2 ) ) ) {
            if ( this.chat.getValue ( ) )
                Command.sendMessage ( "<" + this.getDisplayName ( ) + "> " + "\u00a7c" + "Not enough room to jump." );
            return false;
        }
        for (Entity entity : BlockUtil.mc.world.getEntitiesWithinAABB ( Entity.class , new AxisAlignedBB ( burrowPos ) )) {
            if ( entity instanceof EntityItem || entity instanceof EntityXPOrb || entity instanceof EntityArrow || entity instanceof EntityPlayer || entity instanceof EntityExpBottle )
                continue;
            return false;
        }
        return true;
    }

    private
    enum Mode {
        OBSIDIAN ( BlockObsidian.class ),
        ECHEST ( BlockEnderChest.class ),
        FALLING ( BlockFalling.class ),
        ENDROD ( BlockEndRod.class );
        private final Class priorityBlock;

        Mode ( Class block ) {
            this.priorityBlock = block;
        }

        private
        Class getPriorityBlock ( ) {
            return this.priorityBlock;
        }
    }
}