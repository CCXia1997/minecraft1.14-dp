package net.minecraft.block;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.Difficulty;
import net.minecraft.entity.Entity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.entity.VerticalEntityPosition;
import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.entity.effect.StatusEffect;

public class WitherRoseBlock extends FlowerBlock
{
    public WitherRoseBlock(final StatusEffect statusEffect, final Settings settings) {
        super(statusEffect, 8, settings);
    }
    
    @Override
    protected boolean canPlantOnTop(final BlockState floor, final BlockView view, final BlockPos pos) {
        final Block block4 = floor.getBlock();
        return super.canPlantOnTop(floor, view, pos) || block4 == Blocks.cJ || block4 == Blocks.cK;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void randomDisplayTick(final BlockState state, final World world, final BlockPos pos, final Random rnd) {
        final VoxelShape voxelShape5 = this.getOutlineShape(state, world, pos, VerticalEntityPosition.minValue());
        final Vec3d vec3d6 = voxelShape5.getBoundingBox().getCenter();
        final double double7 = pos.getX() + vec3d6.x;
        final double double8 = pos.getZ() + vec3d6.z;
        for (int integer11 = 0; integer11 < 3; ++integer11) {
            if (rnd.nextBoolean()) {
                world.addParticle(ParticleTypes.Q, double7 + rnd.nextFloat() / 5.0f, pos.getY() + (0.5 - rnd.nextFloat()), double8 + rnd.nextFloat() / 5.0f, 0.0, 0.0, 0.0);
            }
        }
    }
    
    @Override
    public void onEntityCollision(final BlockState state, final World world, final BlockPos pos, final Entity entity) {
        if (world.isClient || world.getDifficulty() == Difficulty.PEACEFUL) {
            return;
        }
        if (entity instanceof LivingEntity) {
            final LivingEntity livingEntity5 = (LivingEntity)entity;
            if (!livingEntity5.isInvulnerableTo(DamageSource.WITHER)) {
                livingEntity5.addPotionEffect(new StatusEffectInstance(StatusEffects.t, 40));
            }
        }
    }
}
