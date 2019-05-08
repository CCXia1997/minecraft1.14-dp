package net.minecraft.block.entity;

import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.block.Blocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.EnvironmentInterface;
import net.fabricmc.api.EnvironmentInterfaces;
import net.minecraft.util.Tickable;
import net.minecraft.client.block.ChestAnimationProgress;

@EnvironmentInterfaces({ @EnvironmentInterface(value = EnvType.CLIENT, itf = buf.class) })
public class EnderChestBlockEntity extends BlockEntity implements ChestAnimationProgress, Tickable
{
    public float animationProgress;
    public float lastAnimationProgress;
    public int viewerCount;
    private int ticks;
    
    public EnderChestBlockEntity() {
        super(BlockEntityType.ENDER_CHEST);
    }
    
    @Override
    public void tick() {
        if (++this.ticks % 20 * 4 == 0) {
            this.world.addBlockAction(this.pos, Blocks.ec, 1, this.viewerCount);
        }
        this.lastAnimationProgress = this.animationProgress;
        final int integer1 = this.pos.getX();
        final int integer2 = this.pos.getY();
        final int integer3 = this.pos.getZ();
        final float float4 = 0.1f;
        if (this.viewerCount > 0 && this.animationProgress == 0.0f) {
            final double double5 = integer1 + 0.5;
            final double double6 = integer3 + 0.5;
            this.world.playSound(null, double5, integer2 + 0.5, double6, SoundEvents.cv, SoundCategory.e, 0.5f, this.world.random.nextFloat() * 0.1f + 0.9f);
        }
        if ((this.viewerCount == 0 && this.animationProgress > 0.0f) || (this.viewerCount > 0 && this.animationProgress < 1.0f)) {
            final float float5 = this.animationProgress;
            if (this.viewerCount > 0) {
                this.animationProgress += 0.1f;
            }
            else {
                this.animationProgress -= 0.1f;
            }
            if (this.animationProgress > 1.0f) {
                this.animationProgress = 1.0f;
            }
            final float float6 = 0.5f;
            if (this.animationProgress < 0.5f && float5 >= 0.5f) {
                final double double6 = integer1 + 0.5;
                final double double7 = integer3 + 0.5;
                this.world.playSound(null, double6, integer2 + 0.5, double7, SoundEvents.cu, SoundCategory.e, 0.5f, this.world.random.nextFloat() * 0.1f + 0.9f);
            }
            if (this.animationProgress < 0.0f) {
                this.animationProgress = 0.0f;
            }
        }
    }
    
    @Override
    public boolean onBlockAction(final int integer1, final int integer2) {
        if (integer1 == 1) {
            this.viewerCount = integer2;
            return true;
        }
        return super.onBlockAction(integer1, integer2);
    }
    
    @Override
    public void invalidate() {
        this.resetBlock();
        super.invalidate();
    }
    
    public void onOpen() {
        ++this.viewerCount;
        this.world.addBlockAction(this.pos, Blocks.ec, 1, this.viewerCount);
    }
    
    public void onClose() {
        --this.viewerCount;
        this.world.addBlockAction(this.pos, Blocks.ec, 1, this.viewerCount);
    }
    
    public boolean canPlayerUse(final PlayerEntity playerEntity) {
        return this.world.getBlockEntity(this.pos) == this && playerEntity.squaredDistanceTo(this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5) <= 64.0;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public float getAnimationProgress(final float float1) {
        return MathHelper.lerp(float1, this.lastAnimationProgress, this.animationProgress);
    }
}
