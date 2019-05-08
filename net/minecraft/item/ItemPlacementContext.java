package net.minecraft.item;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Direction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.Hand;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;

public class ItemPlacementContext extends ItemUsageContext
{
    private final BlockPos placedPos;
    protected boolean a;
    
    public ItemPlacementContext(final ItemUsageContext usageCtx) {
        this(usageCtx.getWorld(), usageCtx.getPlayer(), usageCtx.getHand(), usageCtx.getItemStack(), usageCtx.hitResult);
    }
    
    protected ItemPlacementContext(final World world, @Nullable final PlayerEntity player, final Hand hand, final ItemStack itemStack, final BlockHitResult blockHitResult) {
        super(world, player, hand, itemStack, blockHitResult);
        this.a = true;
        this.placedPos = blockHitResult.getBlockPos().offset(blockHitResult.getSide());
        this.a = world.getBlockState(blockHitResult.getBlockPos()).canReplace(this);
    }
    
    public static ItemPlacementContext create(final ItemPlacementContext placementCtx, final BlockPos blockPos, final Direction facing) {
        return new ItemPlacementContext(placementCtx.getWorld(), placementCtx.getPlayer(), placementCtx.getHand(), placementCtx.getItemStack(), new BlockHitResult(new Vec3d(blockPos.getX() + 0.5 + facing.getOffsetX() * 0.5, blockPos.getY() + 0.5 + facing.getOffsetY() * 0.5, blockPos.getZ() + 0.5 + facing.getOffsetZ() * 0.5), facing, blockPos, false));
    }
    
    @Override
    public BlockPos getBlockPos() {
        return this.a ? super.getBlockPos() : this.placedPos;
    }
    
    public boolean canPlace() {
        return this.a || this.getWorld().getBlockState(this.getBlockPos()).canReplace(this);
    }
    
    public boolean c() {
        return this.a;
    }
    
    public Direction getPlayerFacing() {
        return Direction.getEntityFacingOrder(this.player)[0];
    }
    
    public Direction[] getPlacementFacings() {
        final Direction[] arr1 = Direction.getEntityFacingOrder(this.player);
        if (this.a) {
            return arr1;
        }
        Direction direction2;
        int integer3;
        for (direction2 = this.getFacing(), integer3 = 0; integer3 < arr1.length && arr1[integer3] != direction2.getOpposite(); ++integer3) {}
        if (integer3 > 0) {
            System.arraycopy(arr1, 0, arr1, 1, integer3);
            arr1[0] = direction2.getOpposite();
        }
        return arr1;
    }
}
