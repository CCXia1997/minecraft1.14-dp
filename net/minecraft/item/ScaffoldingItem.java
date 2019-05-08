package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.block.BlockState;
import net.minecraft.world.BlockView;
import net.minecraft.block.ScaffoldingBlock;
import net.minecraft.network.Packet;
import net.minecraft.client.network.packet.ChatMessageS2CPacket;
import net.minecraft.text.ChatMessageType;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.block.Block;

public class ScaffoldingItem extends BlockItem
{
    public ScaffoldingItem(final Block block, final Settings settings) {
        super(block, settings);
    }
    
    @Nullable
    @Override
    public ItemPlacementContext getPlacementContext(final ItemPlacementContext itemPlacementContext) {
        final BlockPos blockPos2 = itemPlacementContext.getBlockPos();
        final World world3 = itemPlacementContext.getWorld();
        BlockState blockState4 = world3.getBlockState(blockPos2);
        final Block block5 = this.getBlock();
        if (blockState4.getBlock() == block5) {
            Direction direction6;
            if (itemPlacementContext.isPlayerSneaking()) {
                direction6 = (itemPlacementContext.k() ? itemPlacementContext.getFacing().getOpposite() : itemPlacementContext.getFacing());
            }
            else {
                direction6 = ((itemPlacementContext.getFacing() == Direction.UP) ? itemPlacementContext.getPlayerHorizontalFacing() : Direction.UP);
            }
            int integer7 = 0;
            final BlockPos.Mutable mutable8 = new BlockPos.Mutable(blockPos2).setOffset(direction6);
            while (integer7 < 7) {
                if (!world3.isClient && !World.isValid(mutable8)) {
                    final PlayerEntity playerEntity9 = itemPlacementContext.getPlayer();
                    final int integer8 = world3.getHeight();
                    if (playerEntity9 instanceof ServerPlayerEntity && mutable8.getY() >= integer8) {
                        final ChatMessageS2CPacket chatMessageS2CPacket11 = new ChatMessageS2CPacket(new TranslatableTextComponent("build.tooHigh", new Object[] { integer8 }).applyFormat(TextFormat.m), ChatMessageType.c);
                        ((ServerPlayerEntity)playerEntity9).networkHandler.sendPacket(chatMessageS2CPacket11);
                        break;
                    }
                    break;
                }
                else {
                    blockState4 = world3.getBlockState(mutable8);
                    if (blockState4.getBlock() != this.getBlock()) {
                        if (blockState4.canReplace(itemPlacementContext)) {
                            return ItemPlacementContext.create(itemPlacementContext, mutable8, direction6);
                        }
                        break;
                    }
                    else {
                        mutable8.setOffset(direction6);
                        if (!direction6.getAxis().isHorizontal()) {
                            continue;
                        }
                        ++integer7;
                    }
                }
            }
            return null;
        }
        if (ScaffoldingBlock.calculateDistance(world3, blockPos2) == 7) {
            return null;
        }
        return itemPlacementContext;
    }
    
    @Override
    protected boolean shouldCheckIfStateAllowsPlacement() {
        return false;
    }
}
