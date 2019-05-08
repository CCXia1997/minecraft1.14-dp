package net.minecraft.block.entity;

import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.EntityTags;
import net.minecraft.world.World;
import java.util.Iterator;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.LivingEntity;
import java.util.List;
import net.minecraft.util.math.Direction;
import net.minecraft.util.Tickable;

public class BellBlockEntity extends BlockEntity implements Tickable
{
    private long g;
    public int ringTicks;
    public boolean isRinging;
    public Direction lastSideHit;
    private List<LivingEntity> h;
    private boolean i;
    private int j;
    
    public BellBlockEntity() {
        super(BlockEntityType.BELL);
    }
    
    @Override
    public boolean onBlockAction(final int integer1, final int integer2) {
        if (integer1 == 1) {
            this.c();
            this.j = 0;
            this.lastSideHit = Direction.byId(integer2);
            this.ringTicks = 0;
            return this.isRinging = true;
        }
        return super.onBlockAction(integer1, integer2);
    }
    
    @Override
    public void tick() {
        if (this.isRinging) {
            ++this.ringTicks;
        }
        final BlockPos blockPos1 = this.getPos();
        if (this.ringTicks >= 50) {
            this.isRinging = false;
            this.ringTicks = 0;
        }
        if (this.ringTicks >= 5 && this.j == 0) {
            this.a(this.world, blockPos1);
        }
        if (this.i) {
            if (this.j < 40) {
                ++this.j;
            }
            else {
                this.i = false;
                this.b(this.world, blockPos1);
            }
        }
    }
    
    public void activate(final Direction direction) {
        final BlockPos blockPos2 = this.getPos();
        this.lastSideHit = direction;
        if (this.isRinging) {
            this.ringTicks = 0;
        }
        else {
            this.isRinging = true;
        }
        this.world.addBlockAction(blockPos2, this.getCachedState().getBlock(), 1, direction.getId());
    }
    
    private void c() {
        final BlockPos blockPos1 = this.getPos();
        if (this.world.getTime() > this.g + 60L || this.h == null) {
            this.g = this.world.getTime();
            final BoundingBox boundingBox2 = new BoundingBox(blockPos1).expand(48.0);
            this.h = this.world.<LivingEntity>getEntities(LivingEntity.class, boundingBox2);
        }
        if (!this.world.isClient) {
            for (final LivingEntity livingEntity3 : this.h) {
                if (livingEntity3.isAlive()) {
                    if (livingEntity3.removed) {
                        continue;
                    }
                    if (!blockPos1.isWithinDistance(livingEntity3.getPos(), 32.0)) {
                        continue;
                    }
                    livingEntity3.getBrain().<Long>putMemory(MemoryModuleType.w, this.world.getTime());
                }
            }
        }
    }
    
    private void a(final World world, final BlockPos blockPos) {
        for (final LivingEntity livingEntity4 : this.h) {
            if (livingEntity4.isAlive()) {
                if (livingEntity4.removed) {
                    continue;
                }
                if (!blockPos.isWithinDistance(livingEntity4.getPos(), 32.0) || !livingEntity4.getType().isTaggedWith(EntityTags.b)) {
                    continue;
                }
                this.i = true;
            }
        }
        if (this.i) {
            world.playSound(null, blockPos, SoundEvents.Z, SoundCategory.e, 1.0f, 1.0f);
        }
    }
    
    private void b(final World world, final BlockPos blockPos) {
        int integer3 = 16700985;
        final int integer4 = (int)this.h.stream().filter(livingEntity -> blockPos.isWithinDistance(livingEntity.getPos(), 32.0)).count();
        for (final LivingEntity livingEntity2 : this.h) {
            if (livingEntity2.isAlive()) {
                if (livingEntity2.removed) {
                    continue;
                }
                if (!blockPos.isWithinDistance(livingEntity2.getPos(), 32.0) || !livingEntity2.getType().isTaggedWith(EntityTags.b)) {
                    continue;
                }
                if (!world.isClient) {
                    livingEntity2.addPotionEffect(new StatusEffectInstance(StatusEffects.x, 60));
                }
                else {
                    final float float7 = 1.0f;
                    final float float8 = MathHelper.sqrt((livingEntity2.x - blockPos.getX()) * (livingEntity2.x - blockPos.getX()) + (livingEntity2.z - blockPos.getZ()) * (livingEntity2.z - blockPos.getZ()));
                    final double double9 = blockPos.getX() + 0.5f + 1.0f / float8 * (livingEntity2.x - blockPos.getX());
                    final double double10 = blockPos.getZ() + 0.5f + 1.0f / float8 * (livingEntity2.z - blockPos.getZ());
                    for (int integer5 = MathHelper.clamp((integer4 - 21) / -2, 3, 15), integer6 = 0; integer6 < integer5; ++integer6) {
                        integer3 += 5;
                        final double double11 = (integer3 >> 16 & 0xFF) / 255.0;
                        final double double12 = (integer3 >> 8 & 0xFF) / 255.0;
                        final double double13 = (integer3 & 0xFF) / 255.0;
                        world.addParticle(ParticleTypes.u, double9, blockPos.getY() + 0.5f, double10, double11, double12, double13);
                    }
                }
            }
        }
    }
}
