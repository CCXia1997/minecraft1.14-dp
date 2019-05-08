package net.minecraft.item;

import net.minecraft.sound.SoundEvent;
import net.minecraft.block.Material;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.world.BlockView;
import net.minecraft.fluid.BaseFluid;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.FluidFillable;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.world.IWorld;
import net.minecraft.block.FluidDrainable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.RayTraceContext;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.fluid.Fluid;

public class BucketItem extends Item
{
    private final Fluid fluid;
    
    public BucketItem(final Fluid fluid, final Settings settings) {
        super(settings);
        this.fluid = fluid;
    }
    
    @Override
    public TypedActionResult<ItemStack> use(final World world, final PlayerEntity player, final Hand hand) {
        final ItemStack itemStack4 = player.getStackInHand(hand);
        final HitResult hitResult5 = Item.getHitResult(world, player, (this.fluid == Fluids.EMPTY) ? RayTraceContext.FluidHandling.b : RayTraceContext.FluidHandling.NONE);
        if (hitResult5.getType() == HitResult.Type.NONE) {
            return new TypedActionResult<ItemStack>(ActionResult.PASS, itemStack4);
        }
        if (hitResult5.getType() != HitResult.Type.BLOCK) {
            return new TypedActionResult<ItemStack>(ActionResult.PASS, itemStack4);
        }
        final BlockHitResult blockHitResult6 = (BlockHitResult)hitResult5;
        final BlockPos blockPos7 = blockHitResult6.getBlockPos();
        if (!world.canPlayerModifyAt(player, blockPos7) || !player.canPlaceOn(blockPos7, blockHitResult6.getSide(), itemStack4)) {
            return new TypedActionResult<ItemStack>(ActionResult.c, itemStack4);
        }
        if (this.fluid == Fluids.EMPTY) {
            final BlockState blockState8 = world.getBlockState(blockPos7);
            if (blockState8.getBlock() instanceof FluidDrainable) {
                final Fluid fluid9 = ((FluidDrainable)blockState8.getBlock()).tryDrainFluid(world, blockPos7, blockState8);
                if (fluid9 != Fluids.EMPTY) {
                    player.incrementStat(Stats.c.getOrCreateStat(this));
                    player.playSound(fluid9.matches(FluidTags.b) ? SoundEvents.aB : SoundEvents.az, 1.0f, 1.0f);
                    final ItemStack itemStack5 = this.getFilledStack(itemStack4, player, fluid9.getBucketItem());
                    if (!world.isClient) {
                        Criterions.FILLED_BUCKET.handle((ServerPlayerEntity)player, new ItemStack(fluid9.getBucketItem()));
                    }
                    return new TypedActionResult<ItemStack>(ActionResult.a, itemStack5);
                }
            }
            return new TypedActionResult<ItemStack>(ActionResult.c, itemStack4);
        }
        final BlockState blockState8 = world.getBlockState(blockPos7);
        final BlockPos blockPos8 = (blockState8.getBlock() instanceof FluidFillable && this.fluid == Fluids.WATER) ? blockPos7 : blockHitResult6.getBlockPos().offset(blockHitResult6.getSide());
        if (this.placeFluid(player, world, blockPos8, blockHitResult6)) {
            this.onEmptied(world, itemStack4, blockPos8);
            if (player instanceof ServerPlayerEntity) {
                Criterions.PLACED_BLOCK.handle((ServerPlayerEntity)player, blockPos8, itemStack4);
            }
            player.incrementStat(Stats.c.getOrCreateStat(this));
            return new TypedActionResult<ItemStack>(ActionResult.a, this.getEmptiedStack(itemStack4, player));
        }
        return new TypedActionResult<ItemStack>(ActionResult.c, itemStack4);
    }
    
    protected ItemStack getEmptiedStack(final ItemStack stack, final PlayerEntity player) {
        if (!player.abilities.creativeMode) {
            return new ItemStack(Items.kx);
        }
        return stack;
    }
    
    public void onEmptied(final World world, final ItemStack stack, final BlockPos pos) {
    }
    
    private ItemStack getFilledStack(final ItemStack stack, final PlayerEntity player, final Item filledBucket) {
        if (player.abilities.creativeMode) {
            return stack;
        }
        stack.subtractAmount(1);
        if (stack.isEmpty()) {
            return new ItemStack(filledBucket);
        }
        if (!player.inventory.insertStack(new ItemStack(filledBucket))) {
            player.dropItem(new ItemStack(filledBucket), false);
        }
        return stack;
    }
    
    public boolean placeFluid(@Nullable final PlayerEntity player, final World world, final BlockPos pos, @Nullable final BlockHitResult hitResult) {
        if (!(this.fluid instanceof BaseFluid)) {
            return false;
        }
        final BlockState blockState5 = world.getBlockState(pos);
        final Material material6 = blockState5.getMaterial();
        final boolean boolean7 = !material6.isSolid();
        final boolean boolean8 = material6.isReplaceable();
        if (world.isAir(pos) || boolean7 || boolean8 || (blockState5.getBlock() instanceof FluidFillable && ((FluidFillable)blockState5.getBlock()).canFillWithFluid(world, pos, blockState5, this.fluid))) {
            if (world.dimension.doesWaterVaporize() && this.fluid.matches(FluidTags.a)) {
                final int integer9 = pos.getX();
                final int integer10 = pos.getY();
                final int integer11 = pos.getZ();
                world.playSound(player, pos, SoundEvents.dq, SoundCategory.e, 0.5f, 2.6f + (world.random.nextFloat() - world.random.nextFloat()) * 0.8f);
                for (int integer12 = 0; integer12 < 8; ++integer12) {
                    world.addParticle(ParticleTypes.J, integer9 + Math.random(), integer10 + Math.random(), integer11 + Math.random(), 0.0, 0.0, 0.0);
                }
            }
            else if (blockState5.getBlock() instanceof FluidFillable && this.fluid == Fluids.WATER) {
                if (((FluidFillable)blockState5.getBlock()).tryFillWithFluid(world, pos, blockState5, ((BaseFluid)this.fluid).getStill(false))) {
                    this.playEmptyingSound(player, world, pos);
                }
            }
            else {
                if (!world.isClient && (boolean7 || boolean8) && !material6.isLiquid()) {
                    world.breakBlock(pos, true);
                }
                this.playEmptyingSound(player, world, pos);
                world.setBlockState(pos, this.fluid.getDefaultState().getBlockState(), 11);
            }
            return true;
        }
        return hitResult != null && this.placeFluid(player, world, hitResult.getBlockPos().offset(hitResult.getSide()), null);
    }
    
    protected void playEmptyingSound(@Nullable final PlayerEntity player, final IWorld world, final BlockPos pos) {
        final SoundEvent soundEvent4 = this.fluid.matches(FluidTags.b) ? SoundEvents.ay : SoundEvents.aw;
        world.playSound(player, pos, soundEvent4, SoundCategory.e, 1.0f, 1.0f);
    }
}
