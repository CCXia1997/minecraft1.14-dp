package net.minecraft.item;

import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.BlockPos;
import javax.annotation.Nullable;
import net.minecraft.world.World;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;

public class ItemUsageContext
{
    protected final PlayerEntity player;
    protected final Hand hand;
    protected final BlockHitResult hitResult;
    protected final World world;
    protected final ItemStack stack;
    
    public ItemUsageContext(final PlayerEntity player, final Hand hand, final BlockHitResult blockHitResult) {
        this(player.world, player, hand, player.getStackInHand(hand), blockHitResult);
    }
    
    protected ItemUsageContext(final World world, @Nullable final PlayerEntity player, final Hand hand, final ItemStack itemStack, final BlockHitResult blockHitResult) {
        this.player = player;
        this.hand = hand;
        this.hitResult = blockHitResult;
        this.stack = itemStack;
        this.world = world;
    }
    
    public BlockPos getBlockPos() {
        return this.hitResult.getBlockPos();
    }
    
    public Direction getFacing() {
        return this.hitResult.getSide();
    }
    
    public Vec3d getPos() {
        return this.hitResult.getPos();
    }
    
    public boolean k() {
        return this.hitResult.d();
    }
    
    public ItemStack getItemStack() {
        return this.stack;
    }
    
    @Nullable
    public PlayerEntity getPlayer() {
        return this.player;
    }
    
    public Hand getHand() {
        return this.hand;
    }
    
    public World getWorld() {
        return this.world;
    }
    
    public Direction getPlayerHorizontalFacing() {
        return (this.player == null) ? Direction.NORTH : this.player.getHorizontalFacing();
    }
    
    public boolean isPlayerSneaking() {
        return this.player != null && this.player.isSneaking();
    }
    
    public float getPlayerYaw() {
        return (this.player == null) ? 0.0f : this.player.yaw;
    }
}
