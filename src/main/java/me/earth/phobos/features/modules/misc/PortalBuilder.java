package me.earth.phobos.features.modules.misc;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.earth.phobos.util.BlockInteractHelper;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.features.modules.Module;
import net.minecraft.block.Block;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class PortalBuilder extends Module {
    public PortalBuilder() {
        super("PortalBuilder", "Auto nether portal.", Module.Category.MISC, true, false, false);
    }

    public Setting<Boolean> rotate = register(new Setting<Boolean>("Rotate", true));
    private final Setting<Integer> tick_for_place = this.register(new Setting("BPT", 2, 1, 8));

    Vec3d[] targets = new Vec3d[] {  new Vec3d(1.0, 1.0, 0.0), new Vec3d(1.0, 1.0, 1.0), new Vec3d(1.0, 1.0, 2.0), new Vec3d(1.0, 1.0, 3.0), new Vec3d(1.0, 2.0, 0.0), new Vec3d(1.0, 3.0, 0.0), new Vec3d(1.0, 4.0, 0.0), new Vec3d(1.0, 5.0, 0.0), new Vec3d(1.0, 5.0, 1.0), new Vec3d(1.0, 5.0, 2.0), new Vec3d(1.0, 5.0, 3.0), new Vec3d(1.0, 4.0, 3.0), new Vec3d(1.0, 3.0, 3.0), new Vec3d(1.0, 2.0, 3.0)};

    int new_slot    	= 0;
    int old_slot    	= 0;
    int y_level 		= 0;
    int tick_runs  		= 0;
    int blocks_placed 	= 0;
    int offset_step 	= 0;

    boolean sneak = false;

    @Override
    public void onEnable() {

        if (mc.player != null) {

            old_slot = mc.player.inventory.currentItem;
            new_slot = find_in_hotbar();

            if (new_slot == -1) {
                Command.sendMessage(ChatFormatting.RED + "Cannot find obi in hotbar!");
                toggle();
            }

            y_level = (int) Math.round(mc.player.posY);

        }

    }

    @Override
    public void onDisable() {

        if (mc.player != null) {

            if (new_slot != old_slot && old_slot != - 1) {
                mc.player.inventory.currentItem = old_slot;
            }

            if (sneak) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));

                sneak = false;
            }

            old_slot = - 1;
            new_slot = - 1;
        }

    }

    @Override
    public void onUpdate() {

        if (mc.player != null) {

            blocks_placed = 0;

            while (blocks_placed < this.tick_for_place.getValue()) {

                if (this.offset_step >= this.targets.length) {
                    this.offset_step = 0;
                    break;
                }

                BlockPos offsetPos = new BlockPos(this.targets[this.offset_step]);
                BlockPos targetPos = new BlockPos(mc.player.getPositionVector()).add(offsetPos.getX(), offsetPos.getY(), offsetPos.getZ()).down();

                boolean try_to_place = true;

                if (!mc.world.getBlockState(targetPos).getMaterial().isReplaceable()) {
                    try_to_place = false;
                }

                for (Entity entity : mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(targetPos))) {
                    if (entity instanceof EntityItem || entity instanceof EntityXPOrb) continue;
                    try_to_place = false;
                    break;
                }

                if (try_to_place && this.place_blocks(targetPos)) {
                    ++blocks_placed;
                }

                ++offset_step;

            }

            if (blocks_placed > 0 && this.new_slot != this.old_slot) {
                mc.player.inventory.currentItem = this.old_slot;
            }

            ++this.tick_runs;
        }
    }

    private boolean place_blocks(BlockPos pos) {

        if (!mc.world.getBlockState(pos).getMaterial().isReplaceable()) {
            return false;
        }

        if (!BlockInteractHelper.checkForNeighbours(pos)) {
            return false;
        }

        for (EnumFacing side : EnumFacing.values()) {

            Block neighborPos;
            BlockPos neighbor = pos.offset(side);

            EnumFacing side2 = side.getOpposite();

            if (!BlockInteractHelper.canBeClicked(neighbor)) continue;

            mc.player.inventory.currentItem = new_slot;

            if (BlockInteractHelper.blackList.contains((Object)(neighborPos = mc.world.getBlockState(neighbor).getBlock()))) {
                mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.START_SNEAKING));
                this.sneak = true;
            }

            Vec3d hitVec = new Vec3d((Vec3i)neighbor).add(0.5, 0.5, 0.5).add(new Vec3d(side2.getDirectionVec()).scale(0.5));

            if (this.rotate.getValue()) {
                BlockInteractHelper.faceVectorPacketInstant(hitVec);
            }

            mc.playerController.processRightClickBlock(mc.player, mc.world, neighbor, side2, hitVec, EnumHand.MAIN_HAND);
            mc.player.swingArm(EnumHand.MAIN_HAND);

            return true;
        }

        return false;

    }

    private int find_in_hotbar() {

        for (int i = 0; i < 9; ++i) {

            final ItemStack stack = mc.player.inventory.getStackInSlot(i);

            if (stack != ItemStack.EMPTY && stack.getItem() instanceof ItemBlock) {

                final Block block = ((ItemBlock) stack.getItem()).getBlock();

                if (block instanceof BlockObsidian)
                    return i;

            }
        }
        return -1;
    }
}
