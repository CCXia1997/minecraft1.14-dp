package net.minecraft.entity.passive;

import net.minecraft.entity.ai.TargetPredicate;
import java.util.EnumSet;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.SpawnType;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.IWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.nbt.CompoundTag;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;

public class TraderLlamaEntity extends LlamaEntity
{
    private int despawnDelay;
    
    public TraderLlamaEntity(final EntityType<? extends TraderLlamaEntity> type, final World world) {
        super(type, world);
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public boolean isTrader() {
        return true;
    }
    
    @Override
    protected LlamaEntity createChild() {
        return EntityType.ax.create(this.world);
    }
    
    @Override
    public void writeCustomDataToTag(final CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putInt("DespawnDelay", this.despawnDelay);
    }
    
    @Override
    public void readCustomDataFromTag(final CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        if (tag.containsKey("DespawnDelay", 99)) {
            this.despawnDelay = tag.getInt("DespawnDelay");
        }
    }
    
    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(1, new EscapeDangerGoal(this, 2.0));
        this.targetSelector.add(1, new DefendTraderGoal(this));
    }
    
    public void setDespawnDelay(final int integer) {
        this.despawnDelay = integer;
    }
    
    @Override
    protected void putPlayerOnBack(final PlayerEntity playerEntity) {
        final Entity entity2 = this.getHoldingEntity();
        if (entity2 instanceof WanderingTraderEntity) {
            return;
        }
        super.putPlayerOnBack(playerEntity);
    }
    
    @Override
    public void tick() {
        super.tick();
        if (this.despawnDelay > 0 && --this.despawnDelay == 0 && this.getHoldingEntity() instanceof WanderingTraderEntity) {
            final WanderingTraderEntity wanderingTraderEntity1 = (WanderingTraderEntity)this.getHoldingEntity();
            final int integer2 = wanderingTraderEntity1.getDespawnDelay();
            if (integer2 - 1 > 0) {
                this.despawnDelay = integer2 - 1;
            }
            else {
                this.remove();
            }
        }
    }
    
    @Nullable
    @Override
    public EntityData initialize(final IWorld iWorld, final LocalDifficulty localDifficulty, final SpawnType difficulty, @Nullable final EntityData entityData, @Nullable final CompoundTag compoundTag) {
        final EntityData entityData2 = super.initialize(iWorld, localDifficulty, difficulty, entityData, compoundTag);
        if (difficulty == SpawnType.h) {
            this.setBreedingAge(0);
        }
        return entityData2;
    }
    
    public class DefendTraderGoal extends TrackTargetGoal
    {
        private final LlamaEntity b;
        private LivingEntity offender;
        private int traderLastAttackedTime;
        
        public DefendTraderGoal(final LlamaEntity llamaEntity) {
            super(llamaEntity, false);
            this.b = llamaEntity;
            this.setControls(EnumSet.<Control>of(Control.d));
        }
        
        @Override
        public boolean canStart() {
            if (!this.b.isLeashed()) {
                return false;
            }
            final Entity entity1 = this.b.getHoldingEntity();
            if (!(entity1 instanceof WanderingTraderEntity)) {
                return false;
            }
            final WanderingTraderEntity wanderingTraderEntity2 = (WanderingTraderEntity)entity1;
            this.offender = wanderingTraderEntity2.getAttacker();
            final int integer3 = wanderingTraderEntity2.getLastAttackedTime();
            return integer3 != this.traderLastAttackedTime && this.canTrack(this.offender, TargetPredicate.DEFAULT);
        }
        
        @Override
        public void start() {
            this.entity.setTarget(this.offender);
            final Entity entity1 = this.b.getHoldingEntity();
            if (entity1 instanceof WanderingTraderEntity) {
                this.traderLastAttackedTime = ((WanderingTraderEntity)entity1).getLastAttackedTime();
            }
            super.start();
        }
    }
}
