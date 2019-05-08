package net.minecraft.block;

import net.minecraft.entity.EntityType;
import net.minecraft.world.BlockView;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.world.ViewableWorld;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.ExtendedBlockView;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MagmaBlock extends Block
{
    public MagmaBlock(final Settings settings) {
        super(settings);
    }
    
    @Override
    public void onSteppedOn(final World world, final BlockPos pos, final Entity entity) {
        if (!entity.isFireImmune() && entity instanceof LivingEntity && !EnchantmentHelper.hasFrostWalker((LivingEntity)entity)) {
            entity.damage(DamageSource.HOT_FLOOR, 1.0f);
        }
        super.onSteppedOn(world, pos, entity);
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public int getBlockBrightness(final BlockState state, final ExtendedBlockView view, final BlockPos pos) {
        return 15728880;
    }
    
    @Override
    public void onScheduledTick(final BlockState state, final World world, final BlockPos pos, final Random random) {
        BubbleColumnBlock.update(world, pos.up(), true);
    }
    
    @Override
    public BlockState getStateForNeighborUpdate(final BlockState state, final Direction facing, final BlockState neighborState, final IWorld world, final BlockPos pos, final BlockPos neighborPos) {
        if (facing == Direction.UP && neighborState.getBlock() == Blocks.A) {
            world.getBlockTickScheduler().schedule(pos, this, this.getTickRate(world));
        }
        return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
    }
    
    @Override
    public void onRandomTick(final BlockState state, final World world, final BlockPos pos, final Random random) {
        final BlockPos blockPos5 = pos.up();
        if (world.getFluidState(pos).matches(FluidTags.a)) {
            world.playSound(null, pos, SoundEvents.dq, SoundCategory.e, 0.5f, 2.6f + (world.random.nextFloat() - world.random.nextFloat()) * 0.8f);
            if (world instanceof ServerWorld) {
                ((ServerWorld)world).<DefaultParticleType>spawnParticles(ParticleTypes.J, blockPos5.getX() + 0.5, blockPos5.getY() + 0.25, blockPos5.getZ() + 0.5, 8, 0.5, 0.25, 0.5, 0.0);
            }
        }
    }
    
    @Override
    public int getTickRate(final ViewableWorld world) {
        return 20;
    }
    
    @Override
    public void onBlockAdded(final BlockState state, final World world, final BlockPos pos, final BlockState oldState, final boolean boolean5) {
        world.getBlockTickScheduler().schedule(pos, this, this.getTickRate(world));
    }
    
    @Override
    public boolean allowsSpawning(final BlockState state, final BlockView blockView, final BlockPos blockPos, final EntityType<?> entityType) {
        return entityType.isFireImmune();
    }
    
    @Override
    public boolean shouldPostProcess(final BlockState state, final BlockView view, final BlockPos pos) {
        return true;
    }
}
